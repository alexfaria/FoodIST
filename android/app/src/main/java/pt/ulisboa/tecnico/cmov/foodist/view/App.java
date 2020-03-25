package pt.ulisboa.tecnico.cmov.foodist.view;

import android.app.Application;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.cmov.foodist.R;

public class App extends Application {

    private final String HOST = "localhost";
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
