package model;

import com.google.protobuf.ByteString;

import java.util.ArrayList;
import java.util.List;

public class Dish {

    private String name;
    private float cost;
    private List<ByteString> photos;

    public Dish(String name, float cost) {
        this.name = name;
        this.cost = cost;
        photos = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public float getCost() {
        return cost;
    }

    public boolean hasPhotos() { return photos.size() > 0; }

    public List<ByteString> getPhotos() {
        return photos;
    }

    public void addPhoto(ByteString photo) {
        photos.add(photo);
    }
}
