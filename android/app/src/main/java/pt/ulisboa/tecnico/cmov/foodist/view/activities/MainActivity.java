package pt.ulisboa.tecnico.cmov.foodist.view.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import pt.ulisboa.tecnico.cmov.foodist.R;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final int LOCATION_CODE = 1000;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        if (sharedPreferences.getString("campus", "").isEmpty())
            checkPermissions();
        toolbar.setTitle("Técnico " + sharedPreferences.getString("campus",""));
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
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Técnico" + sharedPreferences.getString("campus", ""));
        }
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_CODE);
        } else {
            requestLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sharedPreferences.edit().putBoolean("localization", true).apply();
            requestLocation();
        }
    }

    public void requestLocation() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
            boolean success = true;
            if (location != null) {
                try {
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    List<Address> address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    Address addr = address.get(0);
                    Log.d("MainActivity", "Location: " + addr.getPostalCode());
                    switch (addr.getPostalCode()) {
                        case "1049-001":
                            sharedPreferences.edit().putString("campus", "Alameda").apply();
                            break;
                        case "2744-016":
                            sharedPreferences.edit().putString("campus", "Taguspark").apply();
                            break;
                        default:
                            success = false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    success = false;
                }
            }
            if (!success || location == null) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                Toast.makeText(this, "Please choose your campus", Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager
                .getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
