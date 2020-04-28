package pt.ulisboa.tecnico.cmov.foodist.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.model.FoodService;
import pt.ulisboa.tecnico.cmov.foodist.repository.database.FoodRoomDatabase;
import pt.ulisboa.tecnico.cmov.foodist.repository.database.dao.FoodServiceDao;
import pt.ulisboa.tecnico.cmov.foodist.repository.database.entity.FoodServiceDBEntity;
import pt.ulisboa.tecnico.cmov.foodist.repository.server.FoodServer;
import pt.ulisboa.tecnico.cmov.foodist.view.App;

public class FoodServiceRepository {

    private FoodServiceDao foodServiceDao;
    private FoodServer foodServer;

    public FoodServiceRepository(App application) {
        FoodRoomDatabase db = FoodRoomDatabase.getDatabase(application);
        foodServiceDao = db.foodServiceDao();
        foodServer = application.getServer();
    }

    public LiveData<List<FoodService>> getFoodServices(String campus, String status) {
        MutableLiveData<List<FoodService>> ld = new MutableLiveData<>();
        foodServiceDao.getAll(campus).observeForever(fsDB -> {
            List<FoodService> foodServices = new ArrayList<>();
            if (fsDB != null) {
                for(FoodServiceDBEntity fs : fsDB) {
                    foodServices.add(
                            new FoodService(fs.getName(), fs.getOpeningHours(), fs.getLatitude(), fs.getLongitude())
                    );
                }
            }
            ld.postValue(foodServices);
        });
        /*
        FoodServer.serverExecutor.execute(() -> {
            ld.postValue(foodServer.getFoodServices(campus, status));
        });*/
        return ld;
    }

    public LiveData<FoodService> getFoodService(String campus, String name) {
        MutableLiveData<FoodService> ld = new MutableLiveData<>();
        foodServiceDao.get(campus, name).observeForever(fsDB -> {
            if (fsDB != null)
                ld.postValue(new FoodService(fsDB.getName(), fsDB.getOpeningHours(), fsDB.getLatitude(), fsDB.getLongitude()));
        });
        return ld;
    }

    public void addToFoodServiceQueue(String campus, String name) {
        FoodServer.serverExecutor.execute(() -> {
            foodServer.addToFoodServiceQueue(campus, name);
        });
    }

    public void removeFromFoodServiceQueue(String campus, String name) {
        FoodServer.serverExecutor.execute(() -> {
            foodServer.removeFromFoodServiceQueue(campus, name);
        });
    }

}
