package pt.ulisboa.tecnico.cmov.foodist.model;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class Dish {

    private String name;
    private float cost;
    private int numberOfPhotos;
    private List<Bitmap> photos;

    public Dish(String name, float cost, int numberOfPhotos) {
        this.name = name;
        this.cost = cost;
        this.numberOfPhotos = numberOfPhotos;
        this.photos = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public float getCost() {
        return cost;
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

    public void addPhoto(Bitmap photo) { photos.add(photo); }
}
