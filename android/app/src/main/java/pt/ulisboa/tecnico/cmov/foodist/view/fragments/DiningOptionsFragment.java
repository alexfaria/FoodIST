package pt.ulisboa.tecnico.cmov.foodist.view.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.view.adapter.FoodServicesAdapter;
import pt.ulisboa.tecnico.cmov.foodist.view.viewmodel.FoodServiceViewModel;

import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.NAVHOST_ARGS_FOODSERVICE_NAME;
import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.SHARED_PREFERENCES_CAMPUS_KEY;
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
        if (!campus.isEmpty() && !status.isEmpty())
            viewModel.getFoodServices(campus, status).observe(this, data -> {
                if (data != null)
                    adapter.setData(data);
                else {
                    Toast toast = Toast.makeText(getContext(), "There are currently no services available!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}
