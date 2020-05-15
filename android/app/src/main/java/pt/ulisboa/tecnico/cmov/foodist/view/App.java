package pt.ulisboa.tecnico.cmov.foodist.view;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.google.protobuf.ByteString;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.model.Beacon;
import pt.ulisboa.tecnico.cmov.foodist.model.Dish;
import pt.ulisboa.tecnico.cmov.foodist.repository.cache.BitmapCache;
import pt.ulisboa.tecnico.cmov.foodist.repository.server.ChannelBuilder;
import pt.ulisboa.tecnico.cmov.foodist.repository.server.FoodServer;
import pt.ulisboa.tecnico.cmov.foodist.service.OnPeersChangedListener;
import pt.ulisboa.tecnico.cmov.foodist.service.SimWifiP2pBroadcastReceiver;
import pt.ulisboa.tecnico.cmov.foodist.view.activities.MainActivity;
import pt.ulisboa.tecnico.cmov.foodservice.DishWithPhotosDto;
import pt.ulisboa.tecnico.cmov.foodservice.GetDishesPhotosResponse;

import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.INTENT_NOTIFICATION;
import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.NAVHOST_ARGS_FOODSERVICE_NAME;
import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.SHARED_PREFERENCES_CAMPUS_KEY;
import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.SHARED_PREFERENCES_STATUS_KEY;
import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.SHARED_PREFERENCES_UUID_KEY;

public class App extends Application implements SharedPreferences.OnSharedPreferenceChangeListener, SimWifiP2pManager.PeerListListener, OnPeersChangedListener {

    // private final String HOST = "192.168.1.41";
    private final String HOST = "server.foodist.com";
    private final int PORT = 8443;

    private boolean isConnected = false;
    private FoodServer foodServer;
    private BitmapCache bitmapCache;
    private static final int NUMBER_OF_PREFETCH_ITEMS = 4;

    private SharedPreferences sharedPreferences;
    private SimWifiP2pBroadcastReceiver mReceiver;
    private SimWifiP2pManager mManager;
    private SimWifiP2pManager.Channel mChannel = null;
    private String uuid;
    private String campus;
    private String status;
    private List<Beacon> beacons;
    private Beacon connectedBeacon;

    // Notifications
    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static int connectingNotificationId = 1;
    private static int leavingNotificationId = 2;
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder connectingNotificationBuilder;
    private NotificationCompat.Builder leavingNotificationBuilder;

    @Override
    public void onCreate() {
        super.onCreate();
        foodServer = null;
        bitmapCache = new BitmapCache(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        uuid = sharedPreferences.getString(SHARED_PREFERENCES_UUID_KEY, "");
        campus = sharedPreferences.getString(SHARED_PREFERENCES_CAMPUS_KEY, getString(R.string.default_campus));
        status = sharedPreferences.getString(SHARED_PREFERENCES_STATUS_KEY, getString(R.string.default_status));
        if (uuid.isEmpty()) {
            uuid = UUID.randomUUID().toString();
            sharedPreferences.edit().putString("uuid", uuid).apply();
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

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
/*
                    ManagedChannel channel = ManagedChannelBuilder
                            .forAddress(HOST, PORT)
                            .usePlaintext()
                            .build();
*/
                    ManagedChannel channel = null;
                    InputStream is = null;
                    Log.d("idk", "criar servidor");
                    try {
                        is = getResources().getAssets().open("server.crt"); // devia ser CA mas pronto n sei fazer
                        channel = ChannelBuilder.buildTls(HOST, PORT, is);
                        is.close();
                    } catch (Throwable e) {
                        isConnected = false;
                        e.printStackTrace();
                    }

                    foodServer = new FoodServer(channel);
                    retrieveBeacons();
                }
                if (!connectivityManager.isActiveNetworkMetered())
                // Fetch first photos for all dishes
                {
                    prefetchPhotos();
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

        // register broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        mReceiver = new SimWifiP2pBroadcastReceiver(this);
        registerReceiver(mReceiver, filter);

        Intent intent = new Intent(getApplicationContext(), SimWifiP2pService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        createNotificationChannel();
        notificationManager = NotificationManagerCompat.from(this);

        //TODO: hardcoded strings
        connectingNotificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.mipmap.ic_launcher))
                .setContentTitle("Joined queue")
                .setContentText("Please add some menu items")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        leavingNotificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.mipmap.ic_launcher))
                .setContentTitle("Left queue")
                .setContentText("Please take and upload photos of your plate")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
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

