import io.grpc.stub.StreamObserver;
import model.Dish;
import model.FoodService;
import pt.ulisboa.tecnico.cmov.foodservice.*;

import java.util.HashMap;

public class FoodServiceImpl extends FoodServerGrpc.FoodServerImplBase {

    private HashMap<String, FoodService> foodServices = new HashMap<>();

    public FoodServiceImpl() {
        foodServices.put("Cantina Social", new FoodService("Cantina Social", "Alameda", "9:00-21:00", 38.736382, -9.136967));
        foodServices.put("Cantina de Civil", new FoodService("Cantina de Civil", "Alameda","9:00-21:00", 38.737732, -9.140482));
        foodServices.put("Bar de Civil", new FoodService("Bar de Civil", "Alameda","8:00-20:00", 38.737066, -9.140007));
        foodServices.put("Bar de Matemática", new FoodService("Bar de Matemática", "Alameda","10:00-20:00", 38.735610, -9.139690));
        foodServices.put("Bar de Química", new FoodService("Bar de Química", "Alameda","8:00-18:00", 38.736012, -9.138324));
        foodServices.put("Bar do Pavilhão Central", new FoodService("Bar do Pavilhão Central", "Alameda","8:00-22:00", 38.736610, -9.139605));
        foodServices.put("Bar de Mecânica", new FoodService("Bar de Mecânica", "Alameda","9:00-16:00", 38.737422, -9.137403));
        foodServices.put("Bar da AEIST", new FoodService("Bar da AEIST", "Alameda","8:00-00:00", 38.736382, -9.136967));
        foodServices.put("Bar da Bola AEIST", new FoodService("Bar da Bola AEIST", "Alameda","8:00-18:00", 38.736131, -9.137807));
        foodServices.put("Restaurante/Bar Sena", new FoodService("Restaurante/Bar Sena", "Alameda","8:00-19:00", 38.737715, -9.138638));
        foodServices.put("Cantina", new FoodService("Cantina", "Taguspark", "9:00-21:00", 38.736902, -9.302608));
        foodServices.put("Snack/Bar Praxe Bar", new FoodService("Snack/Bar Praxe Bar", "Taguspark","8:00-18:00",38.736902, -9.302608));
        foodServices.put("Restaurante/Bar Campus do Taguspark", new FoodService("Restaurante/Bar Campus do Taguspark", "Taguspark","8:00-22:00",38.736563, -9.302200));
    }

    @Override
    public void getFoodServices(GetFoodServicesRequest request, StreamObserver<FoodServiceDto> responseObserver) {
        System.out.println("$ GetFoodServices");
        foodServices.values().forEach(fs -> {
            if (fs.getCampus().equals(request.getCampus()))
                responseObserver.onNext(FoodServiceDto
                        .newBuilder()
                        .setName(fs.getName())
                        .setOpeningHours(fs.getOpeningHours())
                        .setLatitude(fs.getLatitude())
                        .setLongitude(fs.getLongitude())
                        .build());
        });
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
                    .setCost(dish.getCost());
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
        if (foodService != null)
            success = foodService.addMenuItem(new Dish(request.getDishName(), request.getDishCost()));
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

}
