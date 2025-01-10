package com.pac.ovum.data.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity(
        foreignKeys = @ForeignKey(
                entity = CycleData.class,
                parentColumns = "cycleId",
                childColumns = "cycleId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = @Index("cycleId")
)
public class Event {
    @PrimaryKey(autoGenerate = true)
    private int eventId; // Unique event ID
    private int cycleId; // Foreign key to CycleData
    private String eventType; // Type of event (e.g., period start, ovulation)
    private LocalDate eventDate; // Date of the event
    private String description; // Additional details or notes
    private LocalTime eventTime; // Time of the event

    // Getters and Setters
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getCycleId() {
        return cycleId;
    }

    public void setCycleId(int cycleId) {
        this.cycleId = cycleId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalTime eventTime) {
        this.eventTime = eventTime;
    }
}
