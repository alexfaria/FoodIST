package pt.ulisboa.tecnico.cmov.foodist.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.LinkedList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.R;

public class DishPhotoAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private List<Bitmap> photos;

    public DishPhotoAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        photos = new LinkedList<>();
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Object getItem(int position) {
        return photos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View v = view;
        ImageView photo;

        if (v == null) {
            v = mInflater.inflate(R.layout.photo_grid_item, parent, false);
            v.setTag(R.id.photo, v.findViewById(R.id.photo));
        }

        Bitmap bitmap = photos.get(position);
        if (bitmap != null) {
            photo = (ImageView) v.getTag(R.id.photo);
            photo.setImageBitmap(bitmap);
        }

        return v;
    }

    public void setData(List<Bitmap> data) {
        photos = data;
        notifyDataSetChanged();
    }

    public void addItem(Bitmap item) {
        photos.add(item);
        notifyDataSetChanged();
    }
}
