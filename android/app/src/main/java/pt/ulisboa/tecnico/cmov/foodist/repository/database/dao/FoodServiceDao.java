package pt.ulisboa.tecnico.cmov.foodist.repository.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.repository.database.entity.FoodMenuDBEntity;
import pt.ulisboa.tecnico.cmov.foodist.repository.database.entity.FoodServiceDBEntity;

@Dao
public interface FoodServiceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FoodServiceDBEntity fs);

    @Query("select * from food_service_table where campus = :campus")
    LiveData<List<FoodServiceDBEntity>> getAll(String campus);

    @Query("select * from food_service_table where campus = :campus and name = :name")
    LiveData<FoodServiceDBEntity> get(String campus, String name);

    @Transaction
    @Query("select * from food_service_table where campus = :campus")
    LiveData<FoodMenuDBEntity> getMenu(String campus);
}
