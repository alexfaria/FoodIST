package pt.ulisboa.tecnico.cmov.foodist.model;

import java.util.List;

public class FoodService {

    private String name;
    private List<String> openingHours;
    private float rating;
    private List<Integer> categories;
    private String beaconName;
    private int queueTime;
    private double latitude;
    private double longitude;
    private int walkTime;

    public FoodService(String name, List<String> openingHours, float rating, List<Integer> categories, int queueTime,double latitude, double longitude, String beaconName) {
        this.name = name;
        this.openingHours = openingHours;
        this.rating = rating;
        this.categories = categories;
        this.beaconName = beaconName;
        this.queueTime = queueTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.walkTime = 0;
    }

    public String getName() {
        return name;
    }

    public List<String> getOpeningHours() {
        return openingHours;
    }

    public String getOpeningHoursString() {
        StringBuilder str = new StringBuilder();
        for(String hours : openingHours)
            str.append(hours).append(", ");
        return str.length() > 0 ? str.substring(0, str.length()-2) : str.toString();
    }

    public float getRating() {
        return rating;
    }

    public List<Integer> getCategories() {
        return categories;
    }

    public String getBeaconName() {
        return beaconName;
    }

    public int getQueueTime() {
        return queueTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getWalkTime() {
        return walkTime;
    }

    public void setWalkTime(int walkTime) {
        this.walkTime = walkTime;
    }
}
