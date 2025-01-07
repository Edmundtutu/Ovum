package com.pac.ovum.utils.data.calendarutils;



import android.os.Build;

import androidx.annotation.RequiresApi;

import com.pac.ovum.data.models.Event;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a day in the calendar with its associated information.
 */
public class DayInfo {
    private final String dayOfWeek;
    private final LocalDate date;
    private final List<Event> events;

    /**
     * Constructs a DayInfo object.
     *
     * @param dayOfWeek the day of the week
     * @param date      the date
     * @param events    the list of events associated with this day
     * @throws IllegalArgumentException if dayOfWeek or date is null
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public DayInfo(String dayOfWeek, String date, List<Event> events) {
        if (dayOfWeek == null || date == null) {
            throw new IllegalArgumentException("Day of week and date cannot be null");
        }
        this.dayOfWeek = dayOfWeek;
        this.date = LocalDate.parse(date);
        this.events = events;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<Event> getEvents() {
        return events;
    }

    @Override
    public String toString() {
        return String.format("DayInfo{dayOfWeek='%s', date=%s, events=%s}", dayOfWeek, date, events);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DayInfo dayInfo = (DayInfo) o;

        if (!dayOfWeek.equals(dayInfo.dayOfWeek)) return false;
        return date.equals(dayInfo.date);
    }

    @Override
    public int hashCode() {
        int result = dayOfWeek.hashCode();
        result = 31 * result + date.hashCode();
        return result;
    }
}
