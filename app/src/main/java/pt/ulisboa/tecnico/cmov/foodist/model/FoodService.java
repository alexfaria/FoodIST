package pt.ulisboa.tecnico.cmov.foodist.model;

import java.util.List;

public class FoodService {

    private String name;
    private int walkTime;
    private int queueTime;
    private String openingHours;
    private List<Dish> menu;

    public FoodService(String name, int walkTime, int queueTime, String openingHours, List<Dish> menu) {
        this.name = name;
        this.walkTime = walkTime;
        this.queueTime = queueTime;
        this.openingHours = openingHours;
        this.menu = menu;
    }

    public String getName() {
        return name;
    }

    public int getWalkTime() {
        return walkTime;
    }

    public int getQueueTime() {
        return queueTime;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public List<Dish> getMenu() {
        return menu;
    }

}
