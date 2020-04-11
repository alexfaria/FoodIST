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

    @ForeignKey(entity = FoodServiceDBEntity.class, parentColumns = "name", childColumns = "foodServiceName")
    private String foodServiceName;

    public DishDBEntity(@NonNull String name, float cost, String foodServiceName) {
        this.name = name;
        this.cost = cost;
        this.foodServiceName = foodServiceName;
    }

    public String getName() {
        return name;
    }

    public float getCost() {
        return cost;
    }

    public String getFoodServiceName() {
        return foodServiceName;
    }
}
