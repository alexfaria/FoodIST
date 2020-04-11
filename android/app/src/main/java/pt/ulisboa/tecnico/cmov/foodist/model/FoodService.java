package pt.ulisboa.tecnico.cmov.foodist.model;

import java.util.List;

public class FoodService {

    private String name;
    private String openingHours;
    private double latitude;
    private double longitude;
    private int walkTime;
    private int queueTime;

    public FoodService(String name, String openingHours, double latitude, double longitude) {
        this.name = name;
        this.openingHours = openingHours;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getOpeningHours() {
        return openingHours;
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

    public int getQueueTime() {
        return queueTime;
    }

    public void setQueueTime(int queueTime) {
        this.queueTime = queueTime;
    }
}
