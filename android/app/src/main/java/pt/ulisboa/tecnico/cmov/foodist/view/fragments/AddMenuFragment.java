package pt.ulisboa.tecnico.cmov.foodist.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.model.Dish;
import pt.ulisboa.tecnico.cmov.foodist.view.App;
import pt.ulisboa.tecnico.cmov.foodist.view.viewmodel.DishViewModel;

public class AddMenuFragment extends Fragment {

    private EditText dishName;
    private EditText dishCost;

    private DishViewModel viewModel;

    private String foodServiceNameArg;

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
        View view = inflater.inflate(R.layout.fragment_add_to_menu, container, false);
        dishName = view.findViewById(R.id.newDishName);
        dishCost = view.findViewById(R.id.newDishCost);
        view.findViewById(R.id.submitBtn).setOnClickListener(v -> {
            String name = dishName.getText().toString();
            if (name.isEmpty()) {
                dishName.setError("Name missing!");
                return;
            }
            String costStr = dishCost.getText().toString();
            if (costStr.isEmpty()) {
                dishCost.setError("Cost missing!");
                return;
            }
            try {
                float cost = Float.parseFloat(costStr);
                if (viewModel.putDish(foodServiceNameArg, new Dish(name, cost)))
                    NavHostFragment
                            .findNavController(AddMenuFragment.this)
                            .popBackStack();
                else
                    dishName.setError("Name already exists!");
            } catch (NumberFormatException e) {
                dishCost.setError("Not a valid number!");
            }
        });
        return view;
    }

}
