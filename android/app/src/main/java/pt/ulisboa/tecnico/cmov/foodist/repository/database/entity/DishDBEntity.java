package pt.ulisboa.tecnico.cmov.foodist.repository.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "dish_table")
public class DishDBEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "cost")
    private float cost;

    @ColumnInfo(name = "number_of_photos")
    private int numberOfPhotos;

    @ForeignKey(entity = FoodServiceDBEntity.class, parentColumns = "name", childColumns = "foodServiceName")
    private String foodServiceName;

    public DishDBEntity(@NonNull String name, float cost, int numberOfPhotos, String foodServiceName) {
        this.name = name;
        this.cost = cost;
        this.numberOfPhotos = numberOfPhotos;
        this.foodServiceName = foodServiceName;
    }

    public String getName() {
        return name;
    }

    public float getCost() {
        return cost;
    }

    public int getNumberOfPhotos() { return numberOfPhotos; }

    public String getFoodServiceName() {
        return foodServiceName;
    }

}
