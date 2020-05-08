package model;

import edu.princeton.cs.algs4.LinearRegression;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class FoodService {

    private String name;
    private String campus;
    private Map<String, List<OpeningHours>> openingHours;
    private double latitude;
    private double longitude;

    private HashMap<String, Dish> menu;

    private String beaconName;
    // TODO: use Tuple<LocalTime, Integer> as a value
    private HashMap<String, LocalTime> queueArrivalTime; // UUID -> ArrivalTime
    private HashMap<String, Integer> queueClientCount; // UUID -> other clients in queue on arrival
    private ArrayList<Double> LRx; // time difference between arrival time and departure
    private ArrayList<Double> LRy; // how many other clients were present upon arrival

    public FoodService(String name, String campus, Map<String, List<OpeningHours>> openingHours, double latitude, double longitude, String beaconName) {
        this.name = name;
        this.campus = campus;
        this.openingHours = openingHours;
        this.latitude = latitude;
        this.longitude = longitude;

        this.menu = new HashMap<>();

        this.beaconName = beaconName;
        this.queueArrivalTime = new HashMap<>();
        this.queueClientCount = new HashMap<>();

        this.LRx = new ArrayList<>();
        this.LRy = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getCampus() {
        return campus;
    }

    public List<String> getOpeningHours(String status) {
        return openingHours.get(status).stream().map(OpeningHours::toString).collect(Collectors.toList());
    }

    public String getBeaconName() {
        return beaconName;
    }

    public boolean isAvailable(String status, LocalTime current) {
        for (OpeningHours hours : openingHours.get(status))
            if (hours.isAvailable(current))
                return true;
        return false;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public HashMap<String, Dish> getMenu() {
        return menu;
    }

    public boolean addMenuItem(Dish dish) {
        if (!menu.containsKey(dish.getName())) {
            menu.put(dish.getName(), dish);
            return true;
        }
        return false;
    }

    public float getRating() {
        float totalOfRatings = 0;
        int numOfRatings = 0;
        for (Dish dish : menu.values()) {
            for (Float rating : dish.getRatings().values()) {
                totalOfRatings += rating;
            }
            numOfRatings += dish.getRatings().size();
        }
        return numOfRatings == 0 ? 0 : totalOfRatings / numOfRatings;
    }

    public List<Integer> getCategories() {
        List<Integer> categories = new ArrayList<>();
        menu.values().forEach(dish -> {
            int category = dish.getCategory();
            if (!categories.contains(category))
                categories.add(category);
        });
        return categories;
    }

    public void addToQueue(String UUID) {
        queueArrivalTime.put(UUID, LocalTime.now());
        queueClientCount.put(UUID, queueArrivalTime.size() - 1);
    }

    public void removeFromQueue(String UUID) {
        int queueSizeOnArrival = queueClientCount.remove(UUID);
        LocalTime arrivalTime = queueArrivalTime.remove(UUID);

        Duration duration = Duration.between(LocalTime.now(), arrivalTime);

        LRx.add((double) duration.getSeconds());
        LRy.add((double) queueSizeOnArrival);
    }

    public int getQueueWaitTime() {
        // seconds
        double[] lrx = new double[LRx.size()];
        Iterator<Double> iterator = LRx.iterator();
        for (int i = 0; i < lrx.length; i++) {
            lrx[i] = iterator.next();
        }

        double[] lry = new double[LRy.size()];
        iterator = LRy.iterator();
        for (int i = 0; i < lry.length; i++) {
            lry[i] = iterator.next();
        }

        LinearRegression lr = new LinearRegression(lrx, lry);

        // minutes
        return (int) lr.predict(queueClientCount.size()) / 60;
    }
}
