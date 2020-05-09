package pt.ulisboa.tecnico.cmov.foodist.repository;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pt.ulisboa.tecnico.cmov.foodist.model.Dish;
import pt.ulisboa.tecnico.cmov.foodist.repository.cache.BitmapCache;
import pt.ulisboa.tecnico.cmov.foodist.repository.database.FoodRoomDatabase;
import pt.ulisboa.tecnico.cmov.foodist.repository.database.dao.DishDao;
import pt.ulisboa.tecnico.cmov.foodist.repository.database.dao.DishRatingDao;
import pt.ulisboa.tecnico.cmov.foodist.repository.database.entity.DishDBEntity;
import pt.ulisboa.tecnico.cmov.foodist.repository.database.entity.DishRatingDBEntity;
import pt.ulisboa.tecnico.cmov.foodist.repository.server.FoodServer;
import pt.ulisboa.tecnico.cmov.foodist.view.App;

public class DishRepository {

    private final App context;

    private DishDao dishDao;
    private DishRatingDao dishRatingDao;
    private BitmapCache bitmapCache;

    public DishRepository(Context c) {
        this.context = (App) c;

        FoodRoomDatabase db = FoodRoomDatabase.getDatabase(context);
        dishDao = db.dishDao();
        dishRatingDao = db.dishRatingDao();
        bitmapCache = context.getBitmapCache();
    }

    public LiveData<List<Dish>> getDishes(String foodServiceName) {
        MutableLiveData<List<Dish>> ld = new MutableLiveData<>();
        dishDao.getAll(foodServiceName).observeForever(dishDB -> {
            List<Dish> dishes = new ArrayList<>();
            if (dishDB != null) {
                for (DishDBEntity d : dishDB) {
                    Dish dish = new Dish(d.getName(), d.getCost(), d.getCategory(), d.getNumberOfPhotos());
                    dish.setAverageRating(d.getRating());
                    dishes.add(dish);
                }
                ld.postValue(dishes);
            }
        });
        refreshDishes(foodServiceName);
        return ld;
    }

    public LiveData<Dish> getDish(String foodServiceName, String name, String uuid) {
        MutableLiveData<Dish> ld = new MutableLiveData<>();
        dishDao.get(foodServiceName, name).observeForever(dishDB -> {
            if (dishDB != null) {
                Dish dish = new Dish(dishDB.getName(), dishDB.getCost(), dishDB.getCategory(), dishDB.getNumberOfPhotos());
                String key = Dish.generateKeyFrom(foodServiceName, name);
                for (int i = 0; i < dishDB.getNumberOfPhotos(); i++) {
                    bitmapCache.get(key + i, bitmap -> {
                        if (bitmap != null) {
                            dish.addPhoto(bitmap);
                            ld.postValue(dish);
                        }
                    });
                }
                ld.postValue(dish);
            }
        });
        refreshDish(foodServiceName, name, uuid);
        return ld;
    }

    public LiveData<Boolean> putDish(String foodServiceName, Dish dish) {
        MutableLiveData<Boolean> ld = new MutableLiveData<>();
        if (context.isConnected()) {
            final FoodServer foodServer = context.getServer();
            FoodServer.serverExecutor.execute(() -> {
                boolean success = foodServer.putDish(foodServiceName, dish);
                if (success)
                    FoodRoomDatabase.databaseWriteExecutor.execute(() -> {
                        dishDao.insert(new DishDBEntity(dish.getName(), dish.getCost(), dish.getCategory(), dish.getAverageRating(), dish.getNumberOfPhotos(), foodServiceName));
                    });
                ld.postValue(success);
            });
        }
        return ld;
    }

    public void putDishPhoto(String foodServiceName, String dishName, Bitmap photo) {
        if (context.isConnected()) {
            final FoodServer foodServer = context.getServer();
            FoodServer.serverExecutor.execute(() -> {
                int index = foodServer.putDishPhoto(foodServiceName, dishName, photo);
                String key = Dish.generateKeyFrom(foodServiceName, dishName);
                bitmapCache.put(key + index, photo);
                FoodRoomDatabase.databaseWriteExecutor.execute(() -> {
                    dishDao.updateNumberOfPhotos(foodServiceName, dishName, index + 1);
                });
            });
        }
    }

    public LiveData<List<Float>> getAllRatings(String foodServiceName, String dishName) {
        MutableLiveData<List<Float>> ld = new MutableLiveData<>();
        dishRatingDao.getAll(foodServiceName, dishName).observeForever(ratingDB -> {
            if (ratingDB != null) {
                List<Float> ratings = new ArrayList<>();
                for (DishRatingDBEntity ratingDBEntity : ratingDB)
                    ratings.add(ratingDBEntity.getRating());
                ld.postValue(ratings);
            }
        });
        return ld;
    }

    public LiveData<Float> getUserDishRating(String foodServiceName, String dishName, String uuid) {
        MutableLiveData<Float> ld = new MutableLiveData<>();
        dishRatingDao.get(uuid, foodServiceName, dishName).observeForever(ratingDB -> {
            if (ratingDB != null)
                ld.postValue(ratingDB.getRating());
        });
        return ld;
    }

    public void putUserDishRating(String foodServiceName, String dishName, float rating, String uuid) {
        if (context.isConnected()) {
            final FoodServer foodServer = context.getServer();
            FoodServer.serverExecutor.execute(() -> {
                foodServer.putUserDishRating(foodServiceName, dishName, rating, uuid);
                FoodRoomDatabase.databaseWriteExecutor.execute(() -> {
                    dishRatingDao.insert(new DishRatingDBEntity(uuid, rating, foodServiceName, dishName));
                });
            });
        }
    }

    private void refreshDishes(String foodServiceName) {
        if (context.isConnected()) {
            final FoodServer foodServer = context.getServer();
            FoodServer.serverExecutor.execute(() -> {
                ArrayList<Dish> dishes = foodServer.getDishes(foodServiceName);
                FoodRoomDatabase.databaseWriteExecutor.execute(() -> {
                    for (Dish d : dishes)
                        dishDao.insert(new DishDBEntity(d.getName(), d.getCost(), d.getCategory(), d.getAverageRating(), d.getNumberOfPhotos(), foodServiceName));
                });
            });
        }
    }

    private void refreshDish(String foodServiceName, String name, String uuid) {
        if (context.isConnected()) {
            final FoodServer foodServer = context.getServer();
            FoodServer.serverExecutor.execute(() -> {
                Dish dish = foodServer.getDish(foodServiceName, name, uuid);
                List<Bitmap> photos = dish.getPhotos();
                String key = Dish.generateKeyFrom(foodServiceName, name);
                for (int i = 0; i < photos.size(); i++)
                    bitmapCache.put(key + i, photos.get(i));
                FoodRoomDatabase.databaseWriteExecutor.execute(() -> {
                    float sum = 0;
                    for (Map.Entry<String, Float> rating : dish.getRatings().entrySet()) {
                        dishRatingDao.insert(new DishRatingDBEntity(rating.getKey(), rating.getValue(), foodServiceName, name));
                        sum += rating.getValue();
                    }
                    float average = sum == 0 ? 0 : sum / dish.getRatings().size();
                    dishDao.insert(new DishDBEntity(dish.getName(), dish.getCost(), dish.getCategory(), average, dish.getNumberOfPhotos(), foodServiceName));
                });
            });
        }
    }
}
