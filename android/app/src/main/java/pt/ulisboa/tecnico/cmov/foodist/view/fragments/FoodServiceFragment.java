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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.GeoApiContext;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.view.App;
import pt.ulisboa.tecnico.cmov.foodist.view.animation.ViewWeightAnimation;
import pt.ulisboa.tecnico.cmov.foodist.view.viewmodel.FoodServiceViewModel;

public class FoodServiceFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private static final String TAG = "directions";
    private static final int MAP_LAYOUT_STATE_CONTRACTED = 0;
    private static final int MAP_LAYOUT_STATE_EXPANDED = 1;
    private static final int MAP_ZOOM_LEVEL = 17;

    // FoodServiceInfo
    private ConstraintLayout foodServiceContainer;
    private TextView name;
    private TextView openingHours;

    // Maps
    private int mapLayoutState = 0;
    private RelativeLayout mapContainer;
    private MapView mapView;
    private GoogleMap googleMap;
    private GeoApiContext mGeoApiContext = null;

    private FoodServiceViewModel viewModel;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isAdded()) {
            viewModel = new ViewModelProvider(requireActivity()).get(FoodServiceViewModel.class);
            viewModel.init((App) getContext().getApplicationContext());
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
        name = view.findViewById(R.id.foodServiceName);
        openingHours = view.findViewById(R.id.foodServiceOpening);

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
        view.findViewById(R.id.menuBtn).setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("foodServiceName", name.getText().toString());
            NavHostFragment
                    .findNavController(FoodServiceFragment.this)
                    .navigate(R.id.action_FoodService_to_FoodMenu, args);
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

            viewModel.getFoodService(campus, status, foodServiceName).observe(this, fs -> {
                name.setText(fs.getName());
                openingHours.setText(fs.getOpeningHours());
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
                            Log.e(TAG, "onClick: NullPointerException: Couldn't open map." + e.getMessage());
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
