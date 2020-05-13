package pt.ulisboa.tecnico.cmov.foodist.view.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.taufiqrahman.reviewratings.BarLabels;
import com.taufiqrahman.reviewratings.RatingReviews;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.view.App;
import pt.ulisboa.tecnico.cmov.foodist.view.adapter.DishPhotosAdapter;
import pt.ulisboa.tecnico.cmov.foodist.view.viewmodel.DishViewModel;

import static android.app.Activity.RESULT_OK;
import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.NAVHOST_ARGS_DISH_NAME;
import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.NAVHOST_ARGS_DISH_PHOTO;
import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.NAVHOST_ARGS_FOODSERVICE_NAME;

public class DishFragment extends Fragment implements DishPhotosAdapter.OnImageClickListener {

    private static final int CAMERA_REQUEST_CODE = 100;

    private TextView name;
    private TextView cost;
    private TextView category;
    private RatingBar rating;

    private RatingReviews ratingReviews;
    private TextView averageRating;
    private RatingBar averageRatingBar;
    private TextView ratingsCount;

    private DishPhotosAdapter adapter;

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
        category = view.findViewById(R.id.category);
        rating = view.findViewById(R.id.dish_rating);
        rating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                if (!((App)getContext().getApplicationContext()).isConnected()) {
                    Toast.makeText(getContext(), "No connection available!", Toast.LENGTH_LONG).show();
                    return;
                }
                viewModel.putUserDishRating(foodServiceNameArg, dishNameArg, rating, uuid);
                Toast.makeText(getContext(), "Your feedback was saved!", Toast.LENGTH_SHORT).show();
            }
        });
        ratingReviews = view.findViewById(R.id.rating_reviews);
        averageRating = view.findViewById(R.id.average_rating);
        averageRatingBar = view.findViewById(R.id.average_rating_bar);
        ratingsCount = view.findViewById(R.id.average_rating_count);
        RecyclerView recyclerView = view.findViewById(R.id.photos_recycler_view);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        adapter = new DishPhotosAdapter(this);
        recyclerView.setAdapter(adapter);
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
            switch (dish.getCategory()) {
                case 0:
                    category.setText(R.string.meat);
                    break;
                case 1:
                    category.setText(R.string.fish);
                    break;
                case 2:
                    category.setText(R.string.vegetarian);
                    break;
                case 3:
                    category.setText(R.string.vegan);
                    break;
            }
            cost.setText(String.format(Locale.getDefault(), "%.2f€", dish.getCost()));
            if (dish.getNumberOfPhotos() > 0)
                adapter.setData(dish.getPhotos());
        });
        viewModel.getUserDishRating(foodServiceNameArg, dishNameArg, uuid).observe(this, userRating ->
                rating.setRating(userRating)
        );
        viewModel.getAllRatings(foodServiceNameArg, dishNameArg).observe(this, ratings -> {
            int[] raters = new int[] {0, 0, 0, 0, 0};
            float sum = 0;
            for(float f : ratings) {
                raters[-((int)f - 5)]++;
                sum += f;
            }
            float average = ratings.size() > 0 ? sum / ratings.size() : 0f;
            averageRating.setText(String.format(Locale.getDefault(), "%.1f", average));
            averageRatingBar.setRating(average);
            ratingsCount.setText(""+ratings.size());
            ratingReviews.createRatingBars(100, BarLabels.STYPE3, ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null), raters);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if (imageBitmap != null) {
                if (!((App)getContext().getApplicationContext()).isConnected()) {
                    Toast.makeText(getContext(), "No connection available!", Toast.LENGTH_LONG).show();
                    return;
                }
                viewModel.putDishPhoto(foodServiceNameArg, dishNameArg, imageBitmap);
                adapter.addItem(imageBitmap);
                Toast.makeText(getContext(), "Your photo was uploaded!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onImageClick(int position) {
        Bitmap image = adapter.getItem(position);
        Bundle args = getArguments();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        args.putByteArray(NAVHOST_ARGS_DISH_PHOTO, stream.toByteArray());
        NavHostFragment
                .findNavController(DishFragment.this)
                .navigate(R.id.action_Dish_to_FullscreenPhoto, args);
    }
}

