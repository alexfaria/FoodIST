import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Scanner;

import org.conscrypt.Conscrypt;
import java.security.Security;

public class FoodServer {

    public static void main(String[] args) throws IOException, ParseException {
        File certChainFile = new File("ssl/server.crt");
        File privateKeyFile = new File("ssl/server.key");

        Security.insertProviderAt(
                Conscrypt.newProviderBuilder().provideTrustManager(false).build(), 1);

        FoodServiceImpl service = new FoodServiceImpl();
        Server svc = ServerBuilder
                .forPort(8443)
                .useTransportSecurity(certChainFile, privateKeyFile)
                .addService(service)
                .build()
                .start();

        System.out.println("Server started");

        Scanner scanner = new Scanner(System.in);
        while (!scanner.next().equals("shutdown"));
        svc.shutdown();
    }
}
