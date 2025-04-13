package com.pac.ovum.ui.home;

import static com.pac.ovum.utils.data.calendarutils.HorizontalCalendarUtils.daysInWeekArray;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pac.ovum.R;
import com.pac.ovum.data.repositories.EpisodeRepository;
import com.pac.ovum.data.repositories.EventRepository;
import com.pac.ovum.databinding.FragmentHomeBinding;
import com.pac.ovum.ui.dialogs.LogSymptomsDialogFragment;
import com.pac.ovum.utils.AppModule;
import com.pac.ovum.utils.ui.CircularPeriodProgressView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    
    private RecyclerView symptomsRecyclerView;

    private RecyclerView horizontalCalendarRecyclerView;
    private CircularPeriodProgressView progressView;
    private TextView dayOfWeekTextView;
    private LocalDate selectedDate; // Track the selected date

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("HomeFragment", "onCreate called");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d("HomeFragment", "onCreateView called");
        // Create repository and factory
//        EventRepository eventsRepository = new MockCalendarEventsRepository(); //TODO: Get the Events Repository from the Real EventsRepository class
//        EpisodeRepository episodesRepository = new MockEpisodesRepository();  //TODO: Get the Episodes Repository from the Real Data source
        EventRepository eventsRepository = AppModule.provideEventRepository(getContext());
        EpisodeRepository episodeRepository = AppModule.provideEpisodeRepository(getContext());
        HomeViewModelFactory factory = new HomeViewModelFactory(eventsRepository, episodeRepository);
        homeViewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initViews();
        selectedDate = LocalDate.now(); // Initialize selected date to today
        setWeekView(); // Load the next 7 days
        setBlinderView();

        // setting the Symptoms RecyclerView
        setSymptomsRecyclerView();

        binding.feelButton.leftEmoji.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.emoji_pulse));
        binding.feelButton.getRoot().setOnClickListener(v -> {
            LogSymptomsDialogFragment dialogFragment = new LogSymptomsDialogFragment("00,00,00");
            dialogFragment.show(getParentFragmentManager(), "CustomDialog");
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("HomeFragment", "onResume called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("HomeFragment", "onPause called");
    }

    private void initViews() {
        horizontalCalendarRecyclerView = binding.calendarRecyclerView;
        progressView = binding.progressView;
        dayOfWeekTextView = binding.day;
        symptomsRecyclerView = binding.symptomsRecyclerView;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setWeekView() {
        ArrayList<LocalDate> days = daysInWeekArray(selectedDate);
        HorizontalCalendarAdapter horizontalCalendarAdapter = new HorizontalCalendarAdapter(days, this::onDateSelected, getContext(), homeViewModel);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        horizontalCalendarRecyclerView.setLayoutManager(layoutManager);
        horizontalCalendarRecyclerView.setAdapter(horizontalCalendarAdapter);
    }

    private void onDateSelected(LocalDate date) {
        selectedDate = date; // Update the selected date
        // Additional logic to handle the selected date can be added here
    }

    public void setBlinderView() {
        homeViewModel.getCurrentDate().observe(getViewLifecycleOwner(), currentDay -> {
            String formattedDate = currentDay.getDate();
            dayOfWeekTextView.setText(currentDay.getDayOfWeek());
        });
        setCircularProgressView(28, 10); // TODO: Get the cycle length  from cycle data and progress as days since cycle start in the ViewModel
    }

    private void setCircularProgressView(int cycleLength, int progress) {
        progressView.setCenterImageResource(R.drawable.icon_logo);
        progressView.setCycleLength(cycleLength);
        progressView.setProgress(progress);
        progressView.setDaysUntilPeriod(1);
        progressView.setDaysUntilFertile(14);

        // apply the pulse effect
//        Configure based on days left
        int daysLeftCount = cycleLength - progress;
        progressView.configurePulseEffect(daysLeftCount);
    }

    private void setSymptomsRecyclerView(){
        int cycleId = getCyleId(); // TODO: Implement the getCyleId method with the right logic
        //  Get the symptoms from the view model using the CycleId
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            homeViewModel.getSymptoms(LocalDate.now(), cycleId).observe(getViewLifecycleOwner(), symptoms -> {
                Log.d("HomeFragment", "Symptoms: " + symptoms);
                // Set the layout manager and adapter for the recycler view
                symptomsRecyclerView.setAdapter(new SymptomsAdapter(getContext(),symptoms));
                // Set the layout manager to a horizontal LinearLayoutManager
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                symptomsRecyclerView.setLayoutManager(layoutManager);
                // Update the adapter with the new symptoms
                ((SymptomsAdapter) Objects.requireNonNull(symptomsRecyclerView.getAdapter())).updateSymptoms(symptoms);
            });
        }

    }

    private int getCyleId() {
        return 1; // Placeholder
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (horizontalCalendarRecyclerView != null && 
            horizontalCalendarRecyclerView.getAdapter() instanceof HorizontalCalendarAdapter) {
            ((HorizontalCalendarAdapter) horizontalCalendarRecyclerView.getAdapter()).cleanup();
        }
        binding = null;
    }
}