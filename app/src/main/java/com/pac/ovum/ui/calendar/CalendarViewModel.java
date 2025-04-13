package com.pac.ovum.ui.calendar;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.pac.ovum.data.models.Event;
import com.pac.ovum.data.repositories.EventRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarViewModel extends ViewModel {
    private final EventRepository eventRepository;
    private LiveData<List<Event>> events;
    
    // LiveData for tracking sync state
    private final MediatorLiveData<Boolean> isSyncing = new MediatorLiveData<>();
    private final MediatorLiveData<String> syncError = new MediatorLiveData<>();
    private final MediatorLiveData<Boolean> syncSuccess = new MediatorLiveData<>();

    public CalendarViewModel(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
        
        // Initialize sync status observation
        isSyncing.addSource(eventRepository.getIsSyncing(), value -> {
            isSyncing.setValue(value);
        });
        
        // Initialize error observation
        syncError.addSource(eventRepository.getSyncError(), value -> {
            if (value != null && !value.isEmpty()) {
                syncError.setValue(value);
            }
        });
    }

    public LiveData<List<Event>> getEvents() {
        return events;
    }

    public LiveData<List<Event>> getEventsByCycleId(int cycleId) {
        events = eventRepository.getEventsByCycleId(cycleId);
        return events;
    }

    public void insertEvent(Event event) {
        eventRepository.insertEvent(event);
    }

    public void updateEvent(Event event) {
        eventRepository.updateEvent(event);
    }

    public void deleteEvent(Event event) {
        eventRepository.deleteEvent(event);
    }

    public List<Event> getEventsForDate(LocalDate date) {
        List<Event> currentEvents = events.getValue();
        if (currentEvents != null) {
            return currentEvents.stream()
                    .filter(event -> event.getEventDate().equals(date))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
    
    /**
     * Get events for a date range
     * @param startDate Start date of the range
     * @param endDate End date of the range
     * @return LiveData containing events in the date range
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<List<Event>> getEventsForDateRange(LocalDate startDate, LocalDate endDate) {
        events = eventRepository.getEventsForDateRange(startDate, endDate);
        return events;
    }

    /**
     * Get whether the data is currently syncing
     * @return LiveData containing sync status
     */
    public LiveData<Boolean> getIsSyncing() {
        return isSyncing;
    }
    
    /**
     * Get any sync errors
     * @return LiveData containing error message
     */
    public LiveData<String> getSyncError() {
        return syncError;
    }
    
    /**
     * Get sync success status
     * @return LiveData containing success status
     */
    public LiveData<Boolean> getSyncSuccess() {
        return syncSuccess;
    }
    
    /**
     * Insert an event and sync with API
     * @param event Event to insert
     * @param userId User ID
     * @return LiveData containing inserted event ID
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<Long> insertEventWithSync(Event event, int userId) {
        return eventRepository.insertEventWithSync(event, userId);
    }
    
    /**
     * Update an event and sync with API
     * @param event Event to update
     * @param userId User ID
     * @return LiveData containing success status
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<Boolean> updateEventWithSync(Event event, int userId) {
        return eventRepository.updateEventWithSync(event, userId);
    }
    
    /**
     * Delete an event and sync with API
     * @param event Event to delete
     * @return LiveData containing success status
     */
    public LiveData<Boolean> deleteEventWithSync(Event event) {
        return eventRepository.deleteEventWithSync(event);
    }
    
    /**
     * Sync events from API to local database for a specific date range
     * @param userId User ID for local model
     * @param startDate Start date
     * @param endDate End date
     * @return LiveData containing success status
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<Boolean> syncEventsForDateRange(int userId, LocalDate startDate, LocalDate endDate) {
        MediatorLiveData<Boolean> result = new MediatorLiveData<>();
        syncError.setValue(null);
        
        // Start sync from repository
        LiveData<Boolean> syncResult = eventRepository.syncEventsByDateRange(startDate, endDate);
        result.addSource(syncResult, success -> {
            result.removeSource(syncResult);
            
            if (success) {
                syncSuccess.setValue(true);
                result.setValue(true);
                
                // Refresh events for the date range
                events = eventRepository.getEventsForDateRange(startDate, endDate);
            } else {
                String error = syncError.getValue();
                if (error == null || error.isEmpty()) {
                    syncError.setValue("Failed to sync events for date range");
                }
                result.setValue(false);
            }
        });
        
        return result;
    }
    
    /**
     * Sync all events from API to local database
     * @param userId User ID for local model
     * @return LiveData containing success status
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<Boolean> syncAllEventsFromApi(int userId) {
        MediatorLiveData<Boolean> result = new MediatorLiveData<>();
        syncError.setValue(null);
        
        // Start sync from repository
        LiveData<Boolean> syncResult = eventRepository.syncEventsFromApi();
        result.addSource(syncResult, success -> {
            result.removeSource(syncResult);
            
            if (success) {
                syncSuccess.setValue(true);
                result.setValue(true);
            } else {
                String error = syncError.getValue();
                if (error == null || error.isEmpty()) {
                    syncError.setValue("Failed to sync events from server");
                }
                result.setValue(false);
            }
        });
        
        return result;
    }
    
    /**
     * Sync all events from local database to API
     * @param userId User ID for API model
     * @return LiveData containing success status
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<Boolean> syncAllEventsToApi(int userId) {
        MediatorLiveData<Boolean> result = new MediatorLiveData<>();
        syncError.setValue(null);
        
        // Start sync from repository
        LiveData<Boolean> syncResult = eventRepository.syncEventsToApi(userId);
        result.addSource(syncResult, success -> {
            result.removeSource(syncResult);
            
            if (success) {
                syncSuccess.setValue(true);
                result.setValue(true);
            } else {
                String error = syncError.getValue();
                if (error == null || error.isEmpty()) {
                    syncError.setValue("Failed to sync events to server");
                }
                result.setValue(false);
            }
        });
        
        return result;
    }
}