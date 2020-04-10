package pt.ulisboa.tecnico.cmov.foodist.view.fragments;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.view.animation.ViewWeightAnimation;

public class DishFragment extends Fragment {

    private static final int IMG_LAYOUT_STATE_CONTRACTED = 0;
    private static final int IMG_LAYOUT_STATE_EXPANDED = 1;
    private int imgLayoutState = 0;

    private ConstraintLayout foodServiceContainer;

    private ConstraintLayout imagesContainer;
    private ImageView imgView;

    /*@Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }*/

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dish, container, false);
        imgView = view.findViewById(R.id.DishPhoto);
        //imgView.onCreate(savedInstanceState);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imgLayoutState == IMG_LAYOUT_STATE_CONTRACTED){
                    imgLayoutState = IMG_LAYOUT_STATE_EXPANDED;
                    expandImgAnimation();
                }
                else if(imgLayoutState == IMG_LAYOUT_STATE_EXPANDED){
                    imgLayoutState = IMG_LAYOUT_STATE_CONTRACTED;
                    contractImgAnimation();
                }
            }
        });
        return view;
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

    /*public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(DishFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });*/
}

