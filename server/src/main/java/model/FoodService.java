package model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FoodService {

    private String name;
    private String campus;
    private String openingHours;
    private double latitude;
    private double longitude;

    private HashMap<String, Dish> menu;

    public FoodService(String name, String campus, String openingHours, double latitude, double longitude) {
        this.name = name;
        this.campus = campus;
        this.openingHours = openingHours;
        this.latitude = latitude;
        this.longitude = longitude;

        this.menu = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public String getCampus() { return campus; }

    public String getOpeningHours() {
        return openingHours;
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
}
