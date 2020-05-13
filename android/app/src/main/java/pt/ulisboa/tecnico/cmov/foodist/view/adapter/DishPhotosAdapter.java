package pt.ulisboa.tecnico.cmov.foodist.view.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.R;

public class DishPhotosAdapter extends RecyclerView.Adapter<DishPhotosAdapter.DishPhotoViewHolder> {

    private List<Bitmap> photos;
    private OnImageClickListener listener;

    public DishPhotosAdapter(OnImageClickListener listener) {
        this.photos = new LinkedList<>();
        this.listener = listener;
    }

    public static class DishPhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        ImageView imageView;
        OnImageClickListener imageClickListener;

        public DishPhotoViewHolder(View view, OnImageClickListener listener) {
            super(view);
            imageView = view.findViewById(R.id.dish_photo_item);
            imageClickListener = listener;

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            imageClickListener.onImageClick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public DishPhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_dish_photo_item, parent, false);
        return new DishPhotoViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull DishPhotoViewHolder holder, int position) {
        Bitmap bitmap = photos.get(position);
        holder.imageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public void setData(List<Bitmap> data) {
        photos = data;
        notifyDataSetChanged();
    }

    public Bitmap getItem(int position) {
        return photos.get(position);
    }

    public void addItem(Bitmap item) {
        photos.add(item);
        notifyDataSetChanged();
    }

    public interface OnImageClickListener {
        void onImageClick(int position);
    }
}
