import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import model.Dish;
import model.FoodService;
import model.OpeningHours;
import pt.ulisboa.tecnico.cmov.foodservice.*;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

public class FoodServiceImpl extends FoodServerGrpc.FoodServerImplBase {

    private final HashMap<String, FoodService> foodServices = new HashMap<>();

    public FoodServiceImpl() {
        Map<String, List<OpeningHours>> bars = new HashMap<String, List<OpeningHours>>() {{
            put("Student", Collections.singletonList(new OpeningHours("09:00-17:00")));
            put("Professor", Collections.singletonList(new OpeningHours("09:00-17:00")));
            put("Researcher", Collections.singletonList(new OpeningHours("09:00-17:00")));
            put("Staff", Collections.singletonList(new OpeningHours("09:00-17:00")));
            put("General Public", Collections.singletonList(new OpeningHours("09:00-17:00")));
        }};
        Map<String, List<OpeningHours>> mathCafe = new HashMap<String, List<OpeningHours>>() {{
            put("Student", Collections.singletonList(new OpeningHours("13:30-15:00")));
            put("Professor", Collections.singletonList(new OpeningHours("12:00-15:00")));
            put("Researcher", Collections.singletonList(new OpeningHours("12:00-15:00")));
            put("Staff", Collections.singletonList(new OpeningHours("12:00-15:00")));
            put("General Public", Collections.singletonList(new OpeningHours("13:30-15:00")));
        }};
        Map<String, List<OpeningHours>> complexBar = new HashMap<String, List<OpeningHours>>() {{
            put("Student", Arrays.asList(new OpeningHours("09:00-12:00"), new OpeningHours("14:00-17:00")));
            put("Professor", Collections.singletonList(new OpeningHours("09:00-17:00")));
            put("Researcher", Collections.singletonList(new OpeningHours("09:00-17:00")));
            put("Staff", Collections.singletonList(new OpeningHours("09:00-17:00")));
            put("General Public", Arrays.asList(new OpeningHours("09:00-12:00"), new OpeningHours("14:00-17:00")));
        }};
        Map<String, List<OpeningHours>> cafeCivTag = new HashMap<String, List<OpeningHours>>() {{
            put("Student", Collections.singletonList(new OpeningHours("12:00-15:00")));
            put("Professor", Collections.singletonList(new OpeningHours("12:00-15:00")));
            put("Researcher", Collections.singletonList(new OpeningHours("12:00-15:00")));
            put("Staff", Collections.singletonList(new OpeningHours("12:00-15:00")));
            put("General Public", Collections.singletonList(new OpeningHours("12:00-15:00")));
        }};
        Map<String, List<OpeningHours>> sena = new HashMap<String, List<OpeningHours>>() {{
            put("Student", Collections.singletonList(new OpeningHours("08:00-19:00")));
            put("Professor", Collections.singletonList(new OpeningHours("08:00-19:00")));
            put("Researcher", Collections.singletonList(new OpeningHours("08:00-19:00")));
            put("Staff", Collections.singletonList(new OpeningHours("08:00-19:00")));
            put("General Public", Collections.singletonList(new OpeningHours("08:00-19:00")));
        }};
        Map<String, List<OpeningHours>> sas = new HashMap<String, List<OpeningHours>>() {{
            put("Student", Collections.singletonList(new OpeningHours("09:00-21:00")));
            put("Professor", Collections.singletonList(new OpeningHours("09:00-21:00")));
            put("Researcher", Collections.singletonList(new OpeningHours("09:00-21:00")));
            put("Staff", Collections.singletonList(new OpeningHours("09:00-21:00")));
            put("General Public", Collections.singletonList(new OpeningHours("09:00-21:00")));
        }};
        Map<String, List<OpeningHours>> redBar = new HashMap<String, List<OpeningHours>>() {{
            put("Student", Collections.singletonList(new OpeningHours("08:00-22:00")));
            put("Professor", Collections.singletonList(new OpeningHours("08:00-22:00")));
            put("Researcher", Collections.singletonList(new OpeningHours("08:00-22:00")));
            put("Staff", Collections.singletonList(new OpeningHours("08:00-22:00")));
            put("General Public", Collections.singletonList(new OpeningHours("08:00-22:00")));
        }};
        Map<String, List<OpeningHours>> greenBar = new HashMap<String, List<OpeningHours>>() {{
            put("Student", Collections.singletonList(new OpeningHours("07:00-19:00")));
            put("Professor", Collections.singletonList(new OpeningHours("07:00-19:00")));
            put("Researcher", Collections.singletonList(new OpeningHours("07:00-19:00")));
            put("Staff", Collections.singletonList(new OpeningHours("07:00-19:00")));
            put("General Public", Collections.singletonList(new OpeningHours("07:00-19:00")));
        }};
        Map<String, List<OpeningHours>> ctnCafe = new HashMap<String, List<OpeningHours>>() {{
            put("Student", Collections.singletonList(new OpeningHours("12:00-14:00")));
            put("Professor", Collections.singletonList(new OpeningHours("12:00-14:00")));
            put("Researcher", Collections.singletonList(new OpeningHours("12:00-14:00")));
            put("Staff", Collections.singletonList(new OpeningHours("12:00-14:00")));
            put("General Public", Collections.singletonList(new OpeningHours("12:00-14:00")));
        }};
        Map<String, List<OpeningHours>> ctnBar = new HashMap<String, List<OpeningHours>>() {{
            put("Student", Arrays.asList(new OpeningHours("08:30-12:00"), new OpeningHours("15:30-16:30")));
            put("Professor", Arrays.asList(new OpeningHours("08:30-12:00"), new OpeningHours("15:30-16:30")));
            put("Researcher", Arrays.asList(new OpeningHours("08:30-12:00"), new OpeningHours("15:30-16:30")));
            put("Staff", Arrays.asList(new OpeningHours("08:30-12:00"), new OpeningHours("15:30-16:30")));
            put("General Public", Arrays.asList(new OpeningHours("08:30-12:00"), new OpeningHours("15:30-16:30")));
        }};
        foodServices.put("Central Bar", new FoodService("Central Bar", "Alameda", bars, 38.736606, -9.139532, "CentralBar"));
        foodServices.put("Civil Bar", new FoodService("Civil Bar", "Alameda", bars, 38.736988, -9.139955, "CivilBar"));
        foodServices.put("Civil Cafeteria", new FoodService("Civil Cafeteria", "Alameda", cafeCivTag, 38.737650, -9.140384, "CivilCafeteria"));
        foodServices.put("Sena Pastry Shop", new FoodService("Sena Pastry Shop", "Alameda", sena, 38.737677, -9.138672, "Sena"));
        foodServices.put("Mechy Bar", new FoodService("Mechy Bar", "Alameda", bars, 38.737247, -9.137434, "MechyBar"));
        foodServices.put("AEIST Bar", new FoodService("AEIST Bar", "Alameda", bars, 38.736542, -9.137226, "AEISTBar"));
        foodServices.put("AEIST Esplanade", new FoodService("AEIST Esplanade", "Alameda", bars, 	38.736318, -9.137820, "AEISTEsplanade"));
        foodServices.put("Chemy Bar", new FoodService("Chemy Bar", "Alameda", bars, 38.736240, -9.138302, "ChemyBar"));
        foodServices.put("SAS Cafeteria", new FoodService("SAS Cafeteria", "Alameda", sas, 38.736571, -9.137036, "SASCafeteria"));
        foodServices.put("Math Cafeteria", new FoodService("Math Cafeteria", "Alameda", mathCafe, 38.735508, -9.139645, "MathCafeteria"));
        foodServices.put("Complex Bar", new FoodService("Complex Bar", "Alameda", complexBar, 38.736050, -9.140156, "ComplexBar"));
        foodServices.put("Tagus Cafeteria", new FoodService("Tagus Cafeteria", "Taguspark", cafeCivTag, 38.737802, -9.303223, "TagusCafeteria"));
        foodServices.put("Red Bar", new FoodService("Red Bar", "Taguspark", redBar, 38.736546, -9.302207, "RedBar"));
        foodServices.put("Green Bar", new FoodService("Green Bar", "Taguspark", greenBar, 38.738004, -9.303058, "GreenBar"));
        foodServices.put("CTN Cafeteria", new FoodService("CTN Cafeteria", "CTN", ctnCafe, 38.812522, -9.093773, "CTNCafeteria"));
        foodServices.put("CTN Bar", new FoodService("CTN Bar", "CTN", ctnBar, 38.812522, -9.093773, "CTNBar"));
    }

