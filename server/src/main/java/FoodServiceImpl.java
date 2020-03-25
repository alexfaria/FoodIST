import io.grpc.stub.StreamObserver;
import pt.ulisboa.tecnico.cmov.foodservice.*;

import java.util.ArrayList;
import java.util.List;

public class FoodServiceImpl extends FoodServerGrpc.FoodServerImplBase {

    private List<FoodService> foodServices = new ArrayList<>();

    public FoodServiceImpl() {
        foodServices.add(FoodService.newBuilder().setName("Cantina").setOpeningHours("12:30-14:30, 19:30-21:30").build());
        foodServices.add(FoodService.newBuilder().setName("Bar de Civil").setOpeningHours("8:00-22:00").build());
        foodServices.add(FoodService.newBuilder().setName("Bar de Matemática").setOpeningHours("10:00-20:00").build());
        foodServices.add(FoodService.newBuilder().setName("Bar do Pavilhão Central").setOpeningHours("8:00-22:00").build());
        foodServices.add(FoodService.newBuilder().setName("Bar de Mecânica").setOpeningHours("9:00-16:00").build());
        foodServices.add(FoodService.newBuilder().setName("Bar da Associação de Estudantes").setOpeningHours("8:00-20:00").build());
    }

    @Override
    public void getFoodServices(Empty request, StreamObserver<FoodService> responseObserver) {
        System.out.println("$ GetFoodServices");
        foodServices.forEach(responseObserver::onNext);
        responseObserver.onCompleted();
    }

    @Override
    public void getDishes(GetDishRequest request, StreamObserver<Dish> responseObserver) {
        System.out.println("$ GetDishes for " + request.getFoodServiceName());

        responseObserver.onCompleted();
    }
}
