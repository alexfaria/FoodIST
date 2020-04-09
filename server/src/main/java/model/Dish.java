package model;

import java.util.List;

public class Dish {

    private String name;
    private float cost;
    private List<byte[]> photos;

    public Dish(String name, float cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public float getCost() {
        return cost;
    }

    public List<byte[]> getPhotos() {
        return photos;
    }

    public void addPhoto(byte[] photo) {
        photos.add(photo);
    }
}
