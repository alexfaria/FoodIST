import io.grpc.stub.StreamObserver;
import pt.ulisboa.tecnico.cmov.foodservice.*;

import java.util.ArrayList;
import java.util.List;

public class FoodServiceImpl extends FoodServerGrpc.FoodServerImplBase {

    private List<DiningOptionDto> diningOptions = new ArrayList<>();
    private List<FoodServiceDto> foodServices = new ArrayList<>();

    public FoodServiceImpl() {
        diningOptions.add(DiningOptionDto.newBuilder().setName("Cantina Social").setOpeningHours("9:00-21:00").build()); // 38.736382, -9.136967
        diningOptions.add(DiningOptionDto.newBuilder().setName("Cantina Civil").setOpeningHours("9:00-21:00").build()); // 38.737732, -9.140482
        diningOptions.add(DiningOptionDto.newBuilder().setName("Bar de Civil").setOpeningHours("8:00-22:00").build()); // 38.737066, -9.140007
        diningOptions.add(DiningOptionDto.newBuilder().setName("Bar de Matemática").setOpeningHours("10:00-20:00").build()); // 38.735610, -9.139690
        diningOptions.add(DiningOptionDto.newBuilder().setName("Bar de Química").setOpeningHours("8:00-18:00").build()); // 38.736012, -9.138324
        diningOptions.add(DiningOptionDto.newBuilder().setName("Bar do Pavilhão Central").setOpeningHours("8:00-22:00").build()); // 38.736610, -9.139605
        diningOptions.add(DiningOptionDto.newBuilder().setName("Bar de Mecânica").setOpeningHours("9:00-16:00").build()); // 38.737422, -9.137403
        diningOptions.add(DiningOptionDto.newBuilder().setName("Bar da AEIST").setOpeningHours("8:00-00:00").build()); // 38.736382, -9.136967
        diningOptions.add(DiningOptionDto.newBuilder().setName("Bar da Bola AEIST").setOpeningHours("8:00-18:00").build()); // 38.736131, -9.137807
        diningOptions.add(DiningOptionDto.newBuilder().setName("Restaurante/Bar Sena").setOpeningHours("8:00-19:00").build()); // 38.737715, -9.138638
    }

    @Override
    public void getDiningOptions(GetDiningOptionsRequest request, StreamObserver<DiningOptionDto> responseObserver) {
        System.out.println("$ GetFoodServices");
        diningOptions.forEach(responseObserver::onNext);
        responseObserver.onCompleted();
    }
}
