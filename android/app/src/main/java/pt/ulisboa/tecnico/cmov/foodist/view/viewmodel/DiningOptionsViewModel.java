package pt.ulisboa.tecnico.cmov.foodist.view.viewmodel;

import java.util.List;

import androidx.lifecycle.ViewModel;
import io.grpc.ManagedChannel;
import pt.ulisboa.tecnico.cmov.foodist.model.DiningOption;

import pt.ulisboa.tecnico.cmov.foodist.repository.DiningOptionsRepository;

public class DiningOptionsViewModel extends ViewModel {

    private DiningOptionsRepository repository;

    public void init(ManagedChannel channel) {
        if (repository == null)
            repository = new DiningOptionsRepository(channel);
    }

    public List<DiningOption> getDiningOptions() {
        return repository.getDiningOptions();
    }
}
