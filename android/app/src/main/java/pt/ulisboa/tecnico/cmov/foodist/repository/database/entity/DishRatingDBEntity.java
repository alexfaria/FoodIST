package pt.ulisboa.tecnico.cmov.foodist.repository.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "dish_rating_table")
public class DishRatingDBEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "uuid")
    private String uuid;

    @ColumnInfo(name = "rating")
    private float rating;

    @ForeignKey(entity = FoodServiceDBEntity.class, parentColumns = "name", childColumns = "foodServiceName")
    private String foodServiceName;

    @ForeignKey(entity = DishDBEntity.class, parentColumns = "name", childColumns = "dishName")
    private String dishName;

    public DishRatingDBEntity(@NonNull String uuid, float rating, String foodServiceName, String dishName) {
        this.uuid = uuid;
        this.rating = rating;
        this.foodServiceName = foodServiceName;
        this.dishName = dishName;
    }

    @NonNull
    public String getUuid() {
        return uuid;
    }

    public float getRating() {
        return rating;
    }

    public String getFoodServiceName() {
        return foodServiceName;
    }

    public String getDishName() {
        return dishName;
    }
}
