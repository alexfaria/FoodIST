package pt.ulisboa.tecnico.cmov.foodist.repository.server;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.grpc.ManagedChannel;
import pt.ulisboa.tecnico.cmov.foodist.model.DiningOption;
import pt.ulisboa.tecnico.cmov.foodist.model.Dish;
import pt.ulisboa.tecnico.cmov.foodservice.FoodServerGrpc;
import pt.ulisboa.tecnico.cmov.foodservice.FoodServiceDto;
import pt.ulisboa.tecnico.cmov.foodservice.GetFoodServicesRequest;
import pt.ulisboa.tecnico.cmov.foodservice.PutDishRequest;

public class FoodServer {

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService serverExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private final ManagedChannel channel;

    public FoodServer(ManagedChannel channel) {
        this.channel = channel;
    }

    public ArrayList<DiningOption> getDiningOptions(String campus) {
        FoodServerGrpc.FoodServerBlockingStub stub = FoodServerGrpc.newBlockingStub(channel);
        Iterator<FoodServiceDto> foodServicesDtos = stub.getFoodServices(GetFoodServicesRequest.newBuilder().setCampus(campus).build());
        ArrayList<DiningOption> diningOptions = new ArrayList<>();
        while (foodServicesDtos.hasNext()) {
            FoodServiceDto dto = foodServicesDtos.next();
            DiningOption dOption = new DiningOption(dto.getName(), dto.getOpeningHours());
            diningOptions.add(dOption);
        }
        return diningOptions;
    }

    public boolean putDish(String foodServiceName, Dish dish) {
        FoodServerGrpc.FoodServerBlockingStub stub = FoodServerGrpc.newBlockingStub(channel);
        return stub.putDish(PutDishRequest
                    .newBuilder()
                    .setFoodServiceName(foodServiceName)
                    .setDishName(dish.getName())
                    .setDishCost(dish.getCost())
                    .build())
                .getSuccess();
    }
}
