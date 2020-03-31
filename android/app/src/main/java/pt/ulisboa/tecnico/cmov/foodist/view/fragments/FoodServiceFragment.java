package pt.ulisboa.tecnico.cmov.foodist.view.fragments;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.view.animation.ViewWeightAnimation;

public class FoodServiceFragment extends Fragment implements OnMapReadyCallback {

    private static final int MAP_LAYOUT_STATE_CONTRACTED = 0;
    private static final int MAP_LAYOUT_STATE_EXPANDED = 1;
    private int mapLayoutState = 0;

    // FoodServiceInfo
    private ConstraintLayout foodServiceContainer;

    // Maps
    private RelativeLayout mapContainer;
    private MapView mapView;
    private GoogleMap googleMap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        view.findViewById(R.id.fullScreenBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapLayoutState == MAP_LAYOUT_STATE_CONTRACTED){
                    mapLayoutState = MAP_LAYOUT_STATE_EXPANDED;
                    expandMapAnimation();
                }
                else if(mapLayoutState == MAP_LAYOUT_STATE_EXPANDED){
                    mapLayoutState = MAP_LAYOUT_STATE_CONTRACTED;
                    contractMapAnimation();
                }
            }
        });
        return view;
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng foodServiceCoord = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions().position(foodServiceCoord).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(foodServiceCoord));
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

    private void expandMapAnimation(){
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

    private void contractMapAnimation(){
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
}
