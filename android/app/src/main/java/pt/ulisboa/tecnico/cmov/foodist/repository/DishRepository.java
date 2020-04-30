package pt.ulisboa.tecnico.cmov.foodist.repository;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pt.ulisboa.tecnico.cmov.foodist.model.Dish;
import pt.ulisboa.tecnico.cmov.foodist.repository.cache.BitmapCache;
import pt.ulisboa.tecnico.cmov.foodist.repository.database.FoodRoomDatabase;
import pt.ulisboa.tecnico.cmov.foodist.repository.database.dao.DishDao;
import pt.ulisboa.tecnico.cmov.foodist.repository.database.entity.DishDBEntity;
import pt.ulisboa.tecnico.cmov.foodist.repository.server.FoodServer;
import pt.ulisboa.tecnico.cmov.foodist.view.App;

public class DishRepository {

    private DishDao dishDao;
    private FoodServer foodServer;
    private BitmapCache bitmapCache;


    public DishRepository(App application) {
        FoodRoomDatabase db = FoodRoomDatabase.getDatabase(application);
        dishDao = db.dishDao();
        foodServer = application.getServer();
        bitmapCache = application.getBitmapCache();
    }

    public LiveData<List<Dish>> getDishes(String foodServiceName) {
        MutableLiveData<List<Dish>> ld = new MutableLiveData<>();
        dishDao.getAll(foodServiceName).observeForever(dishDB -> {
            List<Dish> dishes = new ArrayList<>();
            if (dishDB != null) {
                for (DishDBEntity d : dishDB)
                    dishes.add(new Dish(d.getName(), d.getCost(), d.getNumberOfPhotos()));
                ld.postValue(dishes);
            }
        });
        refreshDishes(foodServiceName);
        return ld;
    }

    public LiveData<Dish> getDish(String foodServiceName, String name) {
        MutableLiveData<Dish> ld = new MutableLiveData<>();
        dishDao.get(foodServiceName, name).observeForever(dishDB -> {
            if (dishDB != null) {
                Dish dish = new Dish(dishDB.getName(), dishDB.getCost(), dishDB.getNumberOfPhotos());
                String key = generateKeyFrom(foodServiceName, name);
                for (int i = 0; i < dishDB.getNumberOfPhotos(); i++) {
                    bitmapCache.get(key+i, bitmap -> {
                        if (bitmap != null) {
                            dish.addPhoto(bitmap);
                            ld.postValue(dish);
                        }});
                }
                ld.postValue(dish);
            }
        });
        refreshDish(foodServiceName, name);
        return ld;
    }

    public LiveData<Boolean> putDish(String foodServiceName, Dish dish) {
        MutableLiveData<Boolean> ld = new MutableLiveData<>();
        FoodServer.serverExecutor.execute(() -> {
            boolean success = foodServer.putDish(foodServiceName, dish);
            if (success)
                FoodRoomDatabase.databaseWriteExecutor.execute(() -> {
                    dishDao.insert(new DishDBEntity(dish.getName(), dish.getCost(), dish.getNumberOfPhotos(), foodServiceName));
                });
            ld.postValue(success);
        });
        return ld;
    }

    public void putDishPhoto(String foodServiceName, String dishName, Bitmap photo) {
        FoodServer.serverExecutor.execute(() -> {
            int index  = foodServer.putDishPhoto(foodServiceName, dishName, photo);
            String key = generateKeyFrom(foodServiceName, dishName);
            bitmapCache.put(key+index, photo);
            FoodRoomDatabase.databaseWriteExecutor.execute(() -> {
                dishDao.updateNumberOfPhotos(foodServiceName, dishName, index+1);
            });
        });
    }

    private void refreshDishes(String foodServiceName) {
        FoodServer.serverExecutor.execute(() -> {
            ArrayList<Dish> dishes = foodServer.getDishes(foodServiceName);
            FoodRoomDatabase.databaseWriteExecutor.execute(() -> {
                for (Dish d : dishes)
                    dishDao.insert(new DishDBEntity(d.getName(), d.getCost(), d.getNumberOfPhotos(), foodServiceName));
            });
        });
    }

    private void refreshDish(String foodServiceName, String name) {
        FoodServer.serverExecutor.execute(() -> {
            Dish dish = foodServer.getDish(foodServiceName, name);
            List<Bitmap> photos = dish.getPhotos();
            String key = generateKeyFrom(foodServiceName, name);
            for (int i = 0; i < photos.size(); i++) {
                bitmapCache.put(key+i, photos.get(i));
            }
            FoodRoomDatabase.databaseWriteExecutor.execute(() -> {
                dishDao.insert(new DishDBEntity(dish.getName(), dish.getCost(), dish.getNumberOfPhotos(), foodServiceName));
            });
        });
    }

    private String generateKeyFrom(String foodServiceName, String dishName) {
        String key = String.format(Locale.getDefault(),"%s_%s_", foodServiceName, dishName);
        return key.replaceAll("[ /]", "").toLowerCase();
    }
}
