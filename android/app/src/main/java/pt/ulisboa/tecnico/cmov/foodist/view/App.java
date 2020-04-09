package pt.ulisboa.tecnico.cmov.foodist.view;

import android.app.Application;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class App extends Application {

    private final String HOST = "10.0.2.2";
    private final int PORT = 8080;

    private ManagedChannel mChannel;

    @Override
    public void onCreate() {
        super.onCreate();
        mChannel = ManagedChannelBuilder
                .forAddress(HOST, PORT)
                .usePlaintext()
                .build();
    }

    public ManagedChannel getChannel() {
        return mChannel;
    }
}
