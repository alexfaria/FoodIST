package pt.ulisboa.tecnico.cmov.foodist.repository;

import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.model.DiningOption;
import pt.ulisboa.tecnico.cmov.foodist.model.FoodService;
import pt.ulisboa.tecnico.cmov.foodist.repository.database.FoodRoomDatabase;
import pt.ulisboa.tecnico.cmov.foodist.repository.database.dao.FoodServiceDao;
import pt.ulisboa.tecnico.cmov.foodist.repository.database.entity.FoodServiceDBEntity;
import pt.ulisboa.tecnico.cmov.foodist.repository.server.FoodServer;
import pt.ulisboa.tecnico.cmov.foodist.view.App;
import pt.ulisboa.tecnico.cmov.foodservice.DiningOptionDto;
import pt.ulisboa.tecnico.cmov.foodservice.FoodServerGrpc;
import pt.ulisboa.tecnico.cmov.foodservice.FoodServiceDto;
import pt.ulisboa.tecnico.cmov.foodservice.GetDiningOptionsRequest;
import pt.ulisboa.tecnico.cmov.foodservice.GetFoodServicesRequest;

public class FoodServiceRepository {

    private FoodServiceDao foodServiceDao;
    private FoodServer foodServer;

    public FoodServiceRepository(App application) {
        FoodRoomDatabase db = FoodRoomDatabase.getDatabase(application);
        foodServiceDao = db.foodServiceDao();
        foodServer = application.getServer();
    }

    public void getDiningOptions(Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("values", foodServer.getDiningOptions("Alameda"));
                Message message = new Message();
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }).start();
    }

    public LiveData<List<DiningOption>> getFoodServices(String campus) {
        MutableLiveData<List<DiningOption>> ld = new MutableLiveData<>();
        foodServiceDao.getAll(campus).observeForever(fsDB -> {
            List<DiningOption> foodServices = new ArrayList<>();
            if (fsDB != null) {
                for(FoodServiceDBEntity fs : fsDB) {
                    foodServices.add(
                            new DiningOption(fs.getName(), fs.getOpeningHours())
                    );
                }
            }
            ld.postValue(foodServices);
        });
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

}
