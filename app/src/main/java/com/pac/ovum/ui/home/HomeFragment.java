package com.pac.ovum.ui.home;

import static com.pac.ovum.utils.data.calendarutils.HorizontalCalendarUtils.daysInWeekArray;
import static com.pac.ovum.utils.data.calendarutils.HorizontalCalendarUtils.selectedDate;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pac.ovum.R;
import com.pac.ovum.data.repositories.EpisodeRepository;
import com.pac.ovum.data.repositories.MockCalendarEventsRepository;
import com.pac.ovum.data.repositories.MockEpisodesRepository;
import com.pac.ovum.data.repositories.EventRepository;
import com.pac.ovum.databinding.FragmentHomeBinding;
import com.pac.ovum.ui.dialogs.LogSymptomsDialogFragment;
import com.pac.ovum.utils.data.calendarutils.HorizontalCalendarUtils;
import com.pac.ovum.utils.ui.CircularPeriodProgressView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private HomeViewModel homeViewModel;

    private RecyclerView HorizontalCalendarRecyclerView;
    private RecyclerView symptomsRecyclerView;
    private CircularPeriodProgressView progressView;

    private TextView dayOfWeekTextView;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Create repository and factory
        EventRepository eventsRepository = new MockCalendarEventsRepository(); //TODO: Get the Events Repository from the Real EventsRepository class
        EpisodeRepository episodesRepository = new MockEpisodesRepository();  //TODO: Get the Episodes Repository from the Real Data source
        HomeViewModelFactory factory = new HomeViewModelFactory(eventsRepository, episodesRepository);
        homeViewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // initialize the Views with the initViews method
        initViews();

        // Handling the Horizontal Calendar view
        selectedDate = LocalDate.now();
        setWeekView();

        // Setting the Center Image view
        setBlinderView();

        // setting the Symptoms RecyclerView
        setSymptomsRecyclerView();

        // Setting up the Load Symptoms button
        binding.feelButton.leftEmoji.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.emoji_pulse));
        binding.feelButton.getRoot().setOnClickListener(new View.OnClickListener() {
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
        progressView = binding.progressView;
        dayOfWeekTextView = binding.day;
        symptomsRecyclerView = binding.symptomsRecyclerView;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setWeekView() {
        ArrayList<LocalDate> days = daysInWeekArray(HorizontalCalendarUtils.selectedDate);


        HorizontalCalendarAdapter horizontalCalendarAdapter = new HorizontalCalendarAdapter(days, (parent, view, position, id) -> {
            HorizontalCalendarUtils.selectedDate = days.get(position);
        },getContext(), homeViewModel);
        // Set the layout manager and adapter for the recycler view
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7); // using the date_item.xml as the layout for the recycler view
        HorizontalCalendarRecyclerView.setLayoutManager(layoutManager);
        HorizontalCalendarRecyclerView.setAdapter(horizontalCalendarAdapter);
    }
    public void setBlinderView(){
        // Date live data from the View model
        homeViewModel.getCurrentDate().observe(getViewLifecycleOwner(), currentDay -> {
            String formattedDate = currentDay.getDate();
            // first log the fomattedDate
            Log.v("HomeFragment", "Formatted Date: " + formattedDate);

            // Split the formatted date string into parts
            String[] dateParts = formattedDate.split(" ");

            // Extract the month, day, and year
            String month = dateParts[0]; // "MMM"
            String day = dateParts[1].replace(",", ""); // "dd"
            String year = dateParts[2];  // "yyyy"

            dayOfWeekTextView.setText(currentDay.getDayOfWeek());

        });
        // set the circular progress
        setCircularProgressView(33,28);  // TODO: configure the progress view with the real cycle INFO

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
        binding = null;
    }
}