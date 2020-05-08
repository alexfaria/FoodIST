package pt.ulisboa.tecnico.cmov.foodist.repository.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "dish_table", primaryKeys = {"name", "foodServiceName"})
public class DishDBEntity {

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "cost")
    private float cost;

    @ColumnInfo(name = "category")
    private int category;

    @ColumnInfo(name = "rating")
    private float rating;

    @ColumnInfo(name = "number_of_photos")
    private int numberOfPhotos;

    @NonNull
    @ForeignKey(entity = FoodServiceDBEntity.class, parentColumns = "name", childColumns = "foodServiceName")
    private String foodServiceName;

    public DishDBEntity(@NonNull String name, float cost, int category, float rating, int numberOfPhotos, @NonNull String foodServiceName) {
        this.name = name;
        this.cost = cost;
        this.category = category;
        this.rating = rating;
        this.numberOfPhotos = numberOfPhotos;
        this.foodServiceName = foodServiceName;
    }

    public String getName() {
        return name;
    }

    public float getCost() {
        return cost;
    }

    public int getCategory() {
        return category;
    }

    public float getRating() {
        return rating;
    }

    public int getNumberOfPhotos() {
        return numberOfPhotos;
    }

    @NonNull
    public String getFoodServiceName() {
        return foodServiceName;
    }
}
