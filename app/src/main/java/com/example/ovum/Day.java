package com.example.ovum;

import java.time.LocalDate;
import java.util.Objects;

public class Day {
    private boolean isSelected;
    private boolean isDueDate;
    private boolean isTwoDaysFromDueDate;
    private boolean isOneDayFromDueDate;
    private boolean isTwoDaysToDueDate;
    private boolean isOneDayToDueDate;
    private final LocalDate date;

    public Day(boolean isSelected, boolean isDueDate, boolean isTwoDaysFromDueDate, boolean isOneDayFromDueDate, boolean isTwoDaysToDueDate, boolean isOneDayToDueDate, LocalDate date) {
        this.isSelected = isSelected;
        this.isDueDate = isDueDate;
        this.isTwoDaysFromDueDate = isTwoDaysFromDueDate;
        this.isOneDayFromDueDate = isOneDayFromDueDate;
        this.isTwoDaysToDueDate = isTwoDaysToDueDate;
        this.isOneDayToDueDate = isOneDayToDueDate;
        this.date = date;
    }

    // Getters and Setters
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }

    public boolean isDueDate() { return isDueDate; }
    public void setDueDate(boolean dueDate) { isDueDate = dueDate; }

    public boolean isTwoDaysFromDueDate() { return isTwoDaysFromDueDate; }
    public void setTwoDaysFromDueDate(boolean twoDaysFromDueDate) { isTwoDaysFromDueDate = twoDaysFromDueDate; }

    public boolean isOneDayFromDueDate() { return isOneDayFromDueDate; }
    public void setOneDayFromDueDate(boolean oneDayFromDueDate) { isOneDayFromDueDate = oneDayFromDueDate; }

    public boolean isTwoDaysToDueDate() { return isTwoDaysToDueDate; }
    public void setTwoDaysToDueDate(boolean twoDaysToDueDate) { isTwoDaysToDueDate = twoDaysToDueDate; }

    public boolean isOneDayToDueDate() { return isOneDayToDueDate; }
    public void setOneDayToDueDate(boolean oneDayToDueDate) { isOneDayToDueDate = oneDayToDueDate; }

    public LocalDate getDate() { return date; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Day day = (Day) o;
        return isSelected == day.isSelected &&
                isDueDate == day.isDueDate &&
                isTwoDaysFromDueDate == day.isTwoDaysFromDueDate &&
                isOneDayFromDueDate == day.isOneDayFromDueDate &&
                isTwoDaysToDueDate == day.isTwoDaysToDueDate &&
                isOneDayToDueDate == day.isOneDayToDueDate &&
                date.equals(day.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isSelected, isDueDate, isTwoDaysFromDueDate, isOneDayFromDueDate, isTwoDaysToDueDate, isOneDayToDueDate, date);
    }
}
