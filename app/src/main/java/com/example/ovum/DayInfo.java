package com.example.ovum;

import com.example.ovum.models.Event;

import java.util.List;

public class DayInfo {
    private final String dayOfWeek;
    private final String date;

    private final List<Event> events;


    /**  A day in the ovum calendar should havethe following features
     * A date eg 10/19/2222
     * A day of the week eg Mon- Sun
     * An event/events (should be a list of events)
     */
    public DayInfo(String dayOfWeek, String date, List<Event> events) {
        this.dayOfWeek = dayOfWeek;
        this.date = date;
        this.events = events;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "DayInfo{" +
                "dayOfWeek='" + dayOfWeek + '\'' +
                ", date='" + date + '\'' +
                '}';
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
