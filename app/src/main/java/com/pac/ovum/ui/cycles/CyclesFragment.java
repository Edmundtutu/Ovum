package com.pac.ovum.ui.cycles;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pac.ovum.data.models.CycleSummary;
import com.pac.ovum.data.repositories.CycleRepository;
import com.pac.ovum.databinding.FragmentCyclesBinding;
import com.pac.ovum.utils.AppModule;

public class CyclesFragment extends Fragment {

    private FragmentCyclesBinding binding;
    CyclesViewModel cyclesViewModel;
    CyclesAdapter cyclesAdapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCyclesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize the repository and ViewModel
        CycleRepository cycleRepository = AppModule.provideCycleRepository(getContext());
        cyclesViewModel = new ViewModelProvider(this, new CyclesViewModelFactory(cycleRepository)).get(CyclesViewModel.class);

        // Set up RecyclerView
        RecyclerView recyclerView = binding.cardRecyleView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Assume you have a method to get the current user's ID
        int userId = getCurrentUserId(); // TODO: Implement this method to retrieve the user ID

        // Observe the LiveData from the ViewModel
        cyclesViewModel.getCycleSummaries(userId).observe(getViewLifecycleOwner(), cycleSummaries -> {
            cyclesAdapter = new CyclesAdapter(cycleSummaries, new CyclesAdapter.OnCycleItemClickListener() {
                @Override
                public void onItemClick(CycleSummary cycle) {
                    // Handle item click
                    // For example, you can start a new activity or show details
                }

                @Override
                public void onExpandClick(CycleSummary cycle) {
                    // Handle expand click
                    // For example, toggle visibility of additional details
                }
            });
            recyclerView.setAdapter(cyclesAdapter);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private int getCurrentUserId() {
        // TODO: Implement logic to retrieve the current user's ID
        return 1; // Placeholder
    }
}