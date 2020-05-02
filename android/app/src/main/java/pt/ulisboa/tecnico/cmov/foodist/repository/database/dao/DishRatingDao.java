package pt.ulisboa.tecnico.cmov.foodist.repository.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.repository.database.entity.DishRatingDBEntity;

@Dao
public interface DishRatingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DishRatingDBEntity ratingDBEntity);

    @Query("select * from dish_rating_table where foodServiceName = :foodServiceName and dishName = :dishName")
    LiveData<List<DishRatingDBEntity>> getAll(String foodServiceName, String dishName);

    @Query("select * from dish_rating_table where uuid = :uuid and foodServiceName = :foodServiceName and dishName = :dishName")
    LiveData<DishRatingDBEntity> get(String uuid, String foodServiceName, String dishName);
}
