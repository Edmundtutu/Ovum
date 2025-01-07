package com.pac.ovum.utils.data.calendarutils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.util.ArrayList;

public class HorizontalCalendarUtils {
    public static LocalDate selectedDate;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<LocalDate> daysInWeekArray(LocalDate selectedDate) {
        ArrayList<LocalDate> days = new ArrayList<>();
        LocalDate current = sundayForDate(selectedDate);
        assert current != null;
        LocalDate endDate = current.plusWeeks(1);

        while (current.isBefore(endDate)) {
            days.add(current);
            current = current.plusDays(1);
        }
        return days;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static LocalDate sundayForDate(LocalDate current) {
        LocalDate oneWeekAgo = current.minusWeeks(1);

        while (current.isAfter(oneWeekAgo)) {
            if (current.getDayOfWeek() == current.minusDays(7).getDayOfWeek())
                return current;

            current = current.minusDays(1);
        }

        return null;
    }
}
