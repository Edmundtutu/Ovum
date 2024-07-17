package com.example.ovum;

public class DayInfo {
    private final String dayOfWeek;
    private final String date;

    public DayInfo(String dayOfWeek, String date) {
        this.dayOfWeek = dayOfWeek;
        this.date = date;
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
