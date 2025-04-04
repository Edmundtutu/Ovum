package com.pac.ovum.ui.calendar;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.snackbar.Snackbar;
import com.pac.ovum.R;
import com.pac.ovum.data.repositories.EventRepository;
import com.pac.ovum.databinding.FragmentCalendarBinding;
import com.pac.ovum.ui.dialogs.LogSymptomsDialogFragment;
import com.pac.ovum.ui.dialogs.SetGynEventFragment;
import com.pac.ovum.utils.AppModule;
import com.pac.ovum.utils.data.calendarutils.DateUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding binding;
    private static final String TAG = "CalendarFragment";

    public static CompactCalendarView compactCalendarView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button syncButton;
    private TextView monthYearTitle;
    private Button addSymptomButton;
    private Button addEventButton;

    private DateUtils dateUtils;
    private EventAdapter eventAdapter;
    private CalendarViewModel viewModel;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initializeViews();
        setupCalendar();
        setupListeners();
        
        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        RecyclerView recyclerView = view.findViewById(R.id.events_recycler_view);
        eventAdapter = new EventAdapter();
        recyclerView.setAdapter(eventAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        initializeViewModel();
        setupSyncObservers();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializeViews() {
        dateUtils = new DateUtils();
        compactCalendarView = binding.compactcalendarView;
        swipeRefreshLayout = binding.calendarSwipeRefresh;
        syncButton = binding.calendarSyncButton;
        monthYearTitle = binding.monthYearTitle;
        addSymptomButton = binding.addSymptomButton;
        addEventButton = binding.addEventButton;
        
        // Set current month/year title
        updateMonthYearTitle(compactCalendarView.getFirstDayOfCurrentMonth());
    }

    private void setupCalendar() {
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);

        // Set up the listener for the CompactCalendarView
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDayClick(Date dateClicked) {
                java.time.LocalDate localDate = dateClicked.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                handleDayClick(localDate);
                Log.d(TAG, "Day was clicked: " + dateClicked);
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                Log.d(TAG, "Month was scrolled to: " + firstDayOfNewMonth);
                // Update month/year title
                updateMonthYearTitle(firstDayOfNewMonth);
                
                // Update events for the scrolled month
                updateEventsForCurrentMonth();
            }
        });
    }

    private void updateMonthYearTitle(Date firstDayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(firstDayOfMonth);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        
        // Get month name
        String[] monthNames = {"January", "February", "March", "April", "May", "June", 
                "July", "August", "September", "October", "November", "December"};
        
        monthYearTitle.setText(monthNames[month] + " " + year);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupListeners() {
        // Set up swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(this::syncEventsFromApi);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.purple_500,
                R.color.teal_200,
                R.color.purple_700);
        
        // Set up sync button
        syncButton.setOnClickListener(v -> syncEventsToApi());
        
        // Set up add symptom button
        addSymptomButton.setOnClickListener(v -> {
            Date date = compactCalendarView.getFirstDayOfCurrentMonth();
            java.time.LocalDate localDate = date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            showLogSymptomsDialog(localDate);
        });
        
        // Set up add event button
        addEventButton.setOnClickListener(v -> {
            Date date = compactCalendarView.getFirstDayOfCurrentMonth();
            java.time.LocalDate localDate = date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            showSetGynEventDialog(localDate);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleDayClick(LocalDate date) {
        // Update events list for this day
        if (viewModel != null) {
            List<com.pac.ovum.data.models.Event> dayEvents = viewModel.getEventsForDate(date);
            eventAdapter.setEvents(dayEvents);
            eventAdapter.notifyDataSetChanged();
            
            // Show appropriate add buttons
            addSymptomButton.setVisibility(View.VISIBLE);
            addEventButton.setVisibility(View.VISIBLE);
        }
    }

    private void showLogSymptomsDialog(LocalDate date) {
        LogSymptomsDialogFragment dialog = new LogSymptomsDialogFragment(date.toString());
        Bundle args = new Bundle();
        args.putString("selectedDate", date.toString());
        dialog.setArguments(args);
        dialog.show(getParentFragmentManager(), "LogSymptomsDialog");
    }

    private void showSetGynEventDialog(LocalDate date) {
        SetGynEventFragment dialog = new SetGynEventFragment(date.toString());
        Bundle args = new Bundle();
        args.putString("selectedDate", date.toString());
        dialog.setArguments(args);
        dialog.show(getParentFragmentManager(), "SetGynEventDialog");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateEventsForCurrentMonth() {
        if (viewModel != null) {
            // Get the current month's date range
            Date firstDayOfMonth = compactCalendarView.getFirstDayOfCurrentMonth();
            LocalDate startDate = firstDayOfMonth.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            LocalDate endDate = startDate.plusMonths(1).minusDays(1);
            
            // Fetch and update events for this date range
            viewModel.getEventsForDateRange(startDate, endDate).observe(
                    getViewLifecycleOwner(), this::updateCalendarEvents);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializeViewModel() {
        EventRepository eventRepository = AppModule.provideEventRepository(requireContext());
        viewModel = new ViewModelProvider(this, 
                new CalendarViewModelFactory(eventRepository)).get(CalendarViewModel.class);
        
        // Get events for current month initially
        updateEventsForCurrentMonth();
    }
    
    /**
     * Set up observers for sync status
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupSyncObservers() {
        // Observe syncing status
        viewModel.getIsSyncing().observe(getViewLifecycleOwner(), isSyncing -> {
            swipeRefreshLayout.setRefreshing(isSyncing);
            syncButton.setEnabled(!isSyncing);
            
            if (isSyncing) {
                syncButton.setText(R.string.syncing);
            } else {
                syncButton.setText(R.string.sync_to_server);
            }
        });
        
        // Observe sync errors
        viewModel.getSyncError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG)
                        .setAction("Dismiss", v -> {})
                        .show();
            }
        });
        
        // Observe sync success
        viewModel.getSyncSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(getContext(), "Calendar sync completed successfully", Toast.LENGTH_SHORT).show();
                updateEventsForCurrentMonth(); // Refresh view after successful sync
            }
        });
    }
    
    /**
     * Sync events from API to local database (pull)
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void syncEventsFromApi() {
        // Get the current month's date range
        Date firstDayOfMonth = compactCalendarView.getFirstDayOfCurrentMonth();
        LocalDate startDate = firstDayOfMonth.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        
        // Sync events for this date range
        viewModel.syncEventsForDateRange(getCurrentUserId(), startDate, endDate);
    }
    
    /**
     * Sync events from local database to API (push)
     */
    private void syncEventsToApi() {
        viewModel.syncAllEventsToApi(getCurrentUserId());
    }

    private int getCurrentUserId() {
        // This would normally come from a user session or authentication service
        return 1; // Placeholder user ID
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateCalendarEvents(List<com.pac.ovum.data.models.Event> events) {
        // Clear previous events
        compactCalendarView.removeAllEvents();
        
        // Add events to calendar
        for (com.pac.ovum.data.models.Event event : events) {
            long timeInMillis = event.getEventDate()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();
            
            int color = getEventColor(event.getEventType());
            Event calEvent = new Event(color, timeInMillis, event);
            compactCalendarView.addEvent(calEvent);
        }
        
        // Refresh the view
        compactCalendarView.invalidate();
    }

    private int getEventColor(String eventType) {
        // Return different colors based on event type
        if (eventType == null) return Color.GRAY;
        
        switch (eventType.toLowerCase()) {
            case "period":
                return Color.RED;
            case "ovulation":
                return Color.BLUE;
            case "pill":
                return Color.GREEN;
            case "doctor":
                return Color.MAGENTA;
            default:
                return Color.GRAY;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}