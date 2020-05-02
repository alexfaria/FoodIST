package pt.ulisboa.tecnico.cmov.foodist.model;

public class FoodService {

    private String name;
    private String openingHours;
    private float rating;
    private int queueTime;
    private double latitude;
    private double longitude;
    private int walkTime;

    public FoodService(String name, String openingHours, float rating, int queueTime,double latitude, double longitude) {
        this.name = name;
        this.openingHours = openingHours;
        this.rating = rating;
        this.queueTime = queueTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.walkTime = 0;
    }

    public String getName() {
        return name;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public float getRating() {
        return rating;
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
