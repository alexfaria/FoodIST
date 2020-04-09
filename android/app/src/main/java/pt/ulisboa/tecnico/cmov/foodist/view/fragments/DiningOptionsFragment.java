package pt.ulisboa.tecnico.cmov.foodist.view.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.view.App;
import pt.ulisboa.tecnico.cmov.foodist.view.adapter.DiningListAdapter;
import pt.ulisboa.tecnico.cmov.foodist.view.viewmodel.DiningOptionsViewModel;

public class DiningOptionsFragment extends Fragment implements Handler.Callback {

    private DiningOptionsViewModel viewModel;
    private Handler handler = new Handler(this);

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(DiningOptionsViewModel.class);
        App app = (App) getContext().getApplicationContext();
        viewModel.init(app.getChannel());
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dining_options, container, false);
        recyclerView = view.findViewById(R.id.dining_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getDiningOptions(handler);
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        adapter = new DiningListAdapter(msg.getData().getParcelableArrayList("values"), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                TextView serviceName = v.findViewById(R.id.serviceName);
                args.putString("name", serviceName.getText().toString());
                NavHostFragment
                        .findNavController(DiningOptionsFragment.this)
                        .navigate(R.id.action_DiningOptions_to_FoodService, args);
            }
        });
        recyclerView.setAdapter(adapter);
        return true;
    }
}
