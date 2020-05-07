package pt.ulisboa.tecnico.cmov.foodist.view;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import java.util.UUID;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.cmov.foodist.repository.cache.BitmapCache;
import pt.ulisboa.tecnico.cmov.foodist.repository.server.FoodServer;

public class App extends Application {

    private final String HOST = "10.0.2.2";
    private final int PORT = 8080;

    private boolean isConnected = false;

    private FoodServer foodServer;
    private BitmapCache bitmapCache;

    @Override
    public void onCreate() {
        super.onCreate();
        foodServer = null;
        bitmapCache = new BitmapCache(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String uuid = sharedPreferences.getString("uuid", "");

        if (uuid.isEmpty()) {
            uuid = UUID.randomUUID().toString();
            sharedPreferences.edit().putString("uuid", uuid).apply();
        }

        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();
        connectivityManager.registerNetworkCallback(networkRequest, new ConnectivityManager.NetworkCallback() {
            private int numOfAvailableNetworks = 0;

            @Override
            public void onAvailable(@NonNull Network network) {
                Log.d("App", "Network onAvailable");
                if (numOfAvailableNetworks++ == 0) {
                    isConnected = true;
                    ManagedChannel channel = ManagedChannelBuilder
                            .forAddress(HOST, PORT)
                            .usePlaintext()
                            .build();
                    foodServer = new FoodServer(channel);
                }
                if (!connectivityManager.isActiveNetworkMetered()) {
                    // Fetch first photos for all dishes
                }
            }

            @Override
            public void onLost(@NonNull Network network) {
                Log.d("App", "Network lost: " + network);
                if (--numOfAvailableNetworks == 0) {
                    isConnected = false;
                    foodServer = null;
                }
            }
        });
    }

    public boolean isConnected() {
        return isConnected;
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
