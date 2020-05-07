package pt.ulisboa.tecnico.cmov.foodist.repository.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "dish_rating_table", primaryKeys = {"uuid", "foodServiceName", "dishName"})
public class DishRatingDBEntity {

    @NonNull
    @ColumnInfo(name = "uuid")
    private String uuid;

    @ColumnInfo(name = "rating")
    private float rating;

    @NonNull
    @ForeignKey(entity = FoodServiceDBEntity.class, parentColumns = "name", childColumns = "foodServiceName")
    private String foodServiceName;

    @NonNull
    @ForeignKey(entity = DishDBEntity.class, parentColumns = "name", childColumns = "dishName")
    private String dishName;

    public DishRatingDBEntity(@NonNull String uuid, float rating, @NonNull String foodServiceName, @NonNull String dishName) {
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

    @NonNull
    public String getFoodServiceName() {
        return foodServiceName;
    }

    @NonNull
    public String getDishName() {
        return dishName;
    }
}
