package pt.ulisboa.tecnico.cmov.foodist.view.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.model.Dish;
import pt.ulisboa.tecnico.cmov.foodist.view.App;
import pt.ulisboa.tecnico.cmov.foodist.view.viewmodel.DishViewModel;

import static android.app.Activity.RESULT_OK;
import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.NAVHOST_ARGS_FOODSERVICE_NAME;


public class AddMenuFragment extends Fragment {

    private static final int CAMERA_REQUEST_CODE = 100;

    private EditText dishName;
    private EditText dishCost;
    private Bitmap dishPhoto;
    private CheckBox dishPhotoCheckbox;

    private DishViewModel viewModel;
    private String foodServiceNameArg;
    private int category;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        foodServiceNameArg = getArguments().getString(NAVHOST_ARGS_FOODSERVICE_NAME);
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
        dishPhotoCheckbox = view.findViewById(R.id.photoCheckBox);
        dishPhotoCheckbox.setEnabled(false);

        RadioGroup rg = view.findViewById(R.id.radioGroup);

        rg.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioMeat:
                    category = 0;
                    break;
                case R.id.radioFish:
                    category = 1;
                    break;
                case R.id.radioVegetarian:
                    category = 2;
                    break;
                case R.id.radioVegan:
                    category = 3;
                    break;
            }
        });

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
                Dish dish = new Dish(name, cost, category, 0);
                if (dishPhoto != null) {
                    dish.addPhoto(dishPhoto);
                }
                viewModel.putDish(foodServiceNameArg, dish).observe(this, success -> {
                    if (success) {
                        view.clearFocus();
                        NavHostFragment
                                .findNavController(AddMenuFragment.this)
                                .popBackStack();
                    } else {
                        dishName.setError("Already exists a dish with the given name!");
                    }
                });
            } catch (NumberFormatException e) {
                dishCost.setError("Not a valid number!");
            }
        });
        view.findViewById(R.id.uploadBtn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if (imageBitmap != null) {
                dishPhoto = imageBitmap;
                dishPhotoCheckbox.setChecked(true);
                Toast.makeText(this.getContext(), "Your photo was uploaded successfully!", Toast.LENGTH_LONG).show();
            }
        }
    }
}