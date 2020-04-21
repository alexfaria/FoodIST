package pt.ulisboa.tecnico.cmov.foodist.view.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.model.FoodService;
import pt.ulisboa.tecnico.cmov.foodist.repository.FoodServiceRepository;
import pt.ulisboa.tecnico.cmov.foodist.view.App;

public class FoodServiceViewModel extends ViewModel {

    private FoodServiceRepository repository;

    public void init(App application) {
        repository = new FoodServiceRepository(application);
    }

    public LiveData<List<FoodService>> getFoodServices(String campus) {
        return repository.getFoodServices(campus);
    }

    public LiveData<FoodService> getFoodService(String campus, String name) {
        return repository.getFoodService(campus, name);
    }

    public void addToFoodServiceQueue(String campus, String name) {
        repository.addToFoodServiceQueue(campus, name);
    }

    public void removeFromFoodServiceQueue(String campus, String name) {
        repository.removeFromFoodServiceQueue(campus, name);
    }
}