    private void prefetchPhotos() {
        FoodServer.serverExecutor.execute(() -> {
            Iterator<GetDishesPhotosResponse> dishesPhotos = foodServer.getDishesPhotos(NUMBER_OF_PREFETCH_ITEMS);
            while (dishesPhotos.hasNext()) {
                GetDishesPhotosResponse dto = dishesPhotos.next();
                DishWithPhotosDto dishDto = dto.getDish();
                int i = 0;
                for (ByteString photoBytes : dishDto.getPhotosList()) {
                    Bitmap photo = BitmapFactory.decodeByteArray(photoBytes.toByteArray(), 0, photoBytes.size());
                    bitmapCache.put(Dish.generateKeyFrom(dto.getFoodServiceName(), dishDto.getName()) + i++, photo);
                }
            }
        });
    }

    private void retrieveBeacons() {
        if (isConnected && !campus.isEmpty() && !status.isEmpty()) {
            FoodServer.serverExecutor.execute(() -> {
                beacons = foodServer.getBeacons(campus, status);
            });
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SHARED_PREFERENCES_CAMPUS_KEY)) {
            campus = sharedPreferences.getString(SHARED_PREFERENCES_CAMPUS_KEY, getString(R.string.default_campus));
            retrieveBeacons();
        } else if (key.equals(SHARED_PREFERENCES_STATUS_KEY)) {
            status = sharedPreferences.getString(SHARED_PREFERENCES_STATUS_KEY, getString(R.string.default_status));
            retrieveBeacons();
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel_name);
            String description = getString(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mManager = new SimWifiP2pManager(new Messenger(service));
            mChannel = mManager.initialize(getApplicationContext(), getMainLooper(), null);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mManager = null;
            mChannel = null;
        }
    };

    @Override
    public void onPeersChanged() {
        mManager.requestPeers(mChannel, this);
    }

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        // list of devices in range
        if (beacons == null) {
            return;
        }

        for (SimWifiP2pDevice device : peers.getDeviceList()) {

            if (connectedBeacon == null) {

                for (Beacon beacon : beacons) {
                    if (beacon.getBeaconName().equals(device.deviceName)) {

                        // connecting to a new device
                        connectedBeacon = beacon;
                        FoodServer.serverExecutor.execute(() -> foodServer.addToFoodServiceQueue(campus, beacon.getFoodServiceName(), uuid));

                        // notificationId is a unique int for each notification that you must define
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra(NAVHOST_ARGS_FOODSERVICE_NAME, connectedBeacon.getFoodServiceName());
                        intent.putExtra(INTENT_NOTIFICATION, true);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                        //TODO: hardcoded strings
                        connectingNotificationBuilder
                                .setContentTitle("Joined queue @ " + connectedBeacon.getFoodServiceName())
                                .addAction(R.drawable.common_google_signin_btn_icon_dark, "Open app",
                                        pendingIntent);

                        notificationManager.notify(connectingNotificationId, connectingNotificationBuilder.build());
                        return;
                    }
                }

            } else {
                // already connected to one => see if its in the peer list
                // => true -> do nothing
                // => false -> remove from queue
                if (connectedBeacon.getBeaconName().equals(device.deviceName)) {
                    return;
                }
            }
        }

        if (connectedBeacon != null) {
            FoodServer.serverExecutor.execute(() -> {
                foodServer.removeFromFoodServiceQueue(campus, connectedBeacon.getFoodServiceName(), uuid);

                // notificationId is a unique int for each notification that you must define
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra(NAVHOST_ARGS_FOODSERVICE_NAME, connectedBeacon.getFoodServiceName());
                intent.putExtra(INTENT_NOTIFICATION, true);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                //TODO: hardcoded strings
                leavingNotificationBuilder
                        .setContentTitle("Left queue @ " + connectedBeacon.getFoodServiceName())
                        .addAction(R.drawable.common_google_signin_btn_icon_dark, "Open app",
                                pendingIntent);
                notificationManager.notify(leavingNotificationId, leavingNotificationBuilder.build());
                connectedBeacon = null;
            });
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        bitmapCache.close();
        unregisterReceiver(mReceiver);

        // leave queue on terminate (?)
        if (connectedBeacon != null) {
            FoodServer.serverExecutor.execute(() -> {
                foodServer.removeFromFoodServiceQueue(campus, connectedBeacon.getFoodServiceName(), uuid);
                notificationManager.notify(leavingNotificationId, leavingNotificationBuilder.build());
                connectedBeacon = null;
            });
        }

        unbindService(mConnection);
    }
}
