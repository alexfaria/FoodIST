package pt.ulisboa.tecnico.cmov.foodist.repository.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "food_service_table")
public class FoodServiceDBEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @NonNull
    @ColumnInfo(name = "campus")
    private String campus;

    @ColumnInfo(name = "opening_hours")
    private String openingHours;

    @ColumnInfo(name = "queue_time")
    private int queueTime;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    public FoodServiceDBEntity(@NonNull String name, @NonNull String campus, String openingHours, int queueTime, double latitude, double longitude) {
        this.name = name;
        this.campus = campus;
        this.openingHours = openingHours;
        this.queueTime = queueTime;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getCampus() {
        return campus;
    }

    public String getOpeningHours() {
        return openingHours;
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
}
