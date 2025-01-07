package com.pac.ovum.data.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "userId",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = @Index("userId")
)
public class CycleData {
    @PrimaryKey(autoGenerate = true)
    private int cycleId; // Unique cycle ID
    private int userId; // Foreign key to User
    private LocalDate startDate; // Start date of the cycle
    private LocalDate endDate; // End date of the cycle (optional for ongoing)
    private int cycleLength; // Number of days in the cycle
    private int periodLength; // Length of the period in days
    private LocalDate ovulationDate; // Changed to Date
    private LocalDate fertileStart; // Changed to Date
    private LocalDate fertileEnd; // Changed to Date
    private boolean isOngoing; // True if the cycle is not yet completed

    // Getters and Setters
    public int getCycleId() {
        return cycleId;
    }

    public void setCycleId(int cycleId) {
        this.cycleId = cycleId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getCycleLength() {
        return cycleLength;
    }

    public void setCycleLength(int cycleLength) {
        this.cycleLength = cycleLength;
    }

    public int getPeriodLength() {
        return periodLength;
    }

    public void setPeriodLength(int periodLength) {
        this.periodLength = periodLength;
    }

    public LocalDate getOvulationDate() {
        return ovulationDate;
    }

    public void setOvulationDate(LocalDate ovulationDate) {
        this.ovulationDate = ovulationDate;
    }

    public LocalDate getFertileStart() {
        return fertileStart;
    }

    public void setFertileStart(LocalDate fertileStart) {
        this.fertileStart = fertileStart;
    }

    public LocalDate getFertileEnd() {
        return fertileEnd;
    }

    public void setFertileEnd(LocalDate fertileEnd) {
        this.fertileEnd = fertileEnd;
    }

    public boolean isOngoing() {
        return isOngoing;
    }

    public void setOngoing(boolean ongoing) {
        isOngoing = ongoing;
    }
}
