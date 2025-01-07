package com.example.ovum;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Arrays;

public class TestCalenderFragment extends Fragment {
    // Constants for default values
    private static final int DEFAULT_START_YEAR = 1900;
    private static final int DEFAULT_END_YEAR = 2040;
    private static final String TAG = "TestCalenderFragment";

    private SharedPrefManager sharedPrefManager;
    private Spinner monthSpinner;
    private Spinner yearSpinner;

    private List<String> months;
    private List<Integer> years;

    public static CompactCalendarView compactCalendarView;

    private TextView dueDateTextView;
    private    DateUtils dateUtils;
    private RecyclerView eventsview;
    private static String timeStamp = null;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_calender, container, false);
        initializeViews(view);
        setupSpinners();
        setupCalendar();
        setupListeners();
        updateCalendarAccordingToSpinner();
        return view;
    }

    private void initializeViews(View view) {
        dueDateTextView = view.findViewById(R.id.due_date_text_view);
        sharedPrefManager = SharedPrefManager.getInstance(getContext());
        dateUtils = new DateUtils();
        monthSpinner = view.findViewById(R.id.monthSpinner);
        yearSpinner = view.findViewById(R.id.yearSpinner);
        compactCalendarView = view.findViewById(R.id.compactcalendar_view);
        eventsview = view.findViewById(R.id.events_recycler_view);
    }

    private void setupSpinners() {
        // Populate the Month Spinner
        months = new ArrayList<>(Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"));
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
                List<Event> events = compactCalendarView.getEvents(dateClicked);
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
                addSpecificEvents();
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
        int defaultBackgroundColor = Color.parseColor("#54FCFCFB");
//        updateEventsForCurrentMonth(defaultBackgroundColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            addSpecificEvents();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addSpecificEvents() {
        // get the dueDate Associates ie one day to, two days from etc
        ArrayList<LocalDate> dueDateAssociates = sharedPrefManager.getDueDate().getDatesList();
        if(dueDateAssociates!=null){
           LocalDate dueDateObj = dueDateAssociates.get(0);
           LocalDate oneToDueDate = dueDateAssociates.get(1);
           LocalDate twoToDueDate = dueDateAssociates.get(2);
           LocalDate oneFromDueDate = dueDateAssociates.get(3);
           LocalDate twoFromDueDate = dueDateAssociates.get(4);

           Log.d("Test", "Duedate Event date: " + String.valueOf(dueDateObj) );
           // Add specific events with their own background colors
           Event ev1 = new Event(getResources().getColor(R.color.due_date), dateUtils.convertToMilliseconds(String.valueOf(dueDateObj)), "due date");
           compactCalendarView.addEvent(ev1);

           Event ev2 = new Event(getResources().getColor(R.color.twoTo_due_date), dateUtils.convertToMilliseconds(String.valueOf(twoToDueDate)), "twotoduedate");
           compactCalendarView.addEvent(ev2);

           Event ev3 = new Event(getResources().getColor(R.color.oneTo_due_date), dateUtils.convertToMilliseconds(String.valueOf(oneToDueDate)), "onetoduedate");
           compactCalendarView.addEvent(ev3);

           Event ev4 = new Event(getResources().getColor(R.color.oneFrom_due_date), dateUtils.convertToMilliseconds(String.valueOf(oneFromDueDate)), "onefromduedate");
           compactCalendarView.addEvent(ev4);

           Event ev5 = new Event(getResources().getColor(R.color.twoFrom_due_date), dateUtils.convertToMilliseconds(String.valueOf(twoFromDueDate)), "twofromduedate");
           compactCalendarView.addEvent(ev5);
       }else{
           Log.v("TestCalendar Prediction", "dueDate Not yet in shared Prefs");
       }

        // add other events that are added dynamically
        // get the events of the day from the calendarUtils
        HashMap<LocalDate, String> eventsOfTheDay = CalendarUtils.eventsOfTheDay;
        if(eventsOfTheDay!=null){
            for (LocalDate date : eventsOfTheDay.keySet()) {
                // get the event of the day
                String event = eventsOfTheDay.get(date);
                // add the event to the calendar view
                Event ev = new Event(getResources().getColor(R.color.green_50), dateUtils.convertToMilliseconds(String.valueOf(date)), event);
                compactCalendarView.addEvent(ev);
            }
        }
    }

    private void eventsHandler(){
        // initialis the adapter class for the events
        EventsAdapter eventsAdapter = new EventsAdapter();
        eventsview.setLayoutManager(new LinearLayoutManager(getContext()));
        eventsview.setAdapter(eventsAdapter);
    }

    // Handle click events on the calendar days
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

}

