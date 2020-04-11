package pt.ulisboa.tecnico.cmov.foodist.view.viewmodel;

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

    public LiveData<Dish> getDish(String foodServiceName, String name) {
        return repository.getDish(foodServiceName, name);
    }

    public boolean putDish(String foodServiceName, Dish dish) {
        return repository.putDish(foodServiceName, dish);
    }
}