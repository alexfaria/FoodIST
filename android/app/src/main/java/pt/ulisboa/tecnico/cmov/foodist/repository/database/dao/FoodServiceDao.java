package pt.ulisboa.tecnico.cmov.foodist.repository.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.repository.database.entity.FoodServiceDBEntity;

@Dao
public interface FoodServiceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FoodServiceDBEntity foodService);

    @Query("select * from food_service_table where campus = :campus and status = :status")
    LiveData<List<FoodServiceDBEntity>> getAll(String campus, String status);

    @Query("select * from food_service_table where campus = :campus and status = :status and name = :name")
    LiveData<FoodServiceDBEntity> get(String campus, String status, String name);
}
