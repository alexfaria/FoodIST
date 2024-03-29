import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.cmov.foodservice.FoodServerGrpc;

public class FoodClient {

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 8080)
                .usePlaintext()
                .build();
        FoodServerGrpc.FoodServerBlockingStub stub = FoodServerGrpc.newBlockingStub(channel);
    }
}
