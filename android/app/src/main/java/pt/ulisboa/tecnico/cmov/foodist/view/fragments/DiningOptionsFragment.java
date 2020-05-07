package pt.ulisboa.tecnico.cmov.foodist.view.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.model.FoodService;
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

    private Toast notAvailable;
    private Toast filtered;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isAdded()) {
            viewModel = new ViewModelProvider(requireActivity()).get(FoodServiceViewModel.class);
            viewModel.init(getContext().getApplicationContext());
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
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
        filtered = Toast.makeText(getContext(), "Some services were filtered!", Toast.LENGTH_SHORT);
        showAllSwitch = view.findViewById(R.id.showAll);
        showAllSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked)
                filtered.show();
            else
                filtered.cancel();
            retrieveFoodServices();
        });
        notAvailable = Toast.makeText(getContext(), "There are currently no services available!", Toast.LENGTH_LONG);
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String campus = sharedPreferences.getString(SHARED_PREFERENCES_CAMPUS_KEY, getString(R.string.default_campus));
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Técnico " + campus);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        retrieveFoodServices();
    }

    @Override
    public void onRefresh() {
        retrieveFoodServices();
        if (refresh.isRefreshing())
            refresh.setRefreshing(false);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SHARED_PREFERENCES_CAMPUS_KEY) || key.equals(SHARED_PREFERENCES_STATUS_KEY))
            retrieveFoodServices();
    }

    private void retrieveFoodServices() {
        String campus = sharedPreferences.getString(SHARED_PREFERENCES_CAMPUS_KEY, getString(R.string.default_campus));
        String status = sharedPreferences.getString(SHARED_PREFERENCES_STATUS_KEY, getString(R.string.default_status));
        Set<String> dietaryPreferences = new HashSet<>(sharedPreferences.getStringSet(SHARED_PREFERENCES_DIETARY_PREFERENCES, new HashSet<>()));
        if (!campus.isEmpty() && !status.isEmpty()) {
            viewModel.getFoodServices(campus, status).observe(this, data -> {
                if (data != null && data.size() > 0) {
                    notAvailable.cancel();
                    adapter.setData(data);
                    if (showAllSwitch.getVisibility() == View.VISIBLE && showAllSwitch.isChecked()) {
                        adapter.setData(data);
                    } else {
                        List<FoodService> filteredData = new ArrayList<>();
                        for (FoodService fs : data) {
                            if (fs.getCategories().size() == 0)
                                filteredData.add(fs);
                            else
                                for (String preference : dietaryPreferences)
                                    if (fs.getCategories().contains(Integer.parseInt(preference))) {
                                        filteredData.add(fs);
                                        break;
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
    public void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}
