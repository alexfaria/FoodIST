package pt.ulisboa.tecnico.cmov.foodist.view.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.GeoApiContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.model.Dish;
import pt.ulisboa.tecnico.cmov.foodist.view.App;
import pt.ulisboa.tecnico.cmov.foodist.view.adapter.FoodMenuAdapter;
import pt.ulisboa.tecnico.cmov.foodist.view.viewmodel.DishViewModel;
import pt.ulisboa.tecnico.cmov.foodist.view.viewmodel.FoodServiceViewModel;

import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.NAVHOST_ARGS_DISH_NAME;
import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.NAVHOST_ARGS_FOODSERVICE_NAME;
import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.SHARED_PREFERENCES_CAMPUS_KEY;
import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.SHARED_PREFERENCES_DIETARY_PREFERENCES;
import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.SHARED_PREFERENCES_STATUS_KEY;

public class FoodServiceFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private static final int MAP_ZOOM_LEVEL = 17;

    // Maps
    private int mapLayoutState = 0;
    private MapView mapView;
    private GoogleMap googleMap;
    private GeoApiContext mGeoApiContext = null;

    private FoodServiceViewModel foodServiceViewModel;
    private SharedPreferences sharedPreferences;

    // Dish
    private FoodMenuAdapter adapter;
    private RecyclerView recyclerView;
    private DishViewModel dishViewModel;
    private RecyclerView.LayoutManager layoutManager;
    private boolean showAll = false;
    private Switch showAllSwitch;

    // Toasts
    private Toast notAvailable;
    private Toast filtered;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isAdded()) {
            foodServiceViewModel = new ViewModelProvider(requireActivity()).get(FoodServiceViewModel.class);
            foodServiceViewModel.init((App) getContext().getApplicationContext());

            dishViewModel = new ViewModelProvider(requireActivity()).get(DishViewModel.class);
            dishViewModel.init((App) getContext().getApplicationContext());

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_food_service, container, false);

        mapView = view.findViewById(R.id.foodServiceMap);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        adapter = new FoodMenuAdapter(v -> {
            Bundle args = getArguments();
            TextView dishName = v.findViewById(R.id.dishName);
            args.putString(NAVHOST_ARGS_DISH_NAME, dishName.getText().toString());
            NavHostFragment
                    .findNavController(FoodServiceFragment.this)
                    .navigate(R.id.action_FoodMenu_to_Dish, args);
        });

        recyclerView = view.findViewById(R.id.menu_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);

        view.findViewById(R.id.addMenuBtn).setOnClickListener(v -> {
            NavHostFragment
                    .findNavController(FoodServiceFragment.this)
                    .navigate(R.id.action_FoodMenu_to_AddToMenu, getArguments());
        });

        notAvailable = Toast.makeText(getContext(), "There are currently no dishes available!", Toast.LENGTH_LONG);
        filtered = Toast.makeText(getContext(), "Some dishes were filtered!", Toast.LENGTH_SHORT);
        showAllSwitch = view.findViewById(R.id.showAllSwitch);
        showAllSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked)
                filtered.show();
            else
                filtered.cancel();
            getDishes();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String foodServiceName = getArguments().getString(NAVHOST_ARGS_FOODSERVICE_NAME);
        if (foodServiceName != null) {

            String campus = sharedPreferences.getString(SHARED_PREFERENCES_CAMPUS_KEY, getString(R.string.default_campus));
            String status = sharedPreferences.getString(SHARED_PREFERENCES_STATUS_KEY, getString(R.string.default_status));
            Set<String> dietaryPreferences = new HashSet<>(sharedPreferences.getStringSet(SHARED_PREFERENCES_DIETARY_PREFERENCES, new HashSet<>()));

            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }

            Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
            toolbar.setTitle(foodServiceName);
            toolbar.setNavigationOnClickListener(_view -> NavHostFragment
                    .findNavController(FoodServiceFragment.this)
                    .popBackStack());

            foodServiceViewModel.getFoodService(campus, status, foodServiceName).observe(this, fs -> {
                if (googleMap != null) {
                    LatLng foodServiceCoord = new LatLng(fs.getLatitude(), fs.getLongitude());
                    MarkerOptions marker = new MarkerOptions().position(foodServiceCoord);
                    marker.title(fs.getName());
                    marker.snippet(fs.getOpeningHours());
                    marker.visible(true);
                    googleMap.addMarker(marker).showInfoWindow();
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(foodServiceCoord, MAP_ZOOM_LEVEL));
                }
            });

            getDishes();
        }
    }


    private void getDishes() {
        String foodServiceName = getArguments().getString(NAVHOST_ARGS_FOODSERVICE_NAME);
        Set<String> dietaryPreferences = new HashSet<>(sharedPreferences.getStringSet(SHARED_PREFERENCES_DIETARY_PREFERENCES, new HashSet<>()));
        if (foodServiceName != null) {
            dishViewModel.getDishes(foodServiceName).observe(this, data -> {
                if (data != null && data.size() > 0) {
                    notAvailable.cancel();
                    if (showAllSwitch.getVisibility() == View.VISIBLE && showAllSwitch.isChecked()) {
                        adapter.setData(data);
                    } else {
                        ArrayList<Dish> filteredData = new ArrayList<>();
                        for (Dish d : data) {
                            for (String preference : dietaryPreferences) {
                                if (Integer.parseInt(preference) == d.getCategory()) {
                                    filteredData.add(d);
                                    break;
                                }
                            }
                        }

                        if (filteredData.size() != data.size()) {
                            filtered.setGravity(Gravity.CENTER, 0, 0);
                            filtered.show();
                            showAllSwitch.setVisibility(View.VISIBLE);
                        }
                        adapter.setData(filteredData);
                    }
                } else {
                    notAvailable.setGravity(Gravity.CENTER, 0, 0);
                    notAvailable.show();
                }
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.google_maps_key)).build();
        }
        googleMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        getDishes();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onInfoWindowClick(final Marker marker) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Open Google Maps?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        String latitude = String.valueOf(marker.getPosition().latitude);
                        String longitude = String.valueOf(marker.getPosition().longitude);
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude + "&mode=w");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");

                        try {
                            if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivity(mapIntent);
                            }
                        } catch (NullPointerException e) {
                            Log.e("directions", "onClick: NullPointerException: Couldn't open map." + e.getMessage());
                            Toast.makeText(getActivity(), "Couldn't open map", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
