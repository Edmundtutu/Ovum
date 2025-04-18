package com.pac.ovum.ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;
import com.pac.ovum.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.time.LocalDate;
import java.util.HashSet;

public class InitialCyclesActivity extends AppCompatActivity {

    private Slider cycleLengthSlider, periodLengthSlider;
    private TextView cycleLengthValue, periodLengthValue;
    private MaterialCalendarView calendarView;
    private MaterialButton saveButton;
    private LocalDate selectedDate;

    // Default values
    private int cycleLength = 28;
    private int periodLength = 5;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_cycles);

        // Initialize views
        initViews();

        // Set up listeners and default values
        setupSliders();
        setupCalendar();
        setupSaveButton();
    }

    private void initViews() {
        cycleLengthSlider = findViewById(R.id.cycleLengthSlider);
        periodLengthSlider = findViewById(R.id.periodLengthSlider); // You'll need to add this to your XML
        cycleLengthValue = findViewById(R.id.cycleLengthValue);
        periodLengthValue = findViewById(R.id.periodLengthValue); // You'll need to add this to your XML
        calendarView = findViewById(R.id.calendarView);
        saveButton = findViewById(R.id.saveButton);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupSliders() {
        // Set up cycle length slider
        cycleLengthSlider.setValue(cycleLength);
        cycleLengthValue.setText(cycleLength + " days");

        cycleLengthSlider.addOnChangeListener((slider, value, fromUser) -> {
            cycleLength = (int) value;
            cycleLengthValue.setText(cycleLength + " days");
            updateFertileWindow();
        });

        // Set up period length slider
        periodLengthSlider.setValue(periodLength);
        periodLengthSlider.setValueFrom(2);
        periodLengthSlider.setValueTo(10);
        periodLengthValue.setText(periodLength + " days");

        periodLengthSlider.addOnChangeListener((slider, value, fromUser) -> {
            periodLength = (int) value;
            periodLengthValue.setText(periodLength + " days");
            updateFertileWindow();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupCalendar() {
        // Get current date
        LocalDate currentDate = LocalDate.now();
        calendarView.setSelectedDate(CalendarDay.from(
                currentDate.getYear(),
                currentDate.getMonthValue() - 1, // MaterialCalendarView months are 0-based
                currentDate.getDayOfMonth()
        ));

        // Set date selection listener
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            selectedDate = LocalDate.of(date.getYear(), date.getMonth() + 1, date.getDay());
            updateFertileWindow();
        });

        // Set date decorators for visualization
        selectedDate = currentDate;
        updateFertileWindow();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateFertileWindow() {
        if (selectedDate == null) return;

        // Clear previous decorators
        calendarView.removeDecorators();

        // Calculate cycle dates
        LocalDate periodEndDate = selectedDate.plusDays(periodLength - 1);
        LocalDate ovulationDate = calculateOvulationDate(selectedDate, cycleLength);
        LocalDate fertileStartDate = ovulationDate.minusDays(5);
        LocalDate fertileEndDate = ovulationDate.plusDays(1);
        LocalDate nextPeriodDate = selectedDate.plusDays(cycleLength);

        // Add decorators for different phases
        calendarView.addDecorators(
                new PeriodDecorator(selectedDate, periodEndDate),
                new FertileDecorator(fertileStartDate, fertileEndDate),
                new OvulationDecorator(ovulationDate),
                new NextPeriodDecorator(nextPeriodDate)
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupSaveButton() {
        saveButton.setOnClickListener(v -> {
            if (selectedDate == null) {
                Toast.makeText(this, "Please select your last period start date", Toast.LENGTH_SHORT).show();
                return;
            }

            // Calculate all necessary cycle data
            LocalDate periodEndDate = selectedDate.plusDays(periodLength - 1);
            LocalDate ovulationDate = calculateOvulationDate(selectedDate, cycleLength);
            LocalDate fertileStartDate = ovulationDate.minusDays(5);
            LocalDate fertileEndDate = ovulationDate.plusDays(1);
            LocalDate cycleEndDate = selectedDate.plusDays(cycleLength);

            // Create intent to pass data back to RegisterActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("START_DATE", selectedDate.toString());
            resultIntent.putExtra("END_DATE", cycleEndDate.toString());
            resultIntent.putExtra("CYCLE_LENGTH", cycleLength);
            resultIntent.putExtra("PERIOD_LENGTH", periodLength);
            resultIntent.putExtra("OVULATION_DATE", ovulationDate.toString());
            resultIntent.putExtra("FERTILE_START", fertileStartDate.toString());
            resultIntent.putExtra("FERTILE_END", fertileEndDate.toString());

            // Set result and finish
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
    }

    /**
     * Calculate the ovulation date based on the period start date and cycle length
     * Typically occurs 14 days before the next period
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private LocalDate calculateOvulationDate(LocalDate periodStartDate, int cycleLength) {
        return periodStartDate.plusDays(cycleLength - 14);
    }

    // Custom decorators for calendar visualization
    private class PeriodDecorator implements DayViewDecorator {
        private final HashSet<CalendarDay> dates;

        @RequiresApi(api = Build.VERSION_CODES.O)
        PeriodDecorator(LocalDate start, LocalDate end) {
            dates = new HashSet<>();
            LocalDate current = start;
            while (!current.isAfter(end)) {
                dates.add(CalendarDay.from(current.getYear(), current.getMonthValue() - 1, current.getDayOfMonth()));
                current = current.plusDays(1);
            }
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(8, getResources().getColor(R.color.colorPrimary)));
            view.addSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)));
        }
    }

    private class FertileDecorator implements DayViewDecorator {
        private final HashSet<CalendarDay> dates;

        @RequiresApi(api = Build.VERSION_CODES.O)
        FertileDecorator(LocalDate start, LocalDate end) {
            dates = new HashSet<>();
            LocalDate current = start;
            while (!current.isAfter(end)) {
                dates.add(CalendarDay.from(current.getYear(), current.getMonthValue() - 1, current.getDayOfMonth()));
                current = current.plusDays(1);
            }
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(8, getResources().getColor(android.R.color.holo_green_light)));
            view.addSpan(new ForegroundColorSpan(getResources().getColor(android.R.color.holo_green_dark)));
        }
    }

    private class OvulationDecorator implements DayViewDecorator {
        private final CalendarDay ovulationDay;

        @RequiresApi(api = Build.VERSION_CODES.O)
        OvulationDecorator(LocalDate ovulationDate) {
            this.ovulationDay = CalendarDay.from(
                    ovulationDate.getYear(),
                    ovulationDate.getMonthValue() - 1,
                    ovulationDate.getDayOfMonth());
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return ovulationDay.equals(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(12, getResources().getColor(R.color.colorOrange)));
            view.addSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorOrangeLight)));
            view.addSpan(new StyleSpan(Typeface.BOLD));
        }
    }

    private class NextPeriodDecorator implements DayViewDecorator {
        private final CalendarDay nextPeriodDay;

        @RequiresApi(api = Build.VERSION_CODES.O)
        NextPeriodDecorator(LocalDate nextPeriodDate) {
            this.nextPeriodDay = CalendarDay.from(
                    nextPeriodDate.getYear(),
                    nextPeriodDate.getMonthValue() - 1,
                    nextPeriodDate.getDayOfMonth());
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return nextPeriodDay.equals(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(10, getResources().getColor(R.color.colorPrimaryDark)));
            view.addSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimaryDark)));
        }
    }
}