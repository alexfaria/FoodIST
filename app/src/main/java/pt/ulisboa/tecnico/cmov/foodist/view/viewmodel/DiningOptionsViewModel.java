package pt.ulisboa.tecnico.cmov.foodist.view.viewmodel;

import java.util.List;

import androidx.lifecycle.ViewModel;
import io.grpc.ManagedChannel;
import pt.ulisboa.tecnico.cmov.foodist.model.DiningOption;

import pt.ulisboa.tecnico.cmov.foodist.repository.FoodServiceRepository;

public class DiningOptionsViewModel extends ViewModel {

    private FoodServiceRepository repository;

    public void init(ManagedChannel channel) {
        if (repository == null)
            repository = new FoodServiceRepository(channel);
    }

    public List<DiningOption> getDiningOptions() {
        return repository.getFoodServices();
    }
}
