package pt.ulisboa.tecnico.cmov.foodist.model;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Dish {

    private String name;
    private float cost;
    private int category;
    private float averageRating;
    private Map<String, Float> ratings;
    private int numberOfPhotos;
    private List<Bitmap> photos;

    public Dish(String name, float cost, int category, int numberOfPhotos) {
        this.name = name;
        this.cost = cost;
        this.category = category;
        this.averageRating = 0;
        this.ratings = new HashMap<>();
        this.numberOfPhotos = numberOfPhotos;
        this.photos = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getCategory() {
        return category;
    }

    public float getCost() {
        return cost;
    }

    public Map<String, Float> getRatings() {
        return ratings;
    }

    public void setRatings(Map<String, Float> ratings) {
        this.ratings = ratings;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }
    public float getAverageRating() {
        return averageRating;
    }

    public List<Bitmap> getPhotos() {
        return photos;
    }

    public int getNumberOfPhotos() {
        return numberOfPhotos;
    }

    public Bitmap getPhoto(int index) {
        if (index < photos.size())
            return photos.get(index);
        return null;
    }

    public void addPhoto(Bitmap photo) {
        photos.add(photo);
    }

    public static String generateKeyFrom(String foodServiceName, String dishName) {
        String key = String.format(Locale.getDefault(), "%s_%s_", foodServiceName, dishName);
        return key.replaceAll("[ /]", "").toLowerCase();
    }
}
