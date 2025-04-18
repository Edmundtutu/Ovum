package com.pac.ovum.ui.calendar;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.snackbar.Snackbar;
import com.pac.ovum.R;
import com.pac.ovum.data.models.CycleData;
import com.pac.ovum.data.repositories.CycleRepository;
import com.pac.ovum.data.repositories.EventRepository;
import com.pac.ovum.databinding.FragmentCalendarBinding;
import com.pac.ovum.ui.dialogs.LogSymptomsDialogFragment;
import com.pac.ovum.ui.dialogs.SetGynEventFragment;
import com.pac.ovum.utils.AppModule;
import com.pac.ovum.utils.SharedPrefManager;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding binding;
    private static final String TAG = "CalendarFragment";
    private static final int DEFAULT_START_YEAR = 1900;
    private static final int DEFAULT_END_YEAR = 2040;

    private Spinner monthSpinner;
    private Spinner yearSpinner;
    private List<Integer> years;

    public static CompactCalendarView compactCalendarView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView syncButton;
    private Button addSymptomButton;
    private Button addEventButton;
    private CardView actionsCard;
    private TextView calendarTitle;
    private TextView selectedDateTitle;

    private EventAdapter eventAdapter;
    private CalendarViewModel viewModel;

    // Define colors for different types of dates
    private static final int COLOR_PERIOD = Color.parseColor("#80FF4B55");       // Red for period
    private static final int COLOR_OVULATION = Color.parseColor("#802196F3");    // Blue for ovulation
    private static final int COLOR_FERTILE = Color.parseColor("#804CAF50");      // Green for fertile window

    // Define a Date time formatter for the form Monday, January 1, 2025
    private static final String DATE_FORMAT = "EEEE, d'th' MMMM";

    // Variable to store the currently selected date
    private LocalDate selectedDate;
    private LocalDate dueDate;

    // Observer for event updates to prevent memory leaks
    private Observer<List<com.pac.ovum.data.models.Event>> eventsObserver;

    // Map to store all events by date - used to coordinate between cycle and user events
    private Map<LocalDate, List<CalendarEventInfo>> combinedEventsMap = new HashMap<>();

    // Class to store event information for better coordination
    private static class CalendarEventInfo {
        int color;
        String description;
        Object data; // Can be Event or String
        boolean isCycleEvent;

        CalendarEventInfo(int color, String description, Object data, boolean isCycleEvent) {
            this.color = color;
            this.description = description;
            this.data = data;
            this.isCycleEvent = isCycleEvent;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initializeViews();
        setupSpinners();
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
        loadAllCalendarData();
    }

    private void setupSpinners() {
        // Populate the Month Spinner
        List<String> months = new ArrayList<>(Arrays.asList(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"));
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item_month, months);
        monthSpinner.setAdapter(monthAdapter);

        // Populate the Year Spinner
        years = new ArrayList<>();
        for (int i = DEFAULT_START_YEAR; i <= DEFAULT_END_YEAR; i++) {
            years.add(i);
        }
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item_year, years);
        yearSpinner.setAdapter(yearAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadAllCalendarData() {
        // Clean the events map before loading new data
        combinedEventsMap.clear();

        // First load cycle data
        observeCycleData();

        // Then load user events - they will be combined with cycle data
        updateEventsForCurrentMonth();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializeViews() {
        compactCalendarView = binding.compactcalendarView;
        swipeRefreshLayout = binding.calendarSwipeRefresh;
        syncButton = binding.calendarSyncButton;
        monthSpinner = binding.monthSpinner;
        yearSpinner = binding.yearSpinner;
        addSymptomButton = binding.addSymptomButton;
        addEventButton = binding.addEventButton;
        actionsCard = binding.actionButtonsCard;
        calendarTitle = binding.calendarTitle;
        selectedDateTitle = binding.selectedDateTitle;

        // Initially hide the action buttons
        actionsCard.setVisibility(View.GONE);
    }

    private void setupCalendar() {
        Calendar calendar = Calendar.getInstance();
        monthSpinner.setSelection(calendar.get(Calendar.MONTH));
        yearSpinner.setSelection(years.indexOf(calendar.get(Calendar.YEAR)));
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);

        // Set up the listener for the CompactCalendarView
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDayClick(Date dateClicked) {
                // Store the clicked date
                selectedDate = dateClicked.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                handleDayClick(selectedDate);
                Log.d(TAG, "Day was clicked: " + dateClicked + ", LocalDate: " + selectedDate);
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                Log.d(TAG, "Month was scrolled to: " + firstDayOfNewMonth);
                // Update Spinners based on the new month scrolled to
                Calendar newMonthCal = Calendar.getInstance();
                newMonthCal.setTime(firstDayOfNewMonth);
                int newYear = newMonthCal.get(Calendar.YEAR);
                int newMonth = newMonthCal.get(Calendar.MONTH);

                yearSpinner.setSelection(years.indexOf(newYear));
                monthSpinner.setSelection(newMonth);

                // Hide action buttons when month changes
                hideActionButtons();
                selectedDate = null;

                // Update events for the scrolled month
                loadAllCalendarData();
            }
        });
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

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateCalendarAccordingToSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateCalendarAccordingToSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Set up add symptom button - now uses the selected date
        addSymptomButton.setOnClickListener(v -> {
            if (selectedDate != null) {
                showLogSymptomsDialog(selectedDate);
            } else {
                Toast.makeText(getContext(), "Please select a date first", Toast.LENGTH_SHORT).show();
                // Highlight the calendar to indicate user should select a date
                highlightCalendarForSelection();
            }
        });

        // Set up add event button - now uses the selected date
        addEventButton.setOnClickListener(v -> {
            Log.d(TAG, "Selected date for Gyn Event: " + selectedDate);
            if (selectedDate != null) {
                showSetGynEventDialog(selectedDate);
            } else {
                Toast.makeText(getContext(), "Please select a date first", Toast.LENGTH_SHORT).show();
                // Highlight the calendar to indicate user should select a date
                highlightCalendarForSelection();
            }
        });
    }

    // Method to highlight calendar briefly to guide user
    private void highlightCalendarForSelection() {
        if (compactCalendarView != null) {
            compactCalendarView.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
            // Could add a brief animation here if desired
        }
    }

    private void updateCalendarAccordingToSpinner() {
        // Update CompactCalendarView based on Spinner selections
        int selectedYear = (int) yearSpinner.getSelectedItem();
        int selectedMonth = monthSpinner.getSelectedItemPosition();

        Calendar cal = Calendar.getInstance();
        cal.set(selectedYear, selectedMonth, 1); // Set to first day of selected month and year

        compactCalendarView.setCurrentDate(cal.getTime());

        // Update Spinners based on CompactCalendarView's current date
        Date firstDayOfCurrentMonth = compactCalendarView.getFirstDayOfCurrentMonth();
        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(firstDayOfCurrentMonth);

        int currentYear = currentCal.get(Calendar.YEAR);
        int currentMonth = currentCal.get(Calendar.MONTH);

        yearSpinner.setSelection(years.indexOf(currentYear));
        monthSpinner.setSelection(currentMonth);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            updateEventsOnScroll();
        }
    }

    private void updateEventsOnScroll() {
        // TODO: Handle the population of the calendar dates with the proper cycle events:
        // use the CycleIDs to distinguish between the different cycles
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleDayClick(LocalDate date) {
        // Update events list for this day
        if (viewModel != null) {
            List<com.pac.ovum.data.models.Event> dayEvents = viewModel.getEventsForDate(date);
            eventAdapter.setEvents(dayEvents);
            eventAdapter.notifyDataSetChanged();

            // Show action buttons with smooth animation
            showActionButtons();

            // Build a description of the selected date based on combined events
            String dateInfo = buildSelectedDateInfo(date);
            selectedDateTitle.setText(dateInfo);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String buildSelectedDateInfo(LocalDate date) {
        StringBuilder info = new StringBuilder(
                date.format(DateTimeFormatter.ofPattern("d'th' MMMM yyyy")).toString());

        // Add cycle-related information if available
        List<CalendarEventInfo> dateEvents = combinedEventsMap.get(date);
        if (dateEvents != null) {
            boolean cycleInfoAdded = false;

            for (CalendarEventInfo eventInfo : dateEvents) {
                if (eventInfo.isCycleEvent && !cycleInfoAdded) {
                    info.append(" (").append(eventInfo.description).append(")");
                    cycleInfoAdded = true;
                    break; // Only add the first cycle event info
                }
            }
        }

        return info.toString();
    }

    private void showActionButtons() {
        // Show buttons with animation
        if (actionsCard.getVisibility() != View.VISIBLE) {
            actionsCard.setAlpha(0f);
            actionsCard.setVisibility(View.VISIBLE);
            actionsCard.animate().alpha(1f).setDuration(300).start();
        }
    }

    private void hideActionButtons() {
        // Hide buttons with animation
        if (actionsCard.getVisibility() == View.VISIBLE) {
            actionsCard.animate().alpha(0f).setDuration(300).withEndAction(() -> {
                actionsCard.setVisibility(View.GONE);
            }).start();
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
        // Just to be sure if this date is really being passed at this point
        Log.d(TAG, "Selected date for Gyn Event: " + date.toString());
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

            // Remove previous observer if exists
            if (eventsObserver != null) {
                LiveData<List<com.pac.ovum.data.models.Event>> currentEvents =
                        viewModel.getEventsForDateRange(startDate, endDate);
                currentEvents.removeObserver(eventsObserver);
            }

            // Create and add new observer
            eventsObserver = events -> {
                // First store these events in our combined map
                addUserEventsToMap(events);
                // Then render all events
                renderCombinedEvents();
            };

            // Fetch and update events for this date range
            viewModel.getEventsForDateRange(startDate, endDate).observe(
                    getViewLifecycleOwner(), eventsObserver);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void observeCycleData() {
        // Get the currently logged-in user ID
        int userId = getCurrentUserId();

        // Observe cycle data from ViewModel
        viewModel.getAllCycles(userId).observe(getViewLifecycleOwner(), cycleList -> {
            if (cycleList != null && !cycleList.isEmpty()) {
                // Add cycle data to our combined events map
                addCycleDataToMap(cycleList);
                // The user events will be added by their observer
                // We don't render here yet - wait for events to also load
                buildCalendarTitleFromDueDate(dueDate);
            } else {
                Log.d(TAG, "No cycle data available for user ID: " + userId);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addCycleDataToMap(List<CycleData> cycleList) {
        for (CycleData cycle : cycleList) {
            // Add period dates (start to end of period)
            LocalDate periodStart = cycle.getStartDate();
            LocalDate periodEnd = periodStart.plusDays(cycle.getPeriodLength() - 1);
            addDateRangeToMap(periodStart, periodEnd, COLOR_PERIOD, "Period", true);

            // Add ovulation date
            LocalDate ovulationDate = cycle.getOvulationDate();
            addSingleDateToMap(ovulationDate, COLOR_OVULATION, "Ovulation", true);

            // Add fertile window
            LocalDate fertileStart = cycle.getFertileStart();
            LocalDate fertileEnd = cycle.getFertileEnd();
            addDateRangeToMap(fertileStart, fertileEnd, COLOR_FERTILE, "Fertile Window", true);

            // Get the next period start date from the current Cycle-end date to update global variable that stores the due date
            dueDate = cycle.getEndDate().plusDays(1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addUserEventsToMap(List<com.pac.ovum.data.models.Event> events) {
        for (com.pac.ovum.data.models.Event event : events) {
            LocalDate eventDate = event.getEventDate();
            int color = getEventColor(event.getEventType());
            // Add this event to our map
            addSingleDateToMap(eventDate, color, event.getEventType(), event, false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addDateRangeToMap(LocalDate startDate, LocalDate endDate, int color, String eventName, boolean isCycleEvent) {
        if (startDate == null || endDate == null) return;

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            addSingleDateToMap(currentDate, color, eventName, isCycleEvent);
            currentDate = currentDate.plusDays(1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addSingleDateToMap(LocalDate date, int color, String eventName, boolean isCycleEvent) {
        addSingleDateToMap(date, color, eventName, eventName, isCycleEvent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addSingleDateToMap(LocalDate date, int color, String eventName, Object data, boolean isCycleEvent) {
        if (date == null) return;

        // Get or create the list for this date
        List<CalendarEventInfo> eventsForDate = combinedEventsMap.computeIfAbsent(date, k -> new ArrayList<>());
        // Add this event info
        eventsForDate.add(new CalendarEventInfo(color, eventName, data, isCycleEvent));

        Log.d(TAG, "Added to map: " + eventName + " on " + date + " with color " + color);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void renderCombinedEvents() {
        // Clear previous events from calendar
        compactCalendarView.removeAllEvents();

        // Add all events from our combined map to the calendar
        for (Map.Entry<LocalDate, List<CalendarEventInfo>> entry : combinedEventsMap.entrySet()) {
            LocalDate date = entry.getKey();
            List<CalendarEventInfo> events = entry.getValue();

            // Convert date to milliseconds
            long timeInMillis = date.atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();

            // Add each event for this date
            for (CalendarEventInfo eventInfo : events) {
                Event calEvent = new Event(eventInfo.color, timeInMillis, eventInfo.data);
                compactCalendarView.addEvent(calEvent);
            }
        }

        // Refresh the view
        compactCalendarView.invalidate();

        // If we have a selected date, update its display
        if (selectedDate != null) {
            handleDayClick(selectedDate);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializeViewModel() {
        EventRepository eventRepository = AppModule.provideEventRepository(requireContext());
        CycleRepository cycleRepository = AppModule.provideCycleRepository(requireContext());
        viewModel = new ViewModelProvider(this,
                new CalendarViewModelFactory(eventRepository, cycleRepository))
                .get(CalendarViewModel.class);
    }

    private void buildCalendarTitleFromDueDate(LocalDate dueDate) {
        if (dueDate != null) {
            String dueDateString = "Next period starts "
                    + dueDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT)).toString();
            calendarTitle.setText(dueDateString);
        } else {
            calendarTitle.setText("No upcoming periods");
        }
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
            // Optionally change button text if needed
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
                loadAllCalendarData(); // Refresh view after successful sync
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void syncEventsToApi() {
        viewModel.syncAllEventsToApi(getCurrentUserId());
    }

    private int getCurrentUserId() {
        return SharedPrefManager.getInstance(requireContext()).getUserId();
    }

    private int getEventColor(String eventType) {
        // Return different colors based on event type
        if (eventType == null)
            return Color.GRAY;

        switch (eventType.toLowerCase()) {
            case "gyn event":
                return Color.GREEN;
            case "emergency":
                return Color.MAGENTA;
            case "medication":
                return Color.YELLOW;
            case "appointment":
                return Color.CYAN;
            default:
                return Color.GRAY;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Clean up observers to prevent memory leaks
        if (eventsObserver != null && viewModel != null && viewModel.getEvents() != null) {
            viewModel.getEvents().removeObserver(eventsObserver);
            eventsObserver = null;
        }
        binding = null;
    }
}
