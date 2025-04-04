package com.pac.ovum.data.services.helpers;

import com.google.gson.annotations.SerializedName;

/**
 * EventData data model for API communication.
 * This model represents event data for gynecological events, for example doctor visits,
 * medication events, and other events that correlate with menstrual cycle.
 */
public class EventData {
    @SerializedName("id")
    private int id;
    
    @SerializedName("cycle_id")
    private int cycleId;
    
    @SerializedName("user_id")
    private int userId;
    
    @SerializedName("event_date")
    private String eventDate; // Format "YYYY-MM-DD"
    
    @SerializedName("event_time")
    private String eventTime; // Format "HH:MM:SS"
    
    @SerializedName("event_type")
    private String eventType;
    
    @SerializedName("description")
    private String description;
    
    // Constructors
    public EventData() {
    }
    
    public EventData(int cycleId, int userId, String eventDate, String eventTime, String eventType, String description) {
        this.cycleId = cycleId;
        this.userId = userId;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventType = eventType;
        this.description = description;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
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
    
    public String getEventDate() {
        return eventDate;
    }
    
    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }
    
    public String getEventTime() {
        return eventTime;
    }
    
    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
} 