package pt.ulisboa.tecnico.cmov.foodist.view.fragments;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
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

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.model.Dish;
import pt.ulisboa.tecnico.cmov.foodist.view.App;
import pt.ulisboa.tecnico.cmov.foodist.view.adapter.FoodMenuAdapter;
import pt.ulisboa.tecnico.cmov.foodist.view.animation.ViewWeightAnimation;
import pt.ulisboa.tecnico.cmov.foodist.view.viewmodel.DishViewModel;
import pt.ulisboa.tecnico.cmov.foodist.view.viewmodel.FoodServiceViewModel;

public class FoodServiceFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private static final int MAP_LAYOUT_STATE_CONTRACTED = 0;
    private static final int MAP_LAYOUT_STATE_EXPANDED = 1;
    private static final int MAP_ZOOM_LEVEL = 17;

    // FoodServiceInfo
    private ConstraintLayout foodServiceContainer;

    // Maps
    private int mapLayoutState = 0;
    private RelativeLayout mapContainer;
    private MapView mapView;
    private GoogleMap googleMap;
    private GeoApiContext mGeoApiContext = null;

    private FoodServiceViewModel foodServiceViewModel;
    private SharedPreferences sharedPreferences;

    // Dish
    private RecyclerView recyclerView;
    private FoodMenuAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private DishViewModel dishViewModel;

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

        foodServiceContainer = view.findViewById(R.id.foodServiceContainer);
        mapContainer = view.findViewById(R.id.mapContainer);
        mapView = view.findViewById(R.id.foodServiceMap);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        view.findViewById(R.id.fullScreenBtn).setOnClickListener(v -> {
            if (mapLayoutState == MAP_LAYOUT_STATE_CONTRACTED) {
                mapLayoutState = MAP_LAYOUT_STATE_EXPANDED;
                expandMapAnimation();
            } else if (mapLayoutState == MAP_LAYOUT_STATE_EXPANDED) {
                mapLayoutState = MAP_LAYOUT_STATE_CONTRACTED;
                contractMapAnimation();
            }
        });

        adapter = new FoodMenuAdapter(v -> {
            Bundle args = getArguments();
            TextView dishName = v.findViewById(R.id.dishName);
            args.putString("dishName", dishName.getText().toString());
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

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String foodServiceName = getArguments().getString("foodServiceName");
        if (foodServiceName != null) {

            String campus = sharedPreferences.getString("campus", getString(R.string.default_campus));
            String status = sharedPreferences.getString("status", getString(R.string.default_status));


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

            dishViewModel.getDishes(foodServiceName).observe(this, dishes -> {
                for(Dish dish : dishes) {
                    Log.d("dishes", String.valueOf(dish));
                }
                adapter.setData(dishes);
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

    private void expandMapAnimation() {
        ViewWeightAnimation mapAnimationWrapper = new ViewWeightAnimation(mapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                50,
                100);
        mapAnimation.setDuration(800);

        ViewWeightAnimation recyclerAnimationWrapper = new ViewWeightAnimation(foodServiceContainer);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                50,
                0);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();
    }

    private void contractMapAnimation() {
        ViewWeightAnimation mapAnimationWrapper = new ViewWeightAnimation(mapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                100,
                50);
        mapAnimation.setDuration(800);

        ViewWeightAnimation recyclerAnimationWrapper = new ViewWeightAnimation(foodServiceContainer);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                0,
                50);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();
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
