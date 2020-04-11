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
import pt.ulisboa.tecnico.cmov.foodist.model.DiningOption;

public class DiningListAdapter extends RecyclerView.Adapter<DiningListAdapter.DiningListViewHolder> {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class DiningListViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        public TextView openingHours;

        public DiningListViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.serviceName);
            openingHours = view.findViewById(R.id.openingHours);

        }

    }

    private List<DiningOption> diningOptions;
    private View.OnClickListener listener;

    public DiningListAdapter(View.OnClickListener listener) {
        diningOptions = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public DiningListAdapter.DiningListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_dining_options_item, parent, false);
        view.setOnClickListener(listener);
        return new DiningListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiningListViewHolder holder, int position) {
        DiningOption diningOption = diningOptions.get(position);
        holder.name.setText(diningOption.getName());
        holder.openingHours.setText(diningOption.getOpeningHours());
    }

    @Override
    public int getItemCount() {
        return diningOptions.size();
    }

    public void setData(List<DiningOption> data) {
        diningOptions = data;
        notifyDataSetChanged();
    }

}
