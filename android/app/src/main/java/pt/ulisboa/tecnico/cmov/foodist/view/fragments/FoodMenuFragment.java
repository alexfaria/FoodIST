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
import pt.ulisboa.tecnico.cmov.foodist.model.Dish;
import pt.ulisboa.tecnico.cmov.foodist.view.App;
import pt.ulisboa.tecnico.cmov.foodist.view.adapter.FoodMenuAdapter;
import pt.ulisboa.tecnico.cmov.foodist.view.viewmodel.DishViewModel;

public class FoodMenuFragment extends Fragment {

    private String foodServiceNameArg;

    private RecyclerView recyclerView;
    private FoodMenuAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private DishViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        foodServiceNameArg = getArguments().getString("foodServiceName");
        if (isAdded()) {
            viewModel = ViewModelProviders.of(this).get(DishViewModel.class);
            viewModel.init((App) getContext().getApplicationContext());
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_food_menu, container, false);

        recyclerView = view.findViewById(R.id.menu_recycler_view);
        recyclerView.setHasFixedSize(true);
        adapter = new FoodMenuAdapter(v -> {
            Bundle args = new Bundle();
            TextView dishName = v.findViewById(R.id.dishName);
            args.putString("foodServiceName", foodServiceNameArg);
            args.putString("dishName", dishName.getText().toString());
            NavHostFragment
                    .findNavController(FoodMenuFragment.this)
                    .navigate(R.id.action_FoodMenu_to_Dish, args);
        });
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);

        view.findViewById(R.id.addMenuBtn).setOnClickListener(v -> {
            viewModel.putDish(foodServiceNameArg,
                    new Dish("Arroz de Pato", 4.5f));
                /*
                NavHostFragment
                    .findNavController(FoodMenuFragment.this)
                    .navigate(R.id.action_FoodMenu_to_AddToMenu));
                 */
        });
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (foodServiceNameArg != null)
            viewModel.getDishes(foodServiceNameArg).observe(this, dishes -> {
                adapter.setData(dishes);
            });
    }
}
