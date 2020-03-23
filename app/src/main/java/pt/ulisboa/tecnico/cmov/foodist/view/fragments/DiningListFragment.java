package pt.ulisboa.tecnico.cmov.foodist.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.model.DiningOption;
import pt.ulisboa.tecnico.cmov.foodist.view.adapter.DiningListAdapter;

public class DiningListFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dining_list, container, false);

        recyclerView = view.findViewById(R.id.dining_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DiningListAdapter(new DiningOption[] {
                new DiningOption("Cantina", "12:30-14:30 / 19:30-21:30"),
                new DiningOption("Bar de Civil", "8:00-22:00"),
                new DiningOption("Bar de Matemática", "10:00-20:00"),
                new DiningOption("Bar do Pavilhão Central", "8:00-22:00"),
                new DiningOption("Bar de Mecânica", "9:00-16:00"),
                new DiningOption("Bar da Associação de Estudantes", "8:00-20:00"),
        });
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });*/
    }
}
