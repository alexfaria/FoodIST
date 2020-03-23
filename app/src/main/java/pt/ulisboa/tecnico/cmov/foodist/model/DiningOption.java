package pt.ulisboa.tecnico.cmov.foodist.model;

public class DiningOption {

    private String name;
    private String openingHours;

    public DiningOption(String name, String openingHours) {
        this.name = name;
        this.openingHours = openingHours;
    }

    public String getName() {
        return name;
    }

    public String getOpeningHours() {
        return openingHours;
    }
}
