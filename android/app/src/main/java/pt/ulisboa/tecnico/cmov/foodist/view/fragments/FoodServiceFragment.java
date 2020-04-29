package pt.ulisboa.tecnico.cmov.foodist.view.fragments;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.model.FoodService;
import pt.ulisboa.tecnico.cmov.foodist.model.PolylineData;
import pt.ulisboa.tecnico.cmov.foodist.view.App;
import pt.ulisboa.tecnico.cmov.foodist.view.activities.MainActivity;
import pt.ulisboa.tecnico.cmov.foodist.view.animation.ViewWeightAnimation;
import pt.ulisboa.tecnico.cmov.foodist.view.viewmodel.FoodServiceViewModel;

public class FoodServiceFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener, GoogleMap.OnInfoWindowClickListener {
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
    private ArrayList<PolylineData> mPolyLinesData = new ArrayList<>();

    private FoodServiceViewModel viewModel;
    private SharedPreferences sharedPreferences;
    private Location mUserPosition;

    private FoodService foodService;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isAdded()) {
            mUserPosition = ((MainActivity) getActivity()).getmUserLocation();
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
        if (foodServiceName != null)
            viewModel.getFoodService(sharedPreferences.getString("campus", ""), foodServiceName).observe(this, fs -> {
                foodService = fs;
                name.setText(fs.getName());
                openingHours.setText(fs.getOpeningHours());
                if (googleMap != null) {
                    LatLng foodServiceCoord = new LatLng(fs.getLatitude(), fs.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(foodServiceCoord).title(fs.getName()));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(foodServiceCoord, MAP_ZOOM_LEVEL));
                    
                    if (mUserPosition != null) {
                        LatLng userPosition = new LatLng(mUserPosition.getLatitude(), mUserPosition.getLongitude());
                        googleMap.addMarker(new MarkerOptions().position(userPosition).title("You"));

                        calculateDirections(foodServiceCoord);
                    }
                }
            });
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.google_maps_key)).build();
        }
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.setOnPolylineClickListener(this);
    }

    private void calculateDirections(LatLng foodServiceCoord) {
        Log.d(TAG, "calculateDirections:        calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                foodServiceCoord.latitude,
                foodServiceCoord.longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(true);
        directions.mode(TravelMode.WALKING);

        directions.origin(
                new com.google.maps.model.LatLng(
                        mUserPosition.getLatitude(),
                        mUserPosition.getLongitude()
                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage());
            }
        });
    }

    private void addPolylinesToMap(final DirectionsResult result) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);
                if (mPolyLinesData.size() > 0) {
                    for (PolylineData polylineData : mPolyLinesData) {
                        polylineData.getPolyline().remove();
                    }
                    mPolyLinesData.clear();
                    mPolyLinesData = new ArrayList<>();
                }
                double duration = 999999999;
                for (DirectionsRoute route : result.routes) {
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for (com.google.maps.model.LatLng latLng : decodedPath) {

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = googleMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getActivity(), R.color.darkGrey));
                    polyline.setClickable(true);
                    mPolyLinesData.add(new PolylineData(polyline, route.legs[0]));

                    double tempDuration = route.legs[0].duration.inSeconds;
                    if (tempDuration < duration) {
                        duration = tempDuration;
                        onPolylineClick(polyline);
                        zoomRoute(polyline.getPoints());
                    }
                }
            }
        });
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
    public void onPolylineClick(Polyline polyline) {
        int index = 0;
        for (PolylineData polylineData : mPolyLinesData) {
            index++;
            Log.d(TAG, "onPolylineClick: toString: " + polylineData.toString());
            if (polyline.getId().equals(polylineData.getPolyline().getId())) {
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.blue1));
                polylineData.getPolyline().setZIndex(1);

//                LatLng endLocation = new LatLng(
//                        polylineData.getLeg().endLocation.lat,
//                        polylineData.getLeg().endLocation.lng
//                );

                LatLng endLocation = new LatLng(foodService.getLatitude(), foodService.getLongitude());

                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(endLocation)
                        .title(foodService.getName())
                        .snippet("Duration: " + polylineData.getLeg().duration
                        ));

                marker.showInfoWindow();
            } else {
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.darkGrey));
                polylineData.getPolyline().setZIndex(0);
            }
        }
    }


    private void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (googleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 120;
        LatLngBounds latLngBounds = boundsBuilder.build();

        googleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }

    @Override
    public void onInfoWindowClick(final Marker marker) {
        Log.d("onInfoWindowClick", "clicked");
        if (!marker.getTitle().contains("You")) {
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
}
