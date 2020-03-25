package pt.ulisboa.tecnico.cmov.foodist.model;

import android.graphics.Bitmap;

import java.util.List;

public class Dish {

    private String name;
    private int cost;
    private List<Bitmap> photos;

    public Dish(String name, int cost, List<Bitmap> photos) {
        this.name = name;
        this.cost = cost;
        this.photos = photos;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public List<Bitmap> getPhotos() {
        return photos;
    }
}
