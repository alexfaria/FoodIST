package pt.ulisboa.tecnico.cmov.foodist.view.viewmodel;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.model.Dish;
import pt.ulisboa.tecnico.cmov.foodist.repository.DishRepository;
import pt.ulisboa.tecnico.cmov.foodist.view.App;

public class DishViewModel extends ViewModel {

    private DishRepository repository;

    public void init(App application) {
        repository = new DishRepository(application);
    }

    public LiveData<List<Dish>> getDishes(String foodServiceName) {
        return repository.getDishes(foodServiceName);
    }

    public LiveData<Dish> getDish(String foodServiceName, String name, String uuid) {
        return repository.getDish(foodServiceName, name, uuid);
    }

    public LiveData<Boolean> putDish(String foodServiceName, Dish dish) {
        return repository.putDish(foodServiceName, dish);
    }

    public void putDishPhoto(String foodServiceName, String dishName, Bitmap photo) {
        repository.putDishPhoto(foodServiceName, dishName, photo);
    }

    public LiveData<List<Float>> getAllRatings(String foodServiceName, String dishName) {
        return repository.getAllRatings(foodServiceName, dishName);
    }

    public LiveData<Float> getUserDishRating(String foodServiceName, String dishName, String uuid) {
        return repository.getUserDishRating(foodServiceName, dishName, uuid);
    }

    public void putUserDishRating(String foodServiceName, String dishName, float rating, String uuid) {
        repository.putUserDishRating(foodServiceName, dishName, rating, uuid);
    }
}