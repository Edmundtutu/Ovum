package com.pac.ovum.ui.home;

import static com.pac.ovum.utils.data.calendarutils.HorizontalCalendarUtils.daysInWeekArray;
import static com.pac.ovum.utils.data.calendarutils.HorizontalCalendarUtils.selectedDate;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pac.ovum.data.repositories.SimulatedEventsRepository;
import com.pac.ovum.ui.dialogs.LogSymptomsDialogFragment;
import com.pac.ovum.databinding.FragmentHomeBinding;
import com.pac.ovum.utils.data.calendarutils.HorizontalCalendarUtils;

import java.time.LocalDate;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private HomeViewModel homeViewModel;

    private RecyclerView HorizontalCalendarRecyclerView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Create repository and factory
        SimulatedEventsRepository repository = new SimulatedEventsRepository(); //TODO: Get the Events Repository from the Real EventsRepository class
        HomeViewModelFactory factory = new HomeViewModelFactory(repository);
        homeViewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // initialize the Views with the initViews method
        initViews();

        // Handling the Horizontal Calendar view
        selectedDate = LocalDate.now();
        setWeekView();

        // Setting up the Load Symptoms button
        final Button loadSymptomsButton = binding.addButton;
        loadSymptomsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogSymptomsDialogFragment dialogFragment = new LogSymptomsDialogFragment("00,00,00"); // TODO: Pass the current date variable if has one
                dialogFragment.show(getParentFragmentManager(), "CustomDialog");
            }
        });
        return root;
    }

    private void initViews(){
        HorizontalCalendarRecyclerView = binding.calendarRecyclerView;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setWeekView() {
        ArrayList<LocalDate> days = daysInWeekArray(HorizontalCalendarUtils.selectedDate);


        HorizontalCalendarAdapter horizontalCalendarAdapter = new HorizontalCalendarAdapter(days, (parent, view, position, id) -> {
            HorizontalCalendarUtils.selectedDate = days.get(position);
        },getContext(), homeViewModel);
        // Set the layout manager and adapter for the recycler view
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        HorizontalCalendarRecyclerView.setLayoutManager(layoutManager);
        HorizontalCalendarRecyclerView.setAdapter(horizontalCalendarAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}