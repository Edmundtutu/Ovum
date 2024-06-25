package com.example.ovum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private Spinner monthSpinner;
    private Spinner yearSpinner;
    private GridView calendarGridView;
    private GridView daysOfWeekGridView;
    private String dueDate = "";
    private static String timeStamp = null;
    private String currentDate = "";

    private static final String[] months = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    private final String[] years = {"2023", "2024"};



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_calender, container, false);

        monthSpinner = view.findViewById(R.id.monthSpinner);
        yearSpinner = view.findViewById(R.id.yearSpinner);
        calendarGridView = view.findViewById(R.id.calendarGridView);
        daysOfWeekGridView = view.findViewById(R.id.daysOfWeekGridView);

        ArrayAdapter<String> customMonthSpinnerAdapter = new CustomSpinnerAdapter(requireContext(), months);
        monthSpinner.setAdapter(customMonthSpinnerAdapter);

        ArrayAdapter<String> customYearSpinnerAdapter = new CustomSpinnerAdapter(requireContext(), years);
        yearSpinner.setAdapter(customYearSpinnerAdapter);

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

    private void updateCalendarGrid(String month, String year) {
        String[] daysOfWeek = {"S", "M", "T", "W", "T", "F", "S"};
        ArrayAdapter<String> daysAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, daysOfWeek);
        daysOfWeekGridView.setAdapter(daysAdapter);

        ArrayList<String> days = getDaysForMonthYear(month, year);
        CalendarGridAdapter adapter = new CalendarGridAdapter(requireContext(), days, dueDate);
        calendarGridView.setAdapter(adapter);
    }

    private ArrayList<String> getDaysForMonthYear(String month, String year) {
        ArrayList<String> days = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, getIndex(months, month));
        calendar.set(Calendar.YEAR, Integer.parseInt(year));
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int startingDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        for (int i = 1; i < startingDayOfWeek; i++) {
            days.add("");
        }

        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= daysInMonth; i++) {
            days.add(String.valueOf(i));
        }

        return days;
    }

    private String getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        return months[month];
    }

    private String getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        return String.valueOf(year);
    }

    private int getIndex(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }

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

            // Set data to views
            String day = getItem(position);
            holder.textView.setText(day);

           // Set onClick listener to the grid item
            // due date effect
            // the due date together with the preceding and former 2 days are set to different background colors
//            if (day != null && !day.isEmpty() && day.equals(dueDate)) {
//                convertView.setBackgroundColor(0xFFE57373);
//            } else if (day != null && !day.isEmpty() && (Integer.parseInt(day) == Integer.parseInt(dueDate) - 1 || Integer.parseInt(day) == Integer.parseInt(dueDate) + 1)) {
//                convertView.setBackgroundColor(0xFFEF9A9A);
//            } else if (day != null && !day.isEmpty() && (Integer.parseInt(day) == Integer.parseInt(dueDate) - 2 || Integer.parseInt(day) == Integer.parseInt(dueDate) + 2)) {
//                convertView.setBackgroundColor(0xFFF48FB1);
//            } else {
//                convertView.setBackgroundColor(0x0);
//            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (day != null && !day.isEmpty()) {
                        Log.d("CalendarGridAdapter", "Selected day: " + day);
                        // Redirect to logSymptoms activity tagging the selected day along with the symptoms
                        // if the day is not beyond the current date and set a timestamp event if selected date is beyond the current date
                        if (Integer.parseInt(day) <= Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                            // Redirect to logSymptoms activity tagging the selected day along with the symptoms
                            // if the day is not beyond the current date
                            Log.d("CalendarGridAdapter", "Day is not beyond the current date");

                            // Mark the day and set the logSymptoms activity for that selected date
                            // Apparently lets just redirect to the LogSymptoms activity and pass the selected date as an extra
                            MainActivity activity = (MainActivity) getContext();
                            if (activity != null) {
                                // Start the new activity
                                activity.startActivity(new Intent(activity, LogSymptoms.class));
                            }
                        } else {
                            // Set a timestamp event if selected date is beyond the current date
                            Log.d("CalendarGridAdapter", "Day is beyond the current date");
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
                            calendar.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));
                            calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            timeStamp = sdf.format(calendar.getTime());
                            Log.d("CalendarGridAdapter", "Timestamp: " + timeStamp);
                        }
                    }
                }
            });

//            // set hover effect on the grid item
//            convertView.setOnHoverListener(new View.OnHoverListener() {
//                @Override
//                public boolean onHover(View v, MotionEvent event) {
//                    if (day != null && !day.isEmpty()) {
//                        if (event.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
//                            v.setBackgroundColor(Color.parseColor("#F0F0F0"));
//                        } else if (event.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
//                            v.setBackgroundColor(0x0);
//                        }
//                    }
//                    return true;
//                }
//            });

            return convertView;
        }

        static class ViewHolder {
            TextView textView;
        }
    }


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

            // Set text to TextView
            holder.textView.setText(items[position]);

            return convertView;
        }

        static class ViewHolder {
            TextView textView;
        }
    }

}
