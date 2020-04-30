package pt.ulisboa.tecnico.cmov.foodist.view.fragments;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.util.Locale;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.view.App;
import pt.ulisboa.tecnico.cmov.foodist.view.animation.ViewWeightAnimation;
import pt.ulisboa.tecnico.cmov.foodist.view.viewmodel.DishViewModel;

import static android.app.Activity.RESULT_OK;
import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.NAVHOST_ARGS_DISHNAME;
import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.NAVHOST_ARGS_FOODSERVICENAME;

public class DishFragment extends Fragment {

    private static final int IMG_LAYOUT_STATE_CONTRACTED = 0;
    private static final int IMG_LAYOUT_STATE_EXPANDED = 1;
    private static final int CAMERA_REQUEST_CODE = 100;

    private ConstraintLayout foodServiceContainer;
    private TextView name;
    private TextView cost;

    private ConstraintLayout imagesContainer;
    private ImageView imgView;
    private int imgIndex = 0;
    private int imgLayoutState = 0;

    private DishViewModel viewModel;

    private String foodServiceNameArg;
    private String dishNameArg;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        foodServiceNameArg = getArguments().getString(NAVHOST_ARGS_FOODSERVICENAME);
        dishNameArg = getArguments().getString(NAVHOST_ARGS_DISHNAME);
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
        View view = inflater.inflate(R.layout.fragment_dish, container, false);
        name = view.findViewById(R.id.dish_name);
        cost = view.findViewById(R.id.dish_price);
        imgView = view.findViewById(R.id.DishPhoto);
        //imgView.onCreate(savedInstanceState);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                /*
                if(imgLayoutState == IMG_LAYOUT_STATE_CONTRACTED){
                    imgLayoutState = IMG_LAYOUT_STATE_EXPANDED;
                    expandImgAnimation();
                }
                else if(imgLayoutState == IMG_LAYOUT_STATE_EXPANDED){
                    imgLayoutState = IMG_LAYOUT_STATE_CONTRACTED;
                    contractImgAnimation();
                }*/
            }
        });
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getDish(foodServiceNameArg, dishNameArg).observe(this, dish -> {
            name.setText(dish.getName());
            cost.setText(String.format(Locale.getDefault(), "%f€", dish.getCost()));
            Bitmap img = dish.getPhoto(imgIndex);
            if (img != null)
                imgView.setImageBitmap(img);
            view.findViewById(R.id.nextPhotoBtn).setOnClickListener(v -> {
                Log.d("DishFragment", "Next image button clicked!");
                if (imgIndex < dish.getNumberOfPhotos()-1)
                    imgView.setImageBitmap(dish.getPhoto(++imgIndex));
            });
            view.findViewById(R.id.prevPhotoBtn).setOnClickListener(v -> {
                Log.d("DishFragment", "Previous image button clicked!");
                if (imgIndex > 0)
                    imgView.setImageBitmap(dish.getPhoto(--imgIndex));
            });
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if (imageBitmap != null) {
                imgView.setImageBitmap(imageBitmap);
                viewModel.putDishPhoto(foodServiceNameArg, dishNameArg, imageBitmap);
            }
        }
    }

    private void expandImgAnimation(){
        ViewWeightAnimation imgAnimationWrapper = new ViewWeightAnimation(imagesContainer);
        ObjectAnimator imgAnimation = ObjectAnimator.ofFloat(imgAnimationWrapper,
                "weight",
                65,
                100);
        imgAnimation.setDuration(800);

        ViewWeightAnimation recyclerAnimationWrapper = new ViewWeightAnimation(foodServiceContainer);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                35,
                0);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        imgAnimation.start();
    }

    private void contractImgAnimation(){
        ViewWeightAnimation imgAnimationWrapper = new ViewWeightAnimation(imagesContainer);
        ObjectAnimator imgAnimation = ObjectAnimator.ofFloat(imgAnimationWrapper,
                "weight",
                100,
                65);
        imgAnimation.setDuration(800);

        ViewWeightAnimation recyclerAnimationWrapper = new ViewWeightAnimation(foodServiceContainer);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                0,
                35);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        imgAnimation.start();
    }

}

