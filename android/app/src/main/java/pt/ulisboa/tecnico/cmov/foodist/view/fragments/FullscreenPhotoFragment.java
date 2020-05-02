package pt.ulisboa.tecnico.cmov.foodist.view.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import pt.ulisboa.tecnico.cmov.foodist.R;

import static pt.ulisboa.tecnico.cmov.foodist.view.Constants.NAVHOST_ARGS_DISH_PHOTO;

public class FullscreenPhotoFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dish_photo_fullscreen, container, false);
        ImageView fullscreenPhoto = view.findViewById(R.id.full_photo);

        byte[] byteArray = getArguments().getByteArray(NAVHOST_ARGS_DISH_PHOTO);
        if (byteArray != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            fullscreenPhoto.setImageBitmap(bitmap);
        }

        return view;
    }
}
