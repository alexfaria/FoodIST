package pt.ulisboa.tecnico.cmov.foodist.view.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Locale;

import pt.ulisboa.tecnico.cmov.foodist.R;

import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.INTENT_NOTIFICATION;
import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.INTENT_NOTIFICATION_FOODSERVICE_NAME;
import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.NAVHOST_ARGS_FOODSERVICE_NAME;
import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.SHARED_PREFERENCES_CAMPUS_KEY;
import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.SHARED_PREFERENCES_FIRST_RUN_KEY;
import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.SHARED_PREFERENCES_LANGUAGE_KEY;
import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.SHARED_PREFERENCES_LOCATION_KEY;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, OnSuccessListener<Location> {

    private final int PERMISSION_ID = 44;

    private final int LOCATION_RADIUS = 1000; // 1km radius
    private final Location ALAMEDA = new Location("");
    private final Location TAGUSPARK = new Location("");
    private final Location CTN = new Location("");

    private Location location;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SharedPreferences sharedPreferences;

    private String campus;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        campus = sharedPreferences.getString(SHARED_PREFERENCES_CAMPUS_KEY, getString(R.string.default_campus));
        toolbar.setTitle(getString(R.string.tecnico) + " " + campus);

        ALAMEDA.setLatitude(Double.parseDouble(getString(R.string.alameda_latitude)));
        ALAMEDA.setLongitude(Double.parseDouble(getString(R.string.alameda_longitude)));
        TAGUSPARK.setLatitude(Double.parseDouble(getString(R.string.taguspark_latitude)));
        TAGUSPARK.setLongitude(Double.parseDouble(getString(R.string.taguspark_longitude)));
        CTN.setLatitude(Double.parseDouble(getString(R.string.ctn_latitude)));
        CTN.setLongitude(Double.parseDouble(getString(R.string.ctn_longitude)));

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setNumUpdates(1);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null) {
                    return;
                }
                onSuccess(locationResult.getLastLocation());
            }
        };

        setLocale();

        boolean firstRun = sharedPreferences.getBoolean(SHARED_PREFERENCES_FIRST_RUN_KEY, true);

        if (firstRun) {
            sharedPreferences.edit().putBoolean(SHARED_PREFERENCES_FIRST_RUN_KEY, false).apply();
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            Toast toast = Toast.makeText(this, getString(R.string.toast_settings), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
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
        if (key.equals(SHARED_PREFERENCES_CAMPUS_KEY)) {
            campus = sharedPreferences.getString(SHARED_PREFERENCES_CAMPUS_KEY, getString(R.string.default_campus));
            toolbar.setTitle(getString(R.string.tecnico) + " " + campus);
        } else if (key.equals(SHARED_PREFERENCES_LANGUAGE_KEY)) {
            setLocale();
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
        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sharedPreferences.edit().putBoolean(SHARED_PREFERENCES_LOCATION_KEY, true).apply();
                getLastLocation();
            } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                sharedPreferences.edit().putBoolean(SHARED_PREFERENCES_LOCATION_KEY, false).apply();
            }
        }
    }

    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this);
            } else {
                Toast.makeText(this, getString(R.string.enable_auto_location), Toast.LENGTH_LONG).show();
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
            this.location = location;
            Log.d("MainActivity", "Location latitude: " + location.getLatitude());
            Log.d("MainActivity", "Location longitude: " + location.getLongitude());
            if (location.distanceTo(ALAMEDA) <= LOCATION_RADIUS) {
                Log.d("MainActivity", "Located in Técnico Alameda");
                sharedPreferences.edit().putString(SHARED_PREFERENCES_CAMPUS_KEY, "Alameda").apply();
            } else if (location.distanceTo(TAGUSPARK) <= LOCATION_RADIUS) {
                Log.d("MainActivity", "Located in Técnico Taguspark");
                sharedPreferences.edit().putString(SHARED_PREFERENCES_CAMPUS_KEY, "Taguspark").apply();
            } else if (location.distanceTo(CTN) <= LOCATION_RADIUS) {
                Log.d("MainActivity", "Located in Técnico CTN");
                sharedPreferences.edit().putString(SHARED_PREFERENCES_CAMPUS_KEY, "CTN").apply();
            } else {
                Log.d("MainActivity", "Not close to any campus -> DEFAULT TO ALAMEDA");
            }
        } else {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    public Location getLocation() {
        return location;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPreferences.getBoolean(SHARED_PREFERENCES_LOCATION_KEY, true)) {
            getLastLocation();
        }

        // https://stackoverflow.com/questions/42040079/pendingintent-wont-launch-desired-fragment
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            final boolean intentBeaconAction = extras.getBoolean(INTENT_NOTIFICATION, false);

            if (intentBeaconAction) {
                final String foodServiceName = extras.getString(INTENT_NOTIFICATION_FOODSERVICE_NAME);
                Bundle args = new Bundle();
                args.putString(NAVHOST_ARGS_FOODSERVICE_NAME, foodServiceName);
                Navigation.findNavController(findViewById(R.id.nav_host_fragment))
                        .navigate(R.id.action_DiningOptions_to_FoodService, args);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    // https://www.tutorialspoint.com/how-to-set-locale-programmatically-in-android-app
    private void setLocale(){
        String localeCode = sharedPreferences.getString(SHARED_PREFERENCES_LANGUAGE_KEY, getString(R.string.default_language));
        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(new Locale(localeCode.toLowerCase()));
        resources.updateConfiguration(configuration, displayMetrics);
        configuration.locale = new Locale(localeCode.toLowerCase());
        resources.updateConfiguration(configuration, displayMetrics);
    }
}
