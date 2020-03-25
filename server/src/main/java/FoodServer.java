import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.Scanner;

public class FoodServer {

    public static void main(String[] args) throws IOException {
        FoodServiceImpl service = new FoodServiceImpl();
        Server svc = ServerBuilder
                .forPort(8080)
                .addService(service)
                .build()
                .start();
        Scanner scanner = new Scanner(System.in);
        while (!scanner.next().equals("shutdown"));
        svc.shutdown();
    }
}
