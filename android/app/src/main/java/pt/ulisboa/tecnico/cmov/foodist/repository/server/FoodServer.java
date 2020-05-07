package pt.ulisboa.tecnico.cmov.foodist.repository.server;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.protobuf.ByteString;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.grpc.ManagedChannel;
import pt.ulisboa.tecnico.cmov.foodist.model.Dish;
import pt.ulisboa.tecnico.cmov.foodist.model.FoodService;
import pt.ulisboa.tecnico.cmov.foodservice.AddToFoodServiceQueueRequest;
import pt.ulisboa.tecnico.cmov.foodservice.DishDto;
import pt.ulisboa.tecnico.cmov.foodservice.DishWithPhotosDto;
import pt.ulisboa.tecnico.cmov.foodservice.FoodServerGrpc;
import pt.ulisboa.tecnico.cmov.foodservice.FoodServiceDto;
import pt.ulisboa.tecnico.cmov.foodservice.GetDishRequest;
import pt.ulisboa.tecnico.cmov.foodservice.GetDishesRequest;
import pt.ulisboa.tecnico.cmov.foodservice.GetFoodServiceRequest;
import pt.ulisboa.tecnico.cmov.foodservice.GetFoodServicesRequest;
import pt.ulisboa.tecnico.cmov.foodservice.PutDishPhotoRequest;
import pt.ulisboa.tecnico.cmov.foodservice.PutDishPhotoResponse;
import pt.ulisboa.tecnico.cmov.foodservice.PutDishRatingRequest;
import pt.ulisboa.tecnico.cmov.foodservice.PutDishRequest;
import pt.ulisboa.tecnico.cmov.foodservice.RemoveFromFoodServiceQueueRequest;

public class FoodServer {

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService serverExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private final ManagedChannel channel;

    public FoodServer(ManagedChannel channel) {
        this.channel = channel;
    }

    public ArrayList<FoodService> getFoodServices(String campus, String status) {
        FoodServerGrpc.FoodServerBlockingStub stub = FoodServerGrpc.newBlockingStub(channel);
        Iterator<FoodServiceDto> foodServicesDtos = stub.getFoodServices(
                GetFoodServicesRequest.newBuilder().setCampus(campus).setStatus(status).build());
        ArrayList<FoodService> diningOptions = new ArrayList<>();
        while (foodServicesDtos.hasNext()) {
            FoodServiceDto dto = foodServicesDtos.next();
            FoodService dOption = new FoodService(dto.getName(), dto.getOpeningHoursList(), dto.getRating(), dto.getCategoriesList(), dto.getQueueTime(),dto.getLatitude(), dto.getLongitude());
            diningOptions.add(dOption);
        }
        return diningOptions;
    }

    public FoodService getFoodService(String campus, String status, String foodServiceName) {
        FoodServerGrpc.FoodServerBlockingStub stub = FoodServerGrpc.newBlockingStub(channel);
        FoodServiceDto dto = stub.getFoodService(
                GetFoodServiceRequest.newBuilder().setCampus(campus).setStatus(status).setFoodServiceName(foodServiceName).build());
       return new FoodService(dto.getName(), dto.getOpeningHoursList(), dto.getRating(), dto.getCategoriesList(), dto.getQueueTime(), dto.getLatitude(), dto.getLongitude());
    }

    public ArrayList<Dish> getDishes(String foodServiceName) {
        FoodServerGrpc.FoodServerBlockingStub stub = FoodServerGrpc.newBlockingStub(channel);
        Iterator<DishDto> dishesDto = stub.getDishes(GetDishesRequest.newBuilder().setFoodServiceName(foodServiceName).build());
        ArrayList<Dish> dishes = new ArrayList<>();
        while (dishesDto.hasNext()) {
            DishDto dto = dishesDto.next();
            Dish dish = new Dish(dto.getName(), dto.getCost(), dto.getCategory(), dto.getNumberOfPhotos());
            dish.setAverageRating(dto.getAverageRating());
            dishes.add(dish);
        }
        return dishes;
    }

    public Dish getDish(String foodServiceName, String dishName, String uuid) {
        FoodServerGrpc.FoodServerBlockingStub stub = FoodServerGrpc.newBlockingStub(channel);
        DishWithPhotosDto dishDto = stub.getDish(GetDishRequest
                .newBuilder()
                .setFoodServiceName(foodServiceName)
                .setDishName(dishName)
                .setUuid(uuid)
                .build());
        List<ByteString> photosList = dishDto.getPhotosList();
        Dish dish = new Dish(dishDto.getName(), dishDto.getCost(), dishDto.getCategory(), photosList.size());
        dish.setRatings(dishDto.getRatingsMap());
        for(ByteString photo : photosList)
            dish.addPhoto(BitmapFactory.decodeByteArray(photo.toByteArray(), 0, photo.size()));
        return dish;
    }

    public boolean putDish(String foodServiceName, Dish dish) {
        FoodServerGrpc.FoodServerBlockingStub stub = FoodServerGrpc.newBlockingStub(channel);
        PutDishRequest.Builder builder = PutDishRequest
                .newBuilder()
                .setFoodServiceName(foodServiceName)
                .setDishName(dish.getName())
                .setDishCategory(dish.getCategory())
                .setDishCost(dish.getCost());
        Bitmap photo = dish.getPhoto(0);
        if (photo != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            builder.setDishPhoto(ByteString.copyFrom(stream.toByteArray()));
        }
        return stub.putDish(builder.build()).getSuccess();
    }

    public int putDishPhoto(String foodServiceName, String dishName, Bitmap photo) {
        FoodServerGrpc.FoodServerBlockingStub stub = FoodServerGrpc.newBlockingStub(channel);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        PutDishPhotoResponse putDishPhotoResponse = stub.putDishPhoto(PutDishPhotoRequest
                .newBuilder()
                .setFoodServiceName(foodServiceName)
                .setDishName(dishName)
                .setPhoto(ByteString.copyFrom(stream.toByteArray()))
                .build());
        return putDishPhotoResponse.getIndex();
    }

    public void putUserDishRating(String foodServiceName, String dishName, float rating, String uuid) {
        FoodServerGrpc.FoodServerBlockingStub stub = FoodServerGrpc.newBlockingStub(channel);
        stub.putDishRating(PutDishRatingRequest
                .newBuilder()
                .setFoodServiceName(foodServiceName)
                .setDishName(dishName)
                .setRating(rating)
                .setUuid(uuid)
                .build());
    }

    public void addToFoodServiceQueue(String campus, String foodServiceName) {
        FoodServerGrpc.FoodServerBlockingStub stub = FoodServerGrpc.newBlockingStub(channel);
        stub.addToFoodServiceQueue(AddToFoodServiceQueueRequest
                .newBuilder()
                .setFoodServiceName(foodServiceName)
                .setCampus(campus)
                .build());
    }

    public void removeFromFoodServiceQueue(String campus, String foodServiceName, String uuid) {
        FoodServerGrpc.FoodServerBlockingStub stub = FoodServerGrpc.newBlockingStub(channel);
        stub.removeFromFoodServiceQueue(RemoveFromFoodServiceQueueRequest
                .newBuilder()
                .setFoodServiceName(foodServiceName)
                .setCampus(campus)
                .build());
    }
}
