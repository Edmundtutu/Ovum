package com.pac.ovum.utils.data.calendarutils;


import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a due date and its associated dates.
 */
public class DueDate {
    private final LocalDate dueDate;

    // Constants for the number of days before and after the due date
    public static final int DAYS_BEFORE = 2;
    public static final int DAYS_AFTER = 2;

    /**
     * Constructs a DueDate object from a string representation of the date.
     *
     * @param dueDateString the due date in "yyyy-MM-dd" format
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public DueDate(String dueDateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.dueDate = LocalDate.parse(dueDateString, formatter);
    }

    /**
     * Retrieves a list of dates associated with this due date.
     *
     * @return a list of LocalDate objects representing the due date and its associated dates
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<LocalDate> getAssociatedDates() {
        List<LocalDate> associatedDates = new ArrayList<>();
        associatedDates.add(dueDate); // The due date itself

        // Add days before the due date
        for (int i = 1; i <= DAYS_BEFORE; i++) {
            associatedDates.add(dueDate.minusDays(i));
        }

        // Add days after the due date
        for (int i = 1; i <= DAYS_AFTER; i++) {
            associatedDates.add(dueDate.plusDays(i));
        }

        return associatedDates;
    }

    @Override
    public String toString() {
        return String.format("DueDate{dueDate=%s}", dueDate);
    }
}