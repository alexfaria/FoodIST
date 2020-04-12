package pt.ulisboa.tecnico.cmov.foodist.repository.server;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.protobuf.ByteString;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.grpc.ManagedChannel;
import pt.ulisboa.tecnico.cmov.foodist.model.DiningOption;
import pt.ulisboa.tecnico.cmov.foodist.model.Dish;
import pt.ulisboa.tecnico.cmov.foodservice.DishDto;
import pt.ulisboa.tecnico.cmov.foodservice.DishWithPhotosDto;
import pt.ulisboa.tecnico.cmov.foodservice.FoodServerGrpc;
import pt.ulisboa.tecnico.cmov.foodservice.FoodServiceDto;
import pt.ulisboa.tecnico.cmov.foodservice.GetDishRequest;
import pt.ulisboa.tecnico.cmov.foodservice.GetDishesRequest;
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

    public ArrayList<Dish> getDishes(String foodServiceName) {
        FoodServerGrpc.FoodServerBlockingStub stub = FoodServerGrpc.newBlockingStub(channel);
        Iterator<DishDto> dishesDto = stub.getDishes(GetDishesRequest.newBuilder().setFoodServiceName(foodServiceName).build());
        ArrayList<Dish> dishes = new ArrayList<>();
        while (dishesDto.hasNext()) {
            DishDto dto = dishesDto.next();
            dishes.add(new Dish(dto.getName(), dto.getCost()));
        }
        return dishes;
    }

    public Dish getDish(String foodServiceName, String dishName) {
        FoodServerGrpc.FoodServerBlockingStub stub = FoodServerGrpc.newBlockingStub(channel);
        DishWithPhotosDto dishDto = stub.getDish(GetDishRequest
                .newBuilder()
                .setFoodServiceName(foodServiceName)
                .setDishName(dishName)
                .build());
        Dish dish = new Dish(dishDto.getName(), dishDto.getCost());
        for(ByteString photo : dishDto.getPhotosList())
            dish.addPhoto(BitmapFactory.decodeByteArray(photo.toByteArray(), 0, photo.size()));
        return dish;
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
