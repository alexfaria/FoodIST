package pt.ulisboa.tecnico.cmov.foodist.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.model.Dish;

public class FoodMenuAdapter extends RecyclerView.Adapter<FoodMenuAdapter.FoodMenuViewHolder>{

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class FoodMenuViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        public TextView cost;
        public RatingBar rating;

        public FoodMenuViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.dishName);
            cost = view.findViewById(R.id.dishCost);
            rating = view.findViewById(R.id.dishRating);
        }

    }

    private List<Dish> dishes;
    private View.OnClickListener listener;

    public FoodMenuAdapter(View.OnClickListener listener) {
        this.dishes = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodMenuAdapter.FoodMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_food_menu_item, parent, false);
        view.setOnClickListener(listener);
        return new FoodMenuAdapter.FoodMenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodMenuAdapter.FoodMenuViewHolder holder, int position) {
        Dish dish = dishes.get(position);
        holder.name.setText(dish.getName());
        holder.cost.setText(String.format(Locale.getDefault(),"%.2f€", dish.getCost()));
        holder.rating.setRating(dish.getAverageRating());
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    public void setData(List<Dish> data) {
        dishes = data;
        notifyDataSetChanged();
    }

}
