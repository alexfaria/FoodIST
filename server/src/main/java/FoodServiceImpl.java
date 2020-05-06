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

        Map<String, OpeningHours> bars = new HashMap<String, OpeningHours>() {{
            put("Student", new OpeningHours("9:00-17:00"));
            put("Professor", new OpeningHours("9:00-17:00"));
            put("Researcher", new OpeningHours("9:00-17:00"));
            put("Staff", new OpeningHours("9:00-17:00"));
            put("General Public", new OpeningHours("9:00-17:00"));
        }};
        Map<String, OpeningHours> mathCafe = new HashMap<String, OpeningHours>() {{
            put("Student", new OpeningHours("13:30-15:00"));
            put("Professor", new OpeningHours("12:00-15:00"));
            put("Researcher", new OpeningHours("12:00-15:00"));
            put("Staff", new OpeningHours("12:00-15:00"));
            put("General Public", new OpeningHours("13:30-15:00"));
        }};
        Map<String, OpeningHours> complexBar = new HashMap<String, OpeningHours>() {{
            put("Student", new OpeningHours("9:00-12:00"));
            //put("Student", new OpeningHours("14:00-17:00"));
            put("Professor", new OpeningHours("9:00-17:00"));
            put("Researcher", new OpeningHours("9:00-17:000"));
            put("Staff", new OpeningHours("9:00-17:00"));
            put("General Public", new OpeningHours("9:00-12:00"));
            //put("General Public", new OpeningHours("14:00-17:00"));
        }};
        Map<String, OpeningHours> cafeCivTag = new HashMap<String, OpeningHours>() {{
            put("Student", new OpeningHours("12:00-15:00"));
            put("Professor", new OpeningHours("12:00-15:00"));
            put("Researcher", new OpeningHours("12:00-15:00"));
            put("Staff", new OpeningHours("12:00-15:00"));
            put("General Public", new OpeningHours("12:00-15:00"));
        }};
        Map<String, OpeningHours> sena = new HashMap<String, OpeningHours>() {{
            put("Student", new OpeningHours("8:00-19:00"));
            put("Professor", new OpeningHours("8:00-19:00"));
            put("Researcher", new OpeningHours("8:00-19:00"));
            put("Staff", new OpeningHours("8:00-19:00"));
            put("General Public", new OpeningHours("8:00-19:00"));
        }};
        Map<String, OpeningHours> sas = new HashMap<String, OpeningHours>() {{
            put("Student", new OpeningHours("9:00-21:00"));
            put("Professor", new OpeningHours("9:00-21:00"));
            put("Researcher", new OpeningHours("9:00-21:00"));
            put("Staff", new OpeningHours("9:00-21:00"));
            put("General Public", new OpeningHours("9:00-21:00"));
        }};
        Map<String, OpeningHours> redBar = new HashMap<String, OpeningHours>() {{
            put("Student", new OpeningHours("8:00-22:00"));
            put("Professor", new OpeningHours("8:00-22:00"));
            put("Researcher", new OpeningHours("8:00-22:00"));
            put("Staff", new OpeningHours("8:00-22:00"));
            put("General Public", new OpeningHours("8:00-22:00"));
        }};
        Map<String, OpeningHours> greenBar = new HashMap<String, OpeningHours>() {{
            put("Student", new OpeningHours("7:00-19:00"));
            put("Professor", new OpeningHours("7:00-19:00"));
            put("Researcher", new OpeningHours("7:00-19:00"));
            put("Staff", new OpeningHours("7:00-19:00"));
            put("General Public", new OpeningHours("7:00-19:00"));
        }};
        /*Map<String, OpeningHours> ctnCafe = new HashMap<String, OpeningHours>() {{
            put("Student", new OpeningHours("12:00-14:00"));
            put("Professor", new OpeningHours("12:00-14:00"));
            put("Researcher", new OpeningHours("12:00-14:00"));
            put("Staff", new OpeningHours("12:00-14:00"));
            put("General Public", new OpeningHours("12:00-14:00"));
        }};
        Map<String, OpeningHours> ctnBar = new HashMap<String, OpeningHours>() {{
            put("Student", new OpeningHours("8:30-12:00"));
            put("Professor", new OpeningHours("8:30-12:00"));
            put("Researcher", new OpeningHours("8:30-12:00"));
            put("Staff", new OpeningHours("8:30-12:00"));
            put("General Public", new OpeningHours("8:30-12:00"));

        }};*/
        foodServices.put("SAS Cafeteria", new FoodService("SAS Cafeteria", "Alameda", sas, 38.736571, -9.137036));
        foodServices.put("Civil Bar", new FoodService("Civil Bar", "Alameda", bars, 38.736988, -9.139955));
        foodServices.put("Civil Cafeteria", new FoodService("Civil Cafeteria", "Alameda", cafeCivTag, 38.737650, -9.140384));
        foodServices.put("Math Cafeteria", new FoodService("Math Cafeteria", "Alameda", mathCafe, 38.735508, -9.139645));
        foodServices.put("Chemy Bar", new FoodService("Chemy Bar", "Alameda", bars, 38.736240, -9.138302));
        foodServices.put("Central Bar", new FoodService("Central Bar", "Alameda", bars, 38.736606, -9.139532));
        foodServices.put("Mechy Bar", new FoodService("Mechy Bar", "Alameda", bars,38.737247, -9.137434));
        foodServices.put("AEIST Bar", new FoodService("AEIST Bar", "Alameda", bars, 38.736542, -9.137226));
        //foodServices.put("AEIST Esplanade", new FoodService("AEIST Esplanade", "Alameda", bars, 	38.736318, -9.137820));
        foodServices.put("Complex Bar", new FoodService("Complex Bar", "Alameda", complexBar, 38.736050, -9.140156));
        foodServices.put("Sena Pastry Shop", new FoodService("Sena Pastry Shop", "Alameda", sena, 38.737677, -9.138672));
        foodServices.put("Tagus Cafeteria", new FoodService("Tagus Cafeteria", "Taguspark", cafeCivTag, 38.737802, -9.303223));
        foodServices.put("Green Bar", new FoodService("Green Bar", "Taguspark", greenBar, 38.738004, -9.303058));
        foodServices.put("Red Bar", new FoodService("Red Bar", "Taguspark", redBar, 38.736546, -9.302207));
        //foodServices.put("CTN Cafeteria", new FoodService("CTN Cafeteria", "CTN", ctnCafe, 38.812522, -9.093773));
        //foodServices.put("CTN Bar", new FoodService("CTN Bar", "CTN", ctnBar, 38.812522, -9.093773));
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
