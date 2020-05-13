package pt.ulisboa.tecnico.cmov.foodist.model;

public class Beacon {

    private String beaconName;
    private String foodServiceName;


    public Beacon(String beaconName, String foodServiceName) {
        this.beaconName = beaconName;
        this.foodServiceName = foodServiceName;
    }

    public String getBeaconName() {
        return beaconName;
    }

    public String getFoodServiceName() {
        return foodServiceName;
    }
}
