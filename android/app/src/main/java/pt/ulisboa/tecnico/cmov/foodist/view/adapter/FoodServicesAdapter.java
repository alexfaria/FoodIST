package pt.ulisboa.tecnico.cmov.foodist.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.model.FoodService;

public class FoodServicesAdapter extends RecyclerView.Adapter<FoodServicesAdapter.FoodServicesViewHolder> {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class FoodServicesViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        public TextView openingHours;

        public FoodServicesViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.serviceName);
            openingHours = view.findViewById(R.id.openingHours);

        }

    }

    private List<FoodService> foodServices;
    private View.OnClickListener listener;

    public FoodServicesAdapter(View.OnClickListener listener) {
        foodServices = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodServicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_dining_options_item, parent, false);
        view.setOnClickListener(listener);
        return new FoodServicesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodServicesViewHolder holder, int position) {
        FoodService fs = foodServices.get(position);
        holder.name.setText(fs.getName());
        holder.openingHours.setText(fs.getOpeningHours());
    }

    @Override
    public int getItemCount() {
        return foodServices.size();
    }

    public void setData(List<FoodService> data) {
        foodServices = data;
        notifyDataSetChanged();
    }

}
