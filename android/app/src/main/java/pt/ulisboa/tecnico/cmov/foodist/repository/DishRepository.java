package pt.ulisboa.tecnico.cmov.foodist.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.model.Dish;
import pt.ulisboa.tecnico.cmov.foodist.repository.database.FoodRoomDatabase;
import pt.ulisboa.tecnico.cmov.foodist.repository.database.dao.DishDao;
import pt.ulisboa.tecnico.cmov.foodist.repository.database.entity.DishDBEntity;
import pt.ulisboa.tecnico.cmov.foodist.repository.server.FoodServer;
import pt.ulisboa.tecnico.cmov.foodist.view.App;

public class DishRepository {

    private DishDao dishDao;
    private FoodServer foodServer;

    public DishRepository(App application) {
        FoodRoomDatabase db = FoodRoomDatabase.getDatabase(application);
        dishDao = db.dishDao();
        foodServer = application.getServer();
    }

    public LiveData<List<Dish>> getDishes(String foodServiceName) {
        MutableLiveData<List<Dish>> ld = new MutableLiveData<>();
        dishDao.getAll(foodServiceName).observeForever(dishDB -> {
            List<Dish> foodServices = new ArrayList<>();
            if (dishDB != null) {
                for(DishDBEntity d : dishDB) {
                    foodServices.add(
                            new Dish(d.getName(), d.getCost())
                    );
                }
            }
            ld.postValue(foodServices);
        });
        return ld;
    }

    public LiveData<Dish> getDish(String foodServiceName, String name) {
        MutableLiveData<Dish> ld = new MutableLiveData<>();
        dishDao.get(foodServiceName, name).observeForever(dishDB -> {
            if (dishDB != null)
                ld.postValue(new Dish(dishDB.getName(), dishDB.getCost()));
        });
        return ld;
    }

    public void putDish(String foodServiceName, Dish dish) {
        FoodRoomDatabase.databaseWriteExecutor.execute(() ->
            dishDao.insert(new DishDBEntity(dish.getName(), dish.getCost(), foodServiceName))
        );
    }
}
