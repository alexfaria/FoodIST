import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import model.Dish;
import model.FoodService;
import model.OpeningHours;
import pt.ulisboa.tecnico.cmov.foodservice.*;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class FoodServiceImpl extends FoodServerGrpc.FoodServerImplBase {

    private final HashMap<String, FoodService> foodServices = new HashMap<>();

    public FoodServiceImpl() {
        Map<String, OpeningHours> restaurants = new HashMap<String, OpeningHours>() {{
            put("Student", new OpeningHours("09:00-21:00"));
            put("Professor", new OpeningHours("08:00-22:00"));
            put("Researcher", new OpeningHours("08:00-22:00"));
            put("Staff", new OpeningHours("08:00-22:00"));
            put("General Public", new OpeningHours("12:00-14:00"));
        }};
        Map<String, OpeningHours> bars = new HashMap<String, OpeningHours>() {{
            put("Student", new OpeningHours("10:00-20:00"));
            put("Professor", new OpeningHours("08:00-22:00"));
            put("Researcher", new OpeningHours("08:00-22:00"));
            put("Staff", new OpeningHours("08:00-22:00"));
            put("General Public", new OpeningHours("11:00-18:00"));
        }};
        Map<String, OpeningHours> lateBars = new HashMap<String, OpeningHours>() {{
            put("Student", new OpeningHours("12:00-23:00"));
            put("Professor", new OpeningHours("12:00-20:00"));
            put("Researcher", new OpeningHours("12:00-20:00"));
            put("Staff", new OpeningHours("12:00-20:00"));
            put("General Public", new OpeningHours("14:00-18:00"));
        }};
        foodServices.put("Social Canteen", new FoodService("Social Canteen", "Alameda", restaurants, 38.736382, -9.136967));
        foodServices.put("Civil Canteen", new FoodService("Civil Canteen", "Alameda", restaurants, 38.737732, -9.140482));
        foodServices.put("Civil Cafeteria", new FoodService("Civil Cafeteria", "Alameda", bars, 38.737066, -9.140007));
        foodServices.put("Math Cafeteria", new FoodService("Math Cafeteria", "Alameda", bars, 38.735610, -9.139690));
        foodServices.put("Chemistry Cafeteria", new FoodService("Chemistry Cafeteria", "Alameda", bars, 38.736012, -9.138324));
        foodServices.put("Central Bar", new FoodService("Central Bar", "Alameda", bars, 38.736610, -9.139605));
        foodServices.put("Mechanic Cafeteria", new FoodService("Mechanic Cafeteria", "Alameda", bars, 38.737422, -9.137403));
        foodServices.put("AEIST Bar", new FoodService("AEIST Bar", "Alameda", lateBars, 38.736382, -9.136967));
        foodServices.put("Complex Bar", new FoodService("Complex Bar", "Alameda", lateBars, 38.736131, -9.137807));
        foodServices.put("Sena Restaurant", new FoodService("Sena Restaurant", "Alameda", restaurants, 38.737715, -9.138638));
        foodServices.put("Canteen", new FoodService("Canteen", "Taguspark", restaurants, 38.736902, -9.302608));
        foodServices.put("Snack/Bar Praxe Bar", new FoodService("Snack/Bar Praxe Bar", "Taguspark", lateBars, 38.736902, -9.302608));
        foodServices.put("Taguspark Restaurant/Bar", new FoodService("Taguspark Restaurant/Bar", "Taguspark", restaurants, 38.736563, -9.302200));
    }

    @Override
    public void getFoodServices(GetFoodServicesRequest request, StreamObserver<FoodServiceDto> responseObserver) {
        // LocalTime current = LocalTime.now(ZoneId.of("GMT+1"));
        LocalTime current = LocalTime.of(14, 0);
        foodServices.values().forEach(fs -> {
            OpeningHours openingHours = fs.getOpeningHours(request.getStatus());
            if (fs.getCampus().equals(request.getCampus())
                    && openingHours.isAvailable(current))
                responseObserver.onNext(FoodServiceDto
                        .newBuilder()
                        .setName(fs.getName())
                        .setOpeningHours(openingHours.toString())
                        .setRating(fs.getRating())
                        .addAllCategories(fs.getCategories())
                        .setQueueTime(fs.getQueueWaitTime())
                        .setLatitude(fs.getLatitude())
                        .setLongitude(fs.getLongitude())
                        .build());
        });
        responseObserver.onCompleted();
    }

    @Override
    public void getFoodService(GetFoodServiceRequest request, StreamObserver<FoodServiceDto> responseObserver) {
        FoodService fs = foodServices.get(request.getFoodServiceName());
        if (fs != null) {
            responseObserver.onNext(FoodServiceDto
                    .newBuilder()
                    .setName(fs.getName())
                    .setOpeningHours(fs.getOpeningHours(request.getStatus()).toString())
                    .addAllCategories(fs.getCategories())
                    .setLatitude(fs.getLatitude())
                    .setLongitude(fs.getLongitude())
                    .build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getDishes(GetDishesRequest request, StreamObserver<DishDto> responseObserver) {
        FoodService foodService = foodServices.get(request.getFoodServiceName());
        if (foodService != null)
            foodService.getMenu().values().forEach(d ->
                    responseObserver.onNext(DishDto
                            .newBuilder()
                            .setName(d.getName())
                            .setCost(d.getCost())
                            .setCategory(d.getCategory())
                            .setAverageRating(d.getAverageRating())
                            .setNumberOfPhotos(d.getNumberOfPhotos())
                            .build()));
        responseObserver.onCompleted();
    }

    @Override
    public void getDish(GetDishRequest request, StreamObserver<DishWithPhotosDto> responseObserver) {
        FoodService foodService = foodServices.get(request.getFoodServiceName());
        if (foodService != null) {
            Dish dish = foodService.getMenu().get(request.getDishName());
            DishWithPhotosDto.Builder dtoBuilder = DishWithPhotosDto
                    .newBuilder()
                    .setName(dish.getName())
                    .setCost(dish.getCost())
                    .setCategory(dish.getCategory())
                    .putAllRatings(dish.getRatings());
            if (dish.hasPhotos())
                dtoBuilder.addAllPhotos(dish.getPhotos());
            responseObserver.onNext(dtoBuilder.build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void putDish(PutDishRequest request, StreamObserver<PutDishResponse> responseObserver) {
        FoodService foodService = foodServices.get(request.getFoodServiceName());
        boolean success = false;
        if (foodService != null) {
            Dish dish = new Dish(request.getDishName(), request.getDishCost(), request.getDishCategory());
            ByteString dishPhoto = request.getDishPhoto();
            if (!dishPhoto.isEmpty())
                dish.addPhoto(dishPhoto);
            success = foodService.addMenuItem(dish);
        }
        responseObserver.onNext(PutDishResponse.newBuilder().setSuccess(success).build());
        responseObserver.onCompleted();
    }

    @Override
    public void putDishPhoto(PutDishPhotoRequest request, StreamObserver<PutDishPhotoResponse> responseObserver) {
        FoodService foodService = foodServices.get(request.getFoodServiceName());
        if (foodService != null) {
            Dish dish = foodService.getMenu().get(request.getDishName());
            if (dish != null) {
                int index = dish.addPhoto(request.getPhoto());
                responseObserver.onNext(PutDishPhotoResponse.newBuilder().setIndex(index).build());
            }
        }
        responseObserver.onCompleted();
    }

    @Override
    public void putDishRating(PutDishRatingRequest request, StreamObserver<Empty> responseObserver) {
        FoodService foodService = foodServices.get(request.getFoodServiceName());
        if (foodService != null) {
            Dish dish = foodService.getMenu().get(request.getDishName());
            if (dish != null)
                dish.addRating(request.getUuid(), request.getRating());
        }
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void addToFoodServiceQueue(AddToFoodServiceQueueRequest request, StreamObserver<Empty> responseObserver) {
        FoodService foodService = foodServices.get(request.getFoodServiceName());
        if (foodService != null) {
            foodService.addToQueue(request.getUUID());
        }
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void removeFromFoodServiceQueue(RemoveFromFoodServiceQueueRequest request, StreamObserver<Empty> responseObserver) {
        FoodService foodService = foodServices.get(request.getFoodServiceName());
        if (foodService != null) {
            foodService.removeFromQueue(request.getUUID());
        }
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }
}
