package pt.ulisboa.tecnico.cmov.foodist.view.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.model.FoodService;
import pt.ulisboa.tecnico.cmov.foodist.service.OnPeersChangedListener;
import pt.ulisboa.tecnico.cmov.foodist.service.SimWifiP2pBroadcastReceiver;
import pt.ulisboa.tecnico.cmov.foodist.view.App;
import pt.ulisboa.tecnico.cmov.foodist.view.viewmodel.FoodServiceViewModel;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, OnSuccessListener<Location>, SimWifiP2pManager.PeerListListener, OnPeersChangedListener {

    private final int PERMISSION_ID = 44;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SharedPreferences sharedPreferences;
    private FoodServiceViewModel viewModel;

    private SimWifiP2pBroadcastReceiver mReceiver;
    private SimWifiP2pManager mManager;
    private SimWifiP2pManager.Channel mChannel = null;
    private String connectedFoodService;

    private String campus;
    private List<String> foodServicesNames = new ArrayList<>();

    public Location getmUserLocation() {
        return mUserLocation;
    }

    private Location mUserLocation;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        viewModel = new ViewModelProvider(this).get(FoodServiceViewModel.class);
        viewModel.init((App) getApplicationContext());

        campus = sharedPreferences.getString("campus", "");
        if (!campus.isEmpty())
            retrieveFoodServicesNames(campus);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setNumUpdates(1);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null) return;
                onSuccess(locationResult.getLastLocation());
            }
        };

        toolbar.setTitle("Técnico " + sharedPreferences.getString("campus", ""));

        // register broadcast receiver
        /*
        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        mReceiver = new SimWifiP2pBroadcastReceiver(this);
        registerReceiver(mReceiver, filter);

        Intent intent = new Intent(getApplicationContext(), SimWifiP2pService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
         */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            Log.i("MainActivity", "Launch SettingsActivity");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("campus")) {
            campus = sharedPreferences.getString("campus", "");
            retrieveFoodServicesNames(campus);
            toolbar.setTitle("Técnico " + campus);
        }
    }

    private void checkCampus() {
        if (sharedPreferences.getString("campus", "").isEmpty()) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Please choose your campus", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null)
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                sharedPreferences.edit().putBoolean("localization", false).apply();
                checkCampus();
            }
        }
    }

    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this);
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @Override
    public void onSuccess(Location location) {
        if (location != null) {
            mUserLocation = location;
            Log.d("MainActivity", "Location latitude: " + location.getLatitude());
            Log.d("MainActivity", "Location longitude: " + location.getLongitude());
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                Address addr = address.get(0);
                Log.d("MainActivity", "Location: " + addr.getPostalCode());
                switch (addr.getPostalCode()) {
                    case "1049-001":
                        Log.d("MainActivity", "Located in Técnico Alameda");
                        sharedPreferences.edit().putString("campus", "Alameda").apply();
                        break;
                    case "2744-016":
                        Log.d("MainActivity", "Located in Técnico Taguspark");
                        sharedPreferences.edit().putString("campus", "Taguspark").apply();
                        break;
                    default:
                        Log.d("MainActivity", "Located in " + addr.getFeatureName());
                        checkCampus();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPreferences.getBoolean("localization", true))
            getLastLocation();
        else
            checkCampus();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mManager = new SimWifiP2pManager(new Messenger(service));
            mChannel = mManager.initialize(getApplication(), getMainLooper(), null);
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
        if (foodServicesNames == null) return;
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            if (connectedFoodService == null)
                if (foodServicesNames.contains(device.deviceName)) {
                    viewModel.addToFoodServiceQueue(campus, device.deviceName);
                    connectedFoodService = device.deviceName; // ToDo change device name to handle spaces
                    return;
                }
            else
                if (connectedFoodService.equals(device.deviceName))
                    return;
        }
        viewModel.removeFromFoodServiceQueue(campus, connectedFoodService);
        connectedFoodService = null;
    }

    private void retrieveFoodServicesNames(String campus) {
        viewModel.getFoodServices(campus).observe(this, foodServices -> {
            for(FoodService fs: foodServices)
                foodServicesNames.add(fs.getName());
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager
                .getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        //unregisterReceiver(mReceiver);
    }
}
