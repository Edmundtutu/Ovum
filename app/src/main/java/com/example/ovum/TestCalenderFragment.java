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

public class TestCalenderFragment extends Fragment {
    private SharedPrefManager sharedPrefManager;
    private Spinner monthSpinner;
    private Spinner yearSpinner;

    private List<String> months;
    private List<Integer> years;

    private CompactCalendarView compactCalendarView;

    private TextView dueDateTextView;
    private    DateUtils dateUtils;
    private static String timeStamp = null;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_calender, container, false);
        // initialize the due date text view
        dueDateTextView = view.findViewById(R.id.due_date_text_view);
        // set the text to the duDateText view from the values in the sharedPref if it's null set the "Not Determined Yet" to the notDeterminedProvision View
        sharedPrefManager =  SharedPrefManager.getInstance(getContext());
        String dueDate = sharedPrefManager.getDueDate().speechFormat();
        // initialize a date Utils class to handle date converssions
        dateUtils= new DateUtils();

        // Initialize UI components
        yearSpinner = view.findViewById(R.id.yearSpinner);
        monthSpinner = view.findViewById(R.id.monthSpinner);
        compactCalendarView = view.findViewById(R.id.compactcalendar_view);
        // setting the default background of the days
        int defaultbackgroundColor = Color.parseColor("#54FCFCFB");

        // Populate the Month Spinner
        months = new ArrayList<>();
        months.add("January");
        months.add("February");
        months.add("March");
        months.add("April");
        months.add("May");
        months.add("June");
        months.add("July");
        months.add("August");
        months.add("September");
        months.add("October");
        months.add("November");
        months.add("December");

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(getContext(),R.layout.spinner_item_month, months);
        monthAdapter.setDropDownViewResource(R.layout.spinner_item_month);
        monthSpinner.setAdapter(monthAdapter);

        // Populate the Spinner with years
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        years = new ArrayList<>();
        for (int i = 1900; i <= 2040; i++) {
            years.add(i);
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item_year, years);
        adapter.setDropDownViewResource(R.layout.spinner_item_year);
        yearSpinner.setAdapter(adapter);


        // Set default selection to current month and year
        int currentMonth = calendar.get(Calendar.MONTH);
        monthSpinner.setSelection(currentMonth);
        yearSpinner.setSelection(years.indexOf(currentYear));

        // Set up listener for the Month Spinner
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateCalendarAccordingToSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Set up listener for the Year Spinner
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateCalendarAccordingToSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Set first day of week to Monday, defaults to Monday so calling setFirstDayOfWeek is not necessary
        // Use constants provided by Java Calendar class
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);

        // Time is not relevant when querying for events, since events are returned by day.
        // So you can pass in any arbitary DateTime and you will receive all events for that day.
        List<Event> events = compactCalendarView.getEvents(dateUtils.convertToMilliseconds("2024-7-13")); // can also take a Date object

        // events has size 2 with the 2 events inserted previously
        Log.d(TAG, "Events: " + events);

        // define a listener to receive callbacks when certain events happen.
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> events = compactCalendarView.getEvents(dateClicked);
                handleDayClick(dateClicked.toString());
                Log.d(TAG, "Day was clicked: " + dateClicked + " with events " + events);
            }

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

                // update the event of days for the scrolled to monthh
//                updateEventsForCurrentMonth(defaultbackgroundColor);
                addSpecificEvents();
            }
        });
        // Initialize calendar view to current month and year
        updateCalendarAccordingToSpinner();
        // Get current date
        LocalDate today = LocalDate.now(ZoneId.systemDefault());

        // Extract month and year
        int monthToday = today.getMonthValue();
        int yearToday = today.getYear();
        // set the due Date Views if the currently selected month is of the current month to show a prediction of the next month
        if (dueDate != null) {
            // get the first part of the due date i.e. the "dd th" part
            String dueDateArrayParts[] = dueDate.split(" ");
            String dueDatePart = dueDateArrayParts[0];
            if (yearSpinner.getSelectedItem() != null && yearSpinner.getSelectedItem().equals(yearToday)
                    && monthSpinner.getSelectedItemPosition() == (monthToday - 1)) {
                // set the due date text view to the due date
                dueDateTextView.setText(dueDatePart);
            }else{
                // set the  visibility of estimatedPeriodLayout and dueDateLayout to gone
                LinearLayout estimatedPeriodLayout = view.findViewById(R.id.estimated_period_layout);
                LinearLayout dueDateLayout = view.findViewById(R.id.due_date_layout);
                estimatedPeriodLayout.setVisibility(View.GONE);
                dueDateLayout.setVisibility(View.GONE);
            }

        } else {
            // initialize the not determined provision view
            TextView notDeterminedProvision = view.findViewById(R.id.not_determined_provision_text_view);
            notDeterminedProvision.setVisibility(View.VISIBLE);
        }
//        updateEventsForCurrentMonth(defaultbackgroundColor);
        addSpecificEvents();

        return view;
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


    // Handle click events on the calendar days
    private void handleDayClick(String dateStr) {
        if (dateStr != null && !dateStr.isEmpty()) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                Date date = inputFormat.parse(dateStr);
                String formattedDate = outputFormat.format(date);
                Log.d("CalendarGridAdapter", "Formatted Date: " + formattedDate);

                Calendar clickedCalendar = Calendar.getInstance();
                clickedCalendar.setTime(date);

                Calendar currentCalendar = Calendar.getInstance();
                String dayValue = dateUtils.formatDateToSpeech(formattedDate);

                if (!clickedCalendar.after(currentCalendar)) {
                    Log.d("CalendarGridAdapter", "Day is not beyond the current date");
                    LogSymptomsDialogFragment dialogFragment = new LogSymptomsDialogFragment(dayValue);
                    dialogFragment.show(getParentFragmentManager(), "CustomDialog");
                } else {
                    Log.d("CalendarGridAdapter", "Day is beyond the current date");
                    SetGynEventFragment dialogFragment = new SetGynEventFragment(dayValue);
                    dialogFragment.show(getParentFragmentManager(), "CustomDialog");
                }
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("CalendarGridAdapter", "Failed to parse date: " + dateStr);
            }
        }
    }

}
