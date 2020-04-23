package model;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FoodService {

    private String name;
    private String campus;
    private Map<String, OpeningHours> openingHours;
    private double latitude;
    private double longitude;

    private int queueCurrentSize;
    private float queueTimeCount;
    private long queueTimeTotal;
    private ArrayList<LocalTime> queueArrivalTimes;

    private HashMap<String, Dish> menu;

    public FoodService(String name, String campus, Map<String, OpeningHours> openingHours, double latitude, double longitude) {
        this.name = name;
        this.campus = campus;
        this.openingHours = openingHours;
        this.latitude = latitude;
        this.longitude = longitude;

        this.menu = new HashMap<>();

        this.queueCurrentSize = 0;
        this.queueTimeCount = 0;
        this.queueTimeTotal = 0;
        this.queueArrivalTimes = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getCampus() {
        return campus;
    }

    public OpeningHours getOpeningHours(String status) {
        return openingHours.get(status);
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

    public void addToQueue() {
        queueCurrentSize++;
        queueArrivalTimes.add(LocalTime.now());
    }

    public void removeFromQueue() {
        queueCurrentSize--;
        queueTimeCount++;

        LocalTime arrivalTime = queueArrivalTimes.remove(0);
        Duration duration = Duration.between(LocalTime.now(), arrivalTime);

        queueTimeTotal += duration.getSeconds();

        System.out.println("getAverageQueueTime: " + getAverageQueueTime());
    }

    public float getAverageQueueTime() {
        return queueTimeTotal / queueTimeCount;
    }
}
