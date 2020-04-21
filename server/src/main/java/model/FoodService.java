package model;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodService {

    private String name;
    private String campus;
    private Map<String, OpeningHours> openingHours;
    private double latitude;
    private double longitude;

    private HashMap<String, Dish> menu;

    public FoodService(String name, String campus, Map<String, OpeningHours> openingHours, double latitude, double longitude) {
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

    public OpeningHours getOpeningHours(String status) {
        return openingHours.get(status);
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
