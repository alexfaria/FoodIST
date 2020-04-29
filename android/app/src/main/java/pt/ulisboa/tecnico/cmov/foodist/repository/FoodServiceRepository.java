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
        foodServiceDao.getAll(campus, status).observeForever(fsDB -> {
            List<FoodService> foodServices = new ArrayList<>();
            if (fsDB != null)
                for(FoodServiceDBEntity fs : fsDB)
                    if (fs.isOpen())
                        foodServices.add(
                            new FoodService(fs.getName(), fs.getOpeningHours(), fs.getQueueTime(),fs.getLatitude(), fs.getLongitude())
                        );
            ld.postValue(foodServices);
        });
        refreshFoodServices(campus, status);
        return ld;
    }

    public LiveData<FoodService> getFoodService(String campus, String status, String foodServiceName) {
        MutableLiveData<FoodService> ld = new MutableLiveData<>();
        foodServiceDao.get(campus, status, foodServiceName).observeForever(fsDB -> {
            if (fsDB != null)
                ld.postValue(new FoodService(fsDB.getName(), fsDB.getOpeningHours(), fsDB.getQueueTime(),fsDB.getLatitude(), fsDB.getLongitude()));
        });
        refreshFoodService(campus, status,foodServiceName);
        return ld;
    }

    public void addToFoodServiceQueue(String campus, String name) {
        FoodServer.serverExecutor.execute(() -> {
            foodServer.addToFoodServiceQueue(campus, name);
        });
    }

    public void removeFromFoodServiceQueue(String campus, String name, String uuid) {
        FoodServer.serverExecutor.execute(() -> {
            foodServer.removeFromFoodServiceQueue(campus, name, uuid);
        });
    }

    private void refreshFoodServices(String campus, String status) {
        FoodServer.serverExecutor.execute(() -> {
            ArrayList<FoodService> foodServices = foodServer.getFoodServices(campus, status);
            FoodRoomDatabase.databaseWriteExecutor.execute(() -> {
                for (FoodService fs : foodServices)
                    foodServiceDao.insert(new FoodServiceDBEntity(fs.getName(), campus, status, fs.getOpeningHours(), fs.getQueueTime(), fs.getLatitude(), fs.getLongitude()));
            });
        });
    }

    private void refreshFoodService(String campus, String status, String foodServiceName) {
        FoodServer.serverExecutor.execute(() -> {
            FoodService fs = foodServer.getFoodService(campus, status, foodServiceName);
            FoodRoomDatabase.databaseWriteExecutor.execute(() -> {
                    foodServiceDao.insert(new FoodServiceDBEntity(fs.getName(), campus, status, fs.getOpeningHours(), fs.getQueueTime(), fs.getLatitude(), fs.getLongitude()));
            });
        });
    }
}