    @Override
    public void getBeacons(GetBeaconsRequest request, StreamObserver<BeaconDto> responseObserver) {
        System.out.println("$ GetBeacons " + request.getCampus());
        foodServices.values().forEach(fs -> {
            if (fs.getCampus().equals(request.getCampus()))
                responseObserver.onNext(BeaconDto
                        .newBuilder()
                        .setBeaconName(fs.getBeaconName())
                        .setFoodServiceName(fs.getName())
                        .build());
        });
        responseObserver.onCompleted();
    }

    @Override
    public void getFoodServices(GetFoodServicesRequest request, StreamObserver<FoodServiceDto> responseObserver) {
        // LocalTime current = LocalTime.now(ZoneId.of("GMT+1"));
        System.out.println("$ GetFoodServices " + request.getCampus());
        LocalTime current = LocalTime.of(14, 30);
        foodServices.values().forEach(fs -> {
            if (fs.getCampus().equals(request.getCampus())
                    && fs.isAvailable(request.getStatus(), current))
                responseObserver.onNext(FoodServiceDto
                        .newBuilder()
                        .setName(fs.getName())
                        .addAllOpeningHours(fs.getOpeningHours(request.getStatus()))
                        .setRating(fs.getRating())
                        .addAllCategories(fs.getCategories())
                        .setQueueTime(fs.getQueueWaitTime())
                        .setLatitude(fs.getLatitude())
                        .setLongitude(fs.getLongitude())
                        .setBeaconName(fs.getBeaconName())
                        .build());
        });
        responseObserver.onCompleted();
    }

