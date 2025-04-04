package com.pac.ovum.data.services.helpers;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.List;

/**
* Cycle history data.
* This history data encompasses all the data related to the cycle history of the user.
* Ranging From everything from the cycle start date to the cycle end date.
* All the parameters from the
*  - Calendar events
*  - Calendar dates
*  - Symptoms logged
* */
public class CycleHistory {
    @SerializedName("month")
    private String month; // Format "YYYY-MM-DD 00:00:00"
    
    @SerializedName("cycle_length")
    private int cycleLength;
    
    @SerializedName("period_length")
    private int periodLength;
    
    @SerializedName("symptoms")
    private String symptoms; // JSON string of symptoms array
    
    // Constructors
    public CycleHistory() {
    }
    
    public CycleHistory(String month, int cycleLength, int periodLength, String symptoms) {
        this.month = month;
        this.cycleLength = cycleLength;
        this.periodLength = periodLength;
        this.symptoms = symptoms;
    }
    
    // Getters and Setters
    public String getMonth() {
        return month;
    }
    
    public void setMonth(String month) {
        this.month = month;
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
    
    public String getSymptoms() {
        return symptoms;
    }
    
    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }
    
    // Helper methods
    public List<String> parseSymptoms() {
        // Parse JSON string to List<String>
        // This would normally use a JSON parser like Gson
        // For simplicity, we'll return null for now
        return null;
    }
}
