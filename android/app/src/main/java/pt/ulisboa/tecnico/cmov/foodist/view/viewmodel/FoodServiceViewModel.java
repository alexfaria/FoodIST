package pt.ulisboa.tecnico.cmov.foodist.view.viewmodel;

import android.os.Handler;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.model.DiningOption;
import pt.ulisboa.tecnico.cmov.foodist.model.FoodService;
import pt.ulisboa.tecnico.cmov.foodist.repository.FoodServiceRepository;
import pt.ulisboa.tecnico.cmov.foodist.repository.server.FoodServer;
import pt.ulisboa.tecnico.cmov.foodist.view.App;

public class FoodServiceViewModel extends ViewModel {

    private FoodServiceRepository repository;

    public void init(App application) {
        repository = new FoodServiceRepository(application);
    }

    public void getDiningOptions(Handler handler) {
        repository.getDiningOptions(handler);
    }

    public LiveData<List<DiningOption>> getFoodServices(String campus) {
        return repository.getFoodServices(campus);
    }

    public LiveData<FoodService> getFoodService(String campus, String name) {
        return repository.getFoodService(campus, name);
    }
}