    @Override
    public void getFoodService(GetFoodServiceRequest request, StreamObserver<FoodServiceDto> responseObserver) {
        System.out.println("$ GetFoodService " + request.getFoodServiceName());
        FoodService fs = foodServices.get(request.getFoodServiceName());
        if (fs != null) {
            responseObserver.onNext(FoodServiceDto
                    .newBuilder()
                    .setName(fs.getName())
                    .addAllOpeningHours(fs.getOpeningHours(request.getStatus()))
                    .setRating(fs.getRating())
                    .addAllCategories(fs.getCategories())
                    .setQueueTime(fs.getQueueWaitTime())
                    .setLatitude(fs.getLatitude())
                    .setLongitude(fs.getLongitude())
                    .setBeaconName(fs.getBeaconName())
                    .build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getDishes(GetDishesRequest request, StreamObserver<DishDto> responseObserver) {
        System.out.println("$ GetDishes " + request.getFoodServiceName());
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
    public void getDishesPhotos(GetDishesPhotosRequest request, StreamObserver<GetDishesPhotosResponse> responseObserver) {
        System.out.println("$ GetDishesPhotos " + request.getNumberOfPhotos());
        for (FoodService fs : foodServices.values()) {
            for (Dish d : fs.getMenu().values()) {
                if (d.hasPhotos()) {
                    responseObserver.onNext(GetDishesPhotosResponse
                            .newBuilder()
                            .setCampus(fs.getCampus())
                            .setFoodServiceName(fs.getName())
                            .setDish(DishWithPhotosDto
                                    .newBuilder()
                                    .setName(d.getName())
                                    .addAllPhotos(d.getFirstPhotos(request.getNumberOfPhotos()))
                                    .build())
                            .build()
                    );
                }
            }
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getDish(GetDishRequest request, StreamObserver<DishWithPhotosDto> responseObserver) {
        System.out.println("$ GetDish " + request.getFoodServiceName());
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
        System.out.println("$ PutDish " + request.getFoodServiceName());
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
        System.out.println("$ PutDishPhoto " + request.getFoodServiceName());
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
        System.out.println("$ PutDishRating " + request.getFoodServiceName());
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
        System.out.println("$ AddToServiceQueue " + request.getFoodServiceName());
        FoodService foodService = foodServices.get(request.getFoodServiceName());
        if (foodService != null) {
            foodService.addToQueue(request.getUUID());
        }
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void removeFromFoodServiceQueue(RemoveFromFoodServiceQueueRequest request, StreamObserver<Empty> responseObserver) {
        System.out.println("$ RemoveFromServiceQueue " + request.getFoodServiceName());
        FoodService foodService = foodServices.get(request.getFoodServiceName());
        if (foodService != null) {
            foodService.removeFromQueue(request.getUUID());
        }
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }
}
