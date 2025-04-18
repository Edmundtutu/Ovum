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
import com.pac.ovum.data.repositories.CycleRepository;
import com.pac.ovum.data.repositories.EpisodeRepository;
import com.pac.ovum.data.repositories.EventRepository;
import com.pac.ovum.databinding.FragmentHomeBinding;
import com.pac.ovum.ui.dialogs.LogSymptomsDialogFragment;
import com.pac.ovum.utils.AppModule;
import com.pac.ovum.utils.SharedPrefManager;
import com.pac.ovum.utils.ui.CircularPeriodProgressView;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
       /*
        * These are the mock repositories for testing purposes
        * EventRepository eventsRepository = new MockCalendarEventsRepository();
        * EpisodeRepository episodesRepository = new MockEpisodesRepository();
        * */
        EventRepository eventsRepository = AppModule.provideEventRepository(getContext());
        EpisodeRepository episodeRepository = AppModule.provideEpisodeRepository(getContext());
        CycleRepository cycleRepository = AppModule.provideCycleRepository(requireContext());
        HomeViewModelFactory factory = new HomeViewModelFactory(eventsRepository, episodeRepository, cycleRepository);
        homeViewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initViews();
        selectedDate = LocalDate.now(); // Initialize selected date to today
        setWeekView(); // Load the next 7 days
        setBlinderAndProgressView();

        // setting the Symptoms RecyclerView
        setSymptomsRecyclerView();

        binding.feelButton.leftEmoji.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.emoji_pulse));
        binding.feelButton.getRoot().setOnClickListener(v -> {
            LogSymptomsDialogFragment dialogFragment = new LogSymptomsDialogFragment(homeViewModel.getDateToday());
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

    public void setBlinderAndProgressView() {
        homeViewModel.getCurrentDate().observe(getViewLifecycleOwner(), currentDay -> {
            String formattedDate = currentDay.getDate();
            dayOfWeekTextView.setText(currentDay.getDayOfWeek());
        });
        homeViewModel.getOngoingCycleData(getUserId()).observe(getViewLifecycleOwner(), cycleData -> {
                if (cycleData != null) {
                    int cycleLength = cycleData.getCycleLength();
                    int progress = calculateProgress(cycleData.getStartDate());
                    int fertileWindowStartsIn = calculateDaysUntilFertile(cycleData.getFertileStart());
                    int nextPeriodStartsIn = calculateDaysToPeriod(cycleData.getEndDate());
                    setCircularProgressView(cycleLength, progress, fertileWindowStartsIn, nextPeriodStartsIn);
                } else {
                    Log.e("Dashboard", "CycleData is null — progress not set.");
                }
            });
        }

    private void setCircularProgressView(int cycleLength, int progress, int fertileWindowStartsIn, int nextPeriodStartsIn) {
        progressView.setCenterImageResource(R.drawable.icon_logo);
        progressView.setCycleLength(cycleLength);
        progressView.setProgress(progress);
        progressView.setDaysUntilPeriod(nextPeriodStartsIn);
        progressView.setDaysUntilFertile(fertileWindowStartsIn);
        int daysLeftCount = cycleLength - progress;
        progressView.configurePulseEffect(daysLeftCount);
    }

    private int calculateProgress(LocalDate startDate) {
        if (startDate == null) return 0;
        LocalDate today = LocalDate.now();
        return (int) ChronoUnit.DAYS.between(startDate, today);
    }
    private int calculateDaysUntilFertile(LocalDate fertileStart) {
        if (fertileStart == null) return 0;

        LocalDate today = LocalDate.now();
        long days = ChronoUnit.DAYS.between(today, fertileStart);

        return (int) Math.max(days, 0); // Prevent negative values
    }
    private int calculateDaysToPeriod(LocalDate endDate) {
        if (endDate == null) return 0;

        LocalDate today = LocalDate.now();
        long days = ChronoUnit.DAYS.between(today, endDate);

        return (int) Math.max(days, 0); // Prevent negative values
    }


    private void setSymptomsRecyclerView(){
        int userId = getUserId();
        // Get the symptoms from the view model using the UserId of the logged in user
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            homeViewModel.getSymptoms(LocalDate.now(), userId).observe(getViewLifecycleOwner(), symptoms -> {
                if (symptoms != null && !symptoms.isEmpty()) {
                    Log.d("HomeFragment", "Symptoms: " + symptoms);
                    // Set the layout manager and adapter for the recycler view
                    symptomsRecyclerView.setAdapter(new SymptomsAdapter(getContext(), symptoms));
                    // Set the layout manager to a horizontal LinearLayoutManager
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                    symptomsRecyclerView.setLayoutManager(layoutManager);
                    // Update the adapter with the new symptoms
                    ((SymptomsAdapter) Objects.requireNonNull(symptomsRecyclerView.getAdapter())).updateSymptoms(symptoms);
                } else {
                    Log.d("HomeFragment", "No symptoms available for today.");
                    // Optionally, handle the UI for no symptoms (e.g., show a message)
                }
            });
        }
    }

    private int getUserId() {
        // Get the current logged in user's ID from the sharedPrefs
        return SharedPrefManager.getInstance(requireContext()).getUserId();
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