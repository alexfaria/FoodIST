package pt.ulisboa.tecnico.cmov.foodist.view.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RatingBar;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.view.App;
import pt.ulisboa.tecnico.cmov.foodist.view.adapter.DishPhotoAdapter;
import pt.ulisboa.tecnico.cmov.foodist.view.viewmodel.DishViewModel;

import static android.app.Activity.RESULT_OK;
import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.NAVHOST_ARGS_DISH_NAME;
import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.NAVHOST_ARGS_FOODSERVICE_NAME;
import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.NAVHOST_ARGS_DISH_PHOTO;

public class DishFragment extends Fragment {

    private static final int CAMERA_REQUEST_CODE = 100;

    private TextView name;
    private TextView cost;
    private RatingBar rating;

    private DishPhotoAdapter adapter;

    private DishViewModel viewModel;

    private String foodServiceNameArg;
    private String dishNameArg;
    private String uuid;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        foodServiceNameArg = getArguments().getString(NAVHOST_ARGS_FOODSERVICE_NAME);
        dishNameArg = getArguments().getString(NAVHOST_ARGS_DISH_NAME);
        if (isAdded()) {
            viewModel = ViewModelProviders.of(this).get(DishViewModel.class);
            viewModel.init((App) getContext().getApplicationContext());
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
            uuid = sharedPreferences.getString("uuid", "");
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dish, container, false);
        name = view.findViewById(R.id.dish_name);
        cost = view.findViewById(R.id.dish_cost);
        rating = view.findViewById(R.id.dish_rating);
        rating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser)
                viewModel.putUserDishRating(foodServiceNameArg, dishNameArg, rating,uuid);
        });
        GridView gridView = view.findViewById(R.id.photos_grid_view);
        adapter = new DishPhotoAdapter(getContext());
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener((parent, view1, position, id) -> {
            Bitmap photo = (Bitmap) adapter.getItem(position);
            Bundle args = new Bundle();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            args.putByteArray(NAVHOST_ARGS_DISH_PHOTO, stream.toByteArray());
            NavHostFragment
                    .findNavController(DishFragment.this)
                    .navigate(R.id.action_Dish_to_FullscreenPhoto, args);
        });
        view.findViewById(R.id.uploadDishPhoto).setOnClickListener(v -> {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        });
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getDish(foodServiceNameArg, dishNameArg, uuid).observe(this, dish -> {
            name.setText(dish.getName());
            cost.setText(String.format(Locale.getDefault(), "%.2f€", dish.getCost()));
            if (dish.getNumberOfPhotos() > 0)
                adapter.setData(dish.getPhotos());
        });
        viewModel.getUserDishRating(foodServiceNameArg, dishNameArg, uuid).observe(this, userRating ->
                rating.setRating(userRating)
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if (imageBitmap != null) {
                viewModel.putDishPhoto(foodServiceNameArg, dishNameArg, imageBitmap);
                adapter.addItem(imageBitmap);
            }
        }
    }
}

