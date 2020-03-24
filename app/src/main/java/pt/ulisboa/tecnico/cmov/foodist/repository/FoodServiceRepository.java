package pt.ulisboa.tecnico.cmov.foodist.repository;

import java.util.Arrays;
import java.util.List;

import io.grpc.ManagedChannel;
import pt.ulisboa.tecnico.cmov.foodist.model.DiningOption;
import pt.ulisboa.tecnico.cmov.foodist.model.FoodService;
import pt.ulisboa.tecnico.cmov.foodservice.Empty;
import pt.ulisboa.tecnico.cmov.foodservice.FoodServerGrpc;

public class FoodServiceRepository {

    private final ManagedChannel channel;

    public FoodServiceRepository(ManagedChannel channel) {
        this.channel = channel;
    }

    public List<DiningOption> getFoodServices() {
        /*FoodServerGrpc.FoodServerBlockingStub stub = FoodServerGrpc.newBlockingStub(channel);
        stub.getFoodServices(Empty.newBuilder().build())*/
        return Arrays.asList(
                new DiningOption("Cantina", "12:30-14:30 / 19:30-21:30"),
                new DiningOption("Bar de Civil", "8:00-22:00"),
                new DiningOption("Bar de Matemática", "10:00-20:00"),
                new DiningOption("Bar do Pavilhão Central", "8:00-22:00"),
                new DiningOption("Bar de Mecânica", "9:00-16:00"),
                new DiningOption("Bar da Associação de Estudantes", "8:00-20:00"));
    }
}
