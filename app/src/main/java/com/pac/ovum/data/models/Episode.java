package com.pac.ovum.data.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(
        foreignKeys = @ForeignKey(
                entity = CycleData.class,
                parentColumns = "cycleId",
                childColumns = "cycleId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = @Index("cycleId")
)
public class Episode {
    @PrimaryKey(autoGenerate = true)
    private int episodeId; // Unique ID for the episode
    private int cycleId; // Foreign key to CycleData
    private String symptomType; // Type of symptom (e.g., pain, mood change)
    private LocalDate date; // Date of the episode
    private String notes; // Notes or description of the symptom
    private int intensity; // Symptom intensity on a 1-10 scale

    // Getters and Setters
    public int getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(int episodeId) {
        this.episodeId = episodeId;
    }

    public int getCycleId() {
        return cycleId;
    }

    public void setCycleId(int cycleId) {
        this.cycleId = cycleId;
    }

    public String getSymptomType() {
        return symptomType;
    }

    public void setSymptomType(String symptomType) {
        this.symptomType = symptomType;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }
}
