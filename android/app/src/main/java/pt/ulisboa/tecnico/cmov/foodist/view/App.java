package pt.ulisboa.tecnico.cmov.foodist.view;

import android.app.Application;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.cmov.foodist.repository.cache.BitmapCache;
import pt.ulisboa.tecnico.cmov.foodist.repository.server.FoodServer;

public class App extends Application {

    private final String HOST = "10.0.2.2";
    private final int PORT = 8080;

    private FoodServer foodServer;
    private BitmapCache bitmapCache;

    @Override
    public void onCreate() {
        super.onCreate();
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(HOST, PORT)
                .usePlaintext()
                .build();
        foodServer = new FoodServer(channel);
        bitmapCache = new BitmapCache(this);
    }

    public FoodServer getServer() {
        return foodServer;
    }

    public BitmapCache getBitmapCache() {
        return bitmapCache;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        bitmapCache.close();
    }
}
