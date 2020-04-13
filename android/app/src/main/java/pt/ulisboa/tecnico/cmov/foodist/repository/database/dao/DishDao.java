package pt.ulisboa.tecnico.cmov.foodist.repository.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.repository.database.entity.DishDBEntity;

@Dao
public interface DishDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DishDBEntity dishDBEntity);

    @Query("select * from dish_table where foodServiceName = :foodServiceName")
    LiveData<List<DishDBEntity>> getAll(String foodServiceName);

    @Query("select * from dish_table where foodServiceName = :foodServiceName and name = :name")
    LiveData<DishDBEntity> get(String foodServiceName, String name);

    @Query("update dish_table set number_of_photos=:numberOfPhotos where foodServiceName=:foodServiceName and name = :name")
    void updateNumberOfPhotos(String foodServiceName, String name, int numberOfPhotos);
}
