package pt.ulisboa.tecnico.cmov.foodist.repository.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

@Entity(tableName = "food_service_table", primaryKeys = {"name", "campus"})
public class FoodServiceDBEntity {

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @NonNull
    @ColumnInfo(name = "campus")
    private String campus;

    @NonNull
    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "opening_hours")
    private List<String> openingHours;

    @ColumnInfo(name = "rating")
    private float rating;

    @ColumnInfo(name = "categories")
    private List<Integer> categories;

    @ColumnInfo(name = "queue_time")
    private int queueTime;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    public FoodServiceDBEntity(@NonNull String name, @NonNull String campus, @NonNull String status, List<String> openingHours, float rating, List<Integer> categories, int queueTime, double latitude, double longitude) {
        this.name = name;
        this.campus = campus;
        this.status = status;
        this.openingHours = openingHours;
        this.rating = rating;
        this.categories = categories;
        this.queueTime = queueTime;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getCampus() {
        return campus;
    }

    @NonNull
    public String getStatus() { return status; }

    public List<String> getOpeningHours() {
        return openingHours;
    }

    public float getRating() {
        return rating;
    }

    public List<Integer> getCategories() {
        return categories;
    }

    public int getQueueTime() {
        return queueTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isOpen() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        for(String openingHour : openingHours) {
            String[] range = openingHour.split("-");
            if (range[0].charAt(0) == '0')
                range[0] = range[0].substring(1);
            int open = Integer.parseInt(range[0].replace(":", ""));
            int close = Integer.parseInt(range[1].replace(":", ""));
            // int current = Integer.parseInt(formatter.format(new Date()).replace(":", "")); // Comment this line to ignore current time
            int current = 1430; // Uncomment this line if you want to ignore current time
            if (open <= current && current < close)
                return true;
        }
        return false;
    }
}
