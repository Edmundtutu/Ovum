package com.example.ovum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TestCalenderFragment extends Fragment {

    private static Spinner monthSpinner;
    private static Spinner yearSpinner;
    private GridView calendarGridView;
    private GridView daysOfWeekGridView;
    private String dueDate = "";
    private static String timeStamp = null;

    // Month and year arrays for the spinners
    private static final String[] months = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };
    private static final String[] years = {"2023", "2024"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_calender, container, false);

        // Initialize UI elements
        monthSpinner = view.findViewById(R.id.monthSpinner);
        yearSpinner = view.findViewById(R.id.yearSpinner);
        calendarGridView = view.findViewById(R.id.calendarGridView);
        daysOfWeekGridView = view.findViewById(R.id.daysOfWeekGridView);

        // Set up adapters for the spinners
        ArrayAdapter<String> customMonthSpinnerAdapter = new CustomSpinnerAdapter(requireContext(), months);
        monthSpinner.setAdapter(customMonthSpinnerAdapter);

        ArrayAdapter<String> customYearSpinnerAdapter = new CustomSpinnerAdapter(requireContext(), years);
        yearSpinner.setAdapter(customYearSpinnerAdapter);

        // Set up listeners for the spinners
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMonth = months[position];
                String selectedYear = years[yearSpinner.getSelectedItemPosition()];
                updateCalendarGrid(selectedMonth, selectedYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMonth = months[monthSpinner.getSelectedItemPosition()];
                String selectedYear = years[position];
                updateCalendarGrid(selectedMonth, selectedYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Initialize the calendar grid with the current month and year
        String currentMonth = getCurrentMonth();
        String currentYear = getCurrentYear();
        monthSpinner.setSelection(getIndex(months, currentMonth));
        yearSpinner.setSelection(getIndex(years, currentYear));

        return view;
    }

    // Update the calendar grid based on the selected month and year
    private void updateCalendarGrid(String month, String year) {
        String[] daysOfWeek = {"S", "M", "T", "W", "T", "F", "S"};
        ArrayAdapter<String> daysAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, daysOfWeek);
        daysOfWeekGridView.setAdapter(daysAdapter);

        ArrayList<String> days = getDaysForMonthYear(month, year);
        CalendarGridAdapter adapter = new CalendarGridAdapter(requireContext(), days, dueDate);
        calendarGridView.setAdapter(adapter);
    }

    // Get the days for the specified month and year
    private ArrayList<String> getDaysForMonthYear(String month, String year) {
        ArrayList<String> days = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, getIndex(months, month));
        calendar.set(Calendar.YEAR, Integer.parseInt(year));
        calendar.set(Calendar.DAY_OF_MONTH,1);

        int startingDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // Add empty strings for days before the start of the month
        for (int i = 1; i < startingDayOfWeek; i++) {
            days.add("");
        }

        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Add days of the month
        for (int i = 1; i <= daysInMonth; i++) {
            days.add(String.valueOf(i));
        }

        return days;
    }

    // Get the current month as a string
    private String getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        return months[month];
    }

    // Get the current year as a string
    private String getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        return String.valueOf(year);
    }

    // Get the index of a value in an array
    private static int getIndex(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }

    // Custom adapter for the month and year spinners
    private static class CustomSpinnerAdapter extends ArrayAdapter<String> {

        private final String[] items;
        private LayoutInflater inflater;

        public CustomSpinnerAdapter(Context context, String[] items) {
            super(context, R.layout.custom_spinner_item, items);
            this.items = items;
            inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        // Create a custom view for the spinner items
        private View getCustomView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.custom_spinner_item, parent, false);
                holder = new ViewHolder();
                holder.textView = convertView.findViewById(R.id.spinnerText);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textView.setText(items[position]);

            return convertView;
        }

        static class ViewHolder {
            TextView textView;
        }
    }

    // Custom adapter for the calendar grid view
    private static class CalendarGridAdapter extends ArrayAdapter<String> {

        private final ArrayList<String> days;
        private String dueDate;
        private LayoutInflater inflater;

        public CalendarGridAdapter(Context context, ArrayList<String> days, String dueDate) {
            super(context, 0, days);
            this.days = days;
            this.dueDate = dueDate;
            inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.grid_item_calendar, parent, false);
                holder = new ViewHolder();
                holder.textView = convertView.findViewById(R.id.dayTextView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String day = getItem(position);
            if (day != null) {
                holder.textView.setText(day);
                holder.textView.setBackgroundResource(R.drawable.circle_background);

                // Get current date and set the background for the current day
                Calendar calendar = Calendar.getInstance();
                if (isCurrentDate(day, calendar)) {
                    holder.textView.setBackgroundResource(R.drawable.circle_background_current_day);
                }

                if (isDateBeyondCurrent(day)) {
                    convertView.setOnClickListener(null); // Disable clicking for future dates
                    holder.textView.setBackgroundResource(R.drawable.circle_background_disabled);
                } else {
                    convertView.setOnClickListener(null); // Enable clicking for valid dates
                    convertView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    Animation expandAnim = AnimationUtils.loadAnimation(getContext(), R.anim.expand);
                                    holder.textView.startAnimation(expandAnim);
                                    break;
                                case MotionEvent.ACTION_UP:
                                case MotionEvent.ACTION_CANCEL:
                                    Animation shrinkAnim = AnimationUtils.loadAnimation(getContext(), R.anim.shrink);
                                    holder.textView.startAnimation(shrinkAnim);

                                    if (event.getAction() == MotionEvent.ACTION_UP) {
                                        handleDayClick(day);
                                    }
                                    break;
                            }
                            return true;
                        }
                    });
                }
            } else {
                // If day is null or empty, handle it gracefully (optional)
                // For example, you could set a placeholder or disable the view
                holder.textView.setText(""); // Set an empty text or placeholder
                holder.textView.setBackgroundResource(R.drawable.circle_background_disabled); // Set disabled background
                convertView.setClickable(false); // Disable clicking
            }
            return convertView;
        }

        // Check if the given day is the current day
        private boolean isCurrentDate(String day, Calendar calendar) {
            String currentDay = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            String currentMonth = months[calendar.get(Calendar.MONTH)];
            String currentYear = String.valueOf(calendar.get(Calendar.YEAR));
            String selectedMonth = months[monthSpinner.getSelectedItemPosition()];
            String selectedYear = years[yearSpinner.getSelectedItemPosition()];

            return day.equals(currentDay) && selectedMonth.equals(currentMonth) && selectedYear.equals(currentYear);
        }

        // Check if the given day is beyond the current date
        private boolean isDateBeyondCurrent(String day) {
            if (day == null || day.isEmpty()) {
                return false; // or handle this case appropriately
            }

            Calendar today = Calendar.getInstance();
            int currentDay = today.get(Calendar.DAY_OF_MONTH);
            int currentMonth = today.get(Calendar.MONTH);
            int currentYear = today.get(Calendar.YEAR);

            int selectedDay = Integer.parseInt(day);
            String selectedMonth = months[monthSpinner.getSelectedItemPosition()];
            String selectedYear = years[yearSpinner.getSelectedItemPosition()];

            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(Calendar.DAY_OF_MONTH, selectedDay);
            selectedDate.set(Calendar.MONTH, getIndex(months, selectedMonth));
            selectedDate.set(Calendar.YEAR, Integer.parseInt(selectedYear));

            return selectedDate.after(today);
        }

        // Handle click events on the calendar days
        private void handleDayClick(String day) {
            if (day != null && !day.isEmpty()) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
                calendar.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));
                calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String timeStamp = sdf.format(calendar.getTime());
                Log.d("CalendarGridAdapter", "Timestamp: " + timeStamp);
                if (Integer.parseInt(day) <= Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                    Log.d("CalendarGridAdapter", "Day is not beyond the current date");
                    MainActivity activity = (MainActivity) getContext();
                    if (activity != null) {
                        activity.startActivity(new Intent(activity, LogSymptoms.class));
                    }
                } else {
                    Log.d("CalendarGridAdapter", "Day is beyond the current date");
                    // Handle if needed
                }
            }
        }

        static class ViewHolder {
            TextView textView;
        }
    }

}
