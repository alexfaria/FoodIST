package pt.ulisboa.tecnico.cmov.foodist.view.fragments;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.model.FoodService;
import pt.ulisboa.tecnico.cmov.foodist.view.activities.MainActivity;
import pt.ulisboa.tecnico.cmov.foodist.view.adapter.FoodServicesAdapter;
import pt.ulisboa.tecnico.cmov.foodist.view.viewmodel.FoodServiceViewModel;

import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.NAVHOST_ARGS_FOODSERVICE_NAME;
import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.SHARED_PREFERENCES_CAMPUS_KEY;
import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.SHARED_PREFERENCES_DIETARY_PREFERENCES;
import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.SHARED_PREFERENCES_STATUS_KEY;

public class DiningOptionsFragment extends Fragment implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        SwipeRefreshLayout.OnRefreshListener {

    private FoodServiceViewModel viewModel;
    private SharedPreferences sharedPreferences;

    private SwipeRefreshLayout refresh;
    private RecyclerView recyclerView;
    private FoodServicesAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Switch showAllSwitch;

    private GeoApiContext geoApiContext;

    private Toast filtered;
    private TextView emptyView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isAdded()) {
            viewModel = new ViewModelProvider(requireActivity()).get(FoodServiceViewModel.class);
            viewModel.init(getContext().getApplicationContext());
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
            geoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.google_maps_key)).build();
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dining_options, container, false);
        refresh = view.findViewById(R.id.dining_options_refresh);
        refresh.setOnRefreshListener(this);
        recyclerView = view.findViewById(R.id.dining_recycler_view);
        emptyView = view.findViewById(R.id.empty_view);

        recyclerView.setHasFixedSize(true);
        adapter = new FoodServicesAdapter(v -> {
            Bundle args = new Bundle();
            TextView serviceName = v.findViewById(R.id.serviceName);
            args.putString(NAVHOST_ARGS_FOODSERVICE_NAME, serviceName.getText().toString());
            NavHostFragment
                    .findNavController(DiningOptionsFragment.this)
                    .navigate(R.id.action_DiningOptions_to_FoodService, args);
        });
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        filtered = Toast.makeText(getContext(), getString(R.string.filtered_services), Toast.LENGTH_SHORT);
        showAllSwitch = view.findViewById(R.id.showAll);
        showAllSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                filtered.show();
            } else {
                filtered.cancel();
            }
            retrieveFoodServices();
        });
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String campus = sharedPreferences.getString(SHARED_PREFERENCES_CAMPUS_KEY, getString(R.string.default_campus));
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.tecnico) + " " + campus);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        retrieveFoodServices();
    }

    @Override
    public void onRefresh() {
        retrieveFoodServices();
        if (refresh.isRefreshing()) {
            refresh.setRefreshing(false);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SHARED_PREFERENCES_CAMPUS_KEY) || key.equals(SHARED_PREFERENCES_STATUS_KEY)) {
            retrieveFoodServices();
        }
    }

    private void retrieveFoodServices() {
        String campus = sharedPreferences.getString(SHARED_PREFERENCES_CAMPUS_KEY, getString(R.string.default_campus));
        String status = sharedPreferences.getString(SHARED_PREFERENCES_STATUS_KEY, getString(R.string.default_status));
        Set<String> dietaryPreferences = new HashSet<>(sharedPreferences.getStringSet(SHARED_PREFERENCES_DIETARY_PREFERENCES, new HashSet<>(
                Arrays.asList(getResources().getStringArray(R.array.dietary_preferences_values)))));
        if (!campus.isEmpty() && !status.isEmpty()) {
            viewModel.getFoodServices(campus, status).observe(this, data -> {
                if (data != null && data.size() > 0) {
                    calculateDirectionsTo(data);
                    adapter.setData(data);
                    if (showAllSwitch.getVisibility() == View.VISIBLE && showAllSwitch.isChecked()) {
                        calculateDirectionsTo(data);
                        adapter.setData(data);
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    } else {
                        List<FoodService> filteredData = new ArrayList<>();
                        for (FoodService fs : data) {
                            if (fs.getCategories().size() == 0) {
                                filteredData.add(fs);
                            } else {
                                for (String preference : dietaryPreferences)
                                    if (fs.getCategories().contains(Integer.parseInt(preference))) {
                                        filteredData.add(fs);
                                        break;
                                    }
                            }
                        }
                        if (filteredData.size() != data.size()) {
                            filtered.setGravity(Gravity.CENTER, 0, 0);
                            filtered.show();
                            showAllSwitch.setVisibility(View.VISIBLE);
                        }

                        recyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);

                        if (filteredData.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                            emptyView.setText(getString(R.string.filtered_services));
                        }

                        calculateDirectionsTo(filteredData);
                        adapter.setData(filteredData);
                    }
                } else {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    emptyView.setText(getString(R.string.no_services));
                }
            });
        }
    }

    private void calculateDirectionsTo(List<FoodService> foodServices) {
        Location location = ((MainActivity) getActivity()).getLocation();
        if (location == null) {
            return;
        }
        int index = 0;
        for (FoodService fs : foodServices) {
            final int i = index++;
            DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);
            directions.mode(TravelMode.WALKING);
            directions.origin(new LatLng(location.getLatitude(), location.getLongitude()));
            directions.destination(new LatLng(fs.getLatitude(), fs.getLongitude())).setCallback(new PendingResult.Callback<DirectionsResult>() {
                @Override
                public void onResult(DirectionsResult result) {
                    Log.d("Directions", "calculateDirections: routes: " + result.routes[0].toString());
                    Log.d("Directions", "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                    Log.d("Directions", "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                    Log.d("Directions", "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                    fs.setWalkTime((int) result.routes[0].legs[0].duration.inSeconds / 60);
                    new Handler(Looper.getMainLooper()).post(() -> adapter.notifyItemChanged(i));
                }

                @Override
                public void onFailure(Throwable e) {
                    Log.e("Directions", "calculateDirections: Failed to get directions: " + e.getMessage());
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}
