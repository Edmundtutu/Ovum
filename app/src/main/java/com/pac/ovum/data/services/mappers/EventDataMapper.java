package com.pac.ovum.data.services.mappers;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.pac.ovum.data.models.Event;
import com.pac.ovum.data.services.helpers.EventData;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mapper class to convert between API models and local database models for Events
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class EventDataMapper {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Convert Event to EventData
     * @param event Local event data
     * @param userId User ID for the event
     * @return EventData API model
     */
    public static EventData toEventData(Event event, int userId) {
        if (event == null) {
            return null;
        }
        
        // Format the date and time strings in the expected API format
        String dateStr = event.getEventDate().format(DATE_FORMATTER);
        String timeStr = event.getEventTime() != null ? 
                event.getEventTime().format(TIME_FORMATTER) : "00:00:00";
        
        EventData eventData = new EventData(
                event.getCycleId(),
                userId,
                dateStr,
                timeStr,
                event.getEventType(),
                event.getDescription()
        );
        
        return eventData;
    }
    
    /**
     * Convert EventData to Event
     * @param eventData API event history
     * @return Event local model
     */
    public static Event toEvent(EventData eventData) {
        if (eventData == null) {
            return null;
        }
        
        // Parse the date and time strings
        LocalDate eventDate = LocalDate.parse(eventData.getEventDate(), DATE_FORMATTER);
        LocalTime eventTime = null;
        if (eventData.getEventTime() != null && !eventData.getEventTime().isEmpty()) {
            eventTime = LocalTime.parse(eventData.getEventTime(), TIME_FORMATTER);
        }
        
        // Create and populate Event object
        Event event = new Event();
        event.setCycleId(eventData.getCycleId());
        event.setEventDate(eventDate);
        event.setEventTime(eventTime);
        event.setEventType(eventData.getEventType());
        event.setDescription(eventData.getDescription());
        
        return event;
    }
    
    /**
     * Convert a list of Event objects to a list of EventData objects
     * @param events List of local Event objects
     * @param userId User ID for the events
     * @return List of EventData objects
     */
    public static List<EventData> toEventDataList(List<Event> events, int userId) {
        if (events == null || events.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<EventData> eventDataList = new ArrayList<>();
        for (Event event : events) {
            EventData eventData = toEventData(event, userId);
            if (eventData != null) {
                eventDataList.add(eventData);
            }
        }
        
        return eventDataList;
    }
    
    /**
     * Convert a list of EventData objects to a list of Event objects
     * @param eventHistories List of API EventData objects
     * @return List of local Event objects
     */
    public static List<Event> toEventList(List<EventData> eventHistories) {
        if (eventHistories == null || eventHistories.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<Event> events = new ArrayList<>();
        for (EventData eventData : eventHistories) {
            Event event = toEvent(eventData);
            if (event != null) {
                events.add(event);
            }
        }
        
        return events;
    }
} 