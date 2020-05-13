package pt.ulisboa.tecnico.cmov.foodist.view.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.model.FoodService;
import pt.ulisboa.tecnico.cmov.foodist.repository.FoodServiceRepository;

public class FoodServiceViewModel extends ViewModel {

    private Context context;
    private FoodServiceRepository repository;

    public void init(Context context) {
        repository = new FoodServiceRepository(context);
    }

    public LiveData<List<FoodService>> getFoodServices(String campus, String status) {
        return repository.getFoodServices(campus, status);
    }

    public LiveData<FoodService> getFoodService(String campus, String status, String name) {
        return repository.getFoodService(campus, status, name);
    }
}
