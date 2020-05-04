package model;

import com.google.protobuf.ByteString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dish {
    private String name;
    private float cost;
    private int category;
    private Map<String, Float> ratings;
    private List<ByteString> photos;

    public Dish(String name, float cost, int category) {
        this.name = name;
        this.cost = cost;
        this.category = category;
        ratings = new HashMap<>();
        photos = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public float getCost() {
        return cost;
    }

    public int getCategory() {
        return this.category;
    }

    public Map<String, Float> getRatings() {
        return ratings;
    }

    public void addRating(String uuid, float rating) {
        ratings.put(uuid, rating);
    }

    public float getAverageRating() {
        if (ratings.size() == 0) return 0;

        float sum = 0;
        for (float r : ratings.values()) {
            sum += r;
        }
        return sum / ratings.size();
    }

    public boolean hasPhotos() {
        return photos.size() > 0;
    }

    public int getNumberOfPhotos() {
        return photos.size();
    }

    public List<ByteString> getPhotos() {
        return photos;
    }

    public synchronized int addPhoto(ByteString photo) {
        photos.add(photo);
        return photos.size() - 1;
    }
}
