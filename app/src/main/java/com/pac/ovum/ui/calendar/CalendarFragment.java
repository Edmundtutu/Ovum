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
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import com.pac.ovum.R;
import com.pac.ovum.data.models.Event;
import com.pac.ovum.data.repositories.EventRepository;
import com.pac.ovum.databinding.FragmentCalendarBinding;
import com.pac.ovum.ui.dialogs.LogSymptomsDialogFragment;
import com.pac.ovum.ui.dialogs.SetGynEventFragment;
import com.pac.ovum.utils.AppModule;
import com.pac.ovum.utils.data.calendarutils.DateUtils;

import java.text.ParseException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding binding;

    private static final int DEFAULT_START_YEAR = 1900;
    private static final int DEFAULT_END_YEAR = 2040;
    private static final String TAG = "TestCalenderFragment";

    private Spinner monthSpinner;
    private Spinner yearSpinner;

    private List<Integer> years;

    public static CompactCalendarView compactCalendarView;

    private DateUtils dateUtils;

    private EventAdapter eventAdapter;
    private CalendarViewModel viewModel;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initializeViews();
        setupSpinners();
        setupCalendar();
        setupListeners();
        updateCalendarAccordingToSpinner();

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
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializeViews() {
        TextView dueDateTextView = binding.dueDateTextView;
        dateUtils = new DateUtils();
        monthSpinner = binding.monthSpinner;
        yearSpinner = binding.yearSpinner;
        compactCalendarView = binding.compactcalendarView;
        RecyclerView eventsView = binding.eventsRecyclerView;
    }

    private void setupSpinners() {
        // Populate the Month Spinner
        List<String> months = new ArrayList<>(Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"));
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

    private void setupCalendar() {
        // Set default selection to current month and year
        Calendar calendar = Calendar.getInstance();
        monthSpinner.setSelection(calendar.get(Calendar.MONTH));
        yearSpinner.setSelection(years.indexOf(calendar.get(Calendar.YEAR)));
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);

        // Set up the listener for the CompactCalendarView
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDayClick(Date dateClicked) {
                List<com.github.sundeepk.compactcalendarview.domain.Event> events = compactCalendarView.getEvents(dateClicked);
                handleDayClick(dateClicked.toString());
                Log.d(TAG, "Day was clicked: " + dateClicked + " with events " + events);
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

                // Update events for the scrolled month
                updateEventsOnScroll();
            }
        });
    }


    private void setupListeners() {
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
        int defaultBackgroundColor = Color.parseColor("#54FCFCFB"); // TODO: Use non hardcoded colors
//        updateEventsForCurrentMonth(defaultBackgroundColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            updateEventsOnScroll();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleDayClick(String dateStr) {
        if (isDateStringValid(dateStr)) {
            try {
                Date date = dateUtils.parseDate(dateStr); // Use DateUtils for parsing
                String formattedDate = dateUtils.formatDate(date); // Use DateUtils for formatting
                Log.d(TAG, "Formatted Date: " + formattedDate);

                Calendar clickedCalendar = Calendar.getInstance();
                clickedCalendar.setTime(date);

                Calendar currentCalendar = Calendar.getInstance();
                String dayValue = dateUtils.formatDateToSpeech(formattedDate);

                if (!clickedCalendar.after(currentCalendar)) {
                    Log.d(TAG, "Day is not beyond the current date");
                    showLogSymptomsDialog(dayValue);
                } else {
                    Log.d(TAG, "Day is beyond the current date");
                    showSetGynEventDialog(dayValue);
                }
            } catch (ParseException e) {
                Log.e(TAG, "Failed to parse date: " + dateStr, e);
            }
        }
    }
    private boolean isDateStringValid(String dateStr) {
        return dateStr != null && !dateStr.isEmpty();
    }

    private void showLogSymptomsDialog(String dayValue) {
        LogSymptomsDialogFragment dialogFragment = new LogSymptomsDialogFragment(dayValue);
        dialogFragment.show(getParentFragmentManager(), "CustomDialog");
    }

    private void showSetGynEventDialog(String dayValue) {
        SetGynEventFragment dialogFragment = new SetGynEventFragment(dayValue);
        dialogFragment.show(getParentFragmentManager(), "CustomDialog");
    }

    private void updateEventsOnScroll() {
        // TODO: Handle the population of the calendar dates with the proper cycle events: use the CycleIDs to distinguish between the different cycles
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializeViewModel() {
        EventRepository eventRepository = AppModule.provideEventRepository(getContext()); //new EventRepository(/* pass your EventDao here */);
        CalendarViewModelFactory factory = new CalendarViewModelFactory(eventRepository);
        viewModel = new ViewModelProvider(this, factory).get(CalendarViewModel.class);
        
        // Assuming you have the current cycle ID
        int currentCycleId = getCurrentCycleId(); // TODO: Implement this method to get current cycle ID
        viewModel.getEventsByCycleId(currentCycleId).observe(getViewLifecycleOwner(), events -> {
            eventAdapter.setEvents(events);
            updateCalendarEvents(events);
        });
    }

    private int getCurrentCycleId() {
        return 1; // Placeholder
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateCalendarEvents(List<Event> events) {
        compactCalendarView.removeAllEvents();
        
        for (Event event : events) {
            // Convert your Event to CompactCalendarView.Event
            long timeInMillis = event.getEventDate().atTime(event.getEventTime())
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();
                    
            int color = getEventColor(event.getEventType()); // Implement this method to get color based on event type
            com.github.sundeepk.compactcalendarview.domain.Event calendarEvent = 
                new com.github.sundeepk.compactcalendarview.domain.Event(color, timeInMillis);
            compactCalendarView.addEvent(calendarEvent);
        }
    }

    // Add helper method to get event colors
    private int getEventColor(String eventType) {
        switch (eventType.toLowerCase()) {
            case "period":
                return Color.RED;
            case "ovulation":
                return Color.BLUE;
            // Add more event types and colors as needed
            default:
                return Color.GREEN;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}