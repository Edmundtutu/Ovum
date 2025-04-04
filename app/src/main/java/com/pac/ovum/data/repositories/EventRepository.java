package com.pac.ovum.data.repositories;

import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.pac.ovum.data.dao.EventDao;
import com.pac.ovum.data.models.Event;
import com.pac.ovum.data.services.EventDetailsService;
import com.pac.ovum.data.services.helpers.EventData;
import com.pac.ovum.data.services.mappers.EventDataMapper;
import com.pac.ovum.utils.AppExecutors;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EventRepository {
    private final EventDao eventDao;
    private final EventDetailsService eventDetailsService;
    
    // State variables for tracking sync status
    private final MutableLiveData<Boolean> isSyncing = new MutableLiveData<>(false);
    private final MutableLiveData<String> syncError = new MutableLiveData<>();

    public EventRepository(EventDao eventDao) {
        this.eventDao = eventDao;
        this.eventDetailsService = EventDetailsService.getInstance();
    }

    public void insertEvent(Event event) {
        AppExecutors.getInstance().diskIO().execute(() -> eventDao.insertEvent(event));
    }

    public LiveData<List<Event>> getEventsByCycleId(int cycleId) {
        return eventDao.getEventsByCycleId(cycleId);
    }

    public LiveData<Event> getEventById(int eventId) {
        return eventDao.getEventById(eventId);
    }

    public LiveData<List<Event>> getEventsByTypeAndCycle(String eventType, int cycleId) {
        return eventDao.getEventsByTypeAndCycle(eventType, cycleId);
    }

    public void updateEvent(Event event) {
        AppExecutors.getInstance().diskIO().execute(() -> eventDao.updateEvent(event));
    }

    public void deleteEvent(Event event) {
        AppExecutors.getInstance().diskIO().execute(() -> eventDao.deleteEvent(event));
    }

    public LiveData<List<Event>> getEventsForDate(LocalDate date) {
        return eventDao.getEventsForDate(date);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<List<Event>> getEventsForDateRange(LocalDate startDate, LocalDate endDate) {
        return eventDao.getEventsForDateRange(startDate, endDate);
    }
    
    /**
     * Get the current syncing status
     * @return LiveData containing sync status
     */
    public LiveData<Boolean> getIsSyncing() {
        return isSyncing;
    }

    /**
     * Get any sync errors
     * @return LiveData containing sync error message
     */
    public LiveData<String> getSyncError() {
        return syncError;
    }
    
    /**
     * Insert an event and sync with API
     * @param event Event to insert
     * @param userId User ID
     * @return LiveData containing the inserted event ID
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<Long> insertEventWithSync(Event event, int userId) {
        MutableLiveData<Long> result = new MutableLiveData<>();
        
        AppExecutors.getInstance().diskIO().execute(() -> {
            try {
                // Insert locally first
                long eventId = eventDao.insertEvent(event);
                
                // Now sync with API
                EventData eventData = com.pac.ovum.data.services.mappers.EventDataMapper.toEventData(event, userId);
                eventDetailsService.addEvent(eventData);
                
                AppExecutors.getInstance().mainThread().execute(() -> result.setValue(eventId));
            } catch (Exception e) {
                AppExecutors.getInstance().mainThread().execute(() -> result.setValue(-1L));
            }
        });
        
        return result;
    }
    
    /**
     * Update an event and sync with API
     * @param event Event to update
     * @param userId User ID
     * @return LiveData containing success status
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<Boolean> updateEventWithSync(Event event, int userId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        
        AppExecutors.getInstance().diskIO().execute(() -> {
            try {
                // Update locally first
                eventDao.updateEvent(event);
                
                // Now sync with API
                EventData eventData = EventDataMapper.toEventData(event, userId);
                eventDetailsService.updateEvent(event.getEventId(), eventData);
                
                AppExecutors.getInstance().mainThread().execute(() -> result.setValue(true));
            } catch (Exception e) {
                AppExecutors.getInstance().mainThread().execute(() -> result.setValue(false));
            }
        });
        
        return result;
    }
    
    /**
     * Delete an event and sync with API
     * @param event Event to delete
     * @return LiveData containing success status
     */
    public LiveData<Boolean> deleteEventWithSync(Event event) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        
        AppExecutors.getInstance().diskIO().execute(() -> {
            try {
                // Delete locally first
                eventDao.deleteEvent(event);
                
                // Now sync with API
                eventDetailsService.deleteEvent(event.getEventId());
                
                AppExecutors.getInstance().mainThread().execute(() -> result.setValue(true));
            } catch (Exception e) {
                AppExecutors.getInstance().mainThread().execute(() -> result.setValue(false));
            }
        });
        
        return result;
    }
    
    /**
     * Sync events from API to local database
     * @param userId User ID
     * @return LiveData containing success status
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<Boolean> syncEventsFromApi(int userId) {
        MediatorLiveData<Boolean> result = new MediatorLiveData<>();
        isSyncing.setValue(true);
        syncError.setValue(null);
        
        // Get events from API
        LiveData<List<EventData>> apiResult = eventDetailsService.getAllEvents(userId);
        
        result.addSource(apiResult, eventHistories -> {
            result.removeSource(apiResult);
            
            if (eventHistories != null && !eventHistories.isEmpty()) {
                AppExecutors.getInstance().diskIO().execute(() -> {
                    try {
                        // Convert and save each event to local database
                        List<Event> events = com.pac.ovum.data.services.mappers.EventDataMapper.toEventList(eventHistories);
                        for (Event event : events) {
                            eventDao.insertEvent(event);
                        }
                        
                        AppExecutors.getInstance().mainThread().execute(() -> {
                            isSyncing.setValue(false);
                            result.setValue(true);
                        });
                    } catch (Exception e) {
                        AppExecutors.getInstance().mainThread().execute(() -> {
                            syncError.setValue("Error syncing events: " + e.getMessage());
                            isSyncing.setValue(false);
                            result.setValue(false);
                        });
                    }
                });
            } else {
                syncError.setValue("No events available from server");
                isSyncing.setValue(false);
                result.setValue(false);
            }
        });
        
        return result;
    }
    
    /**
     * Sync events with API by date range
     * @param userId User ID
     * @param startDate Start date
     * @param endDate End date
     * @return LiveData containing success status
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<Boolean> syncEventsByDateRange(int userId, LocalDate startDate, LocalDate endDate) {
        MediatorLiveData<Boolean> result = new MediatorLiveData<>();
        isSyncing.setValue(true);
        syncError.setValue(null);
        
        // Format dates for API
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDateStr = startDate.format(formatter);
        String endDateStr = endDate.format(formatter);
        
        // Get events from API
        LiveData<List<EventData>> apiResult =
                eventDetailsService.getEventsByDateRange(userId, startDateStr, endDateStr);
        
        result.addSource(apiResult, eventHistories -> {
            result.removeSource(apiResult);
            
            if (eventHistories != null && !eventHistories.isEmpty()) {
                AppExecutors.getInstance().diskIO().execute(() -> {
                    try {
                        // Convert and save each event to local database
                        List<Event> events = com.pac.ovum.data.services.mappers.EventDataMapper.toEventList(eventHistories);
                        for (Event event : events) {
                            eventDao.insertEvent(event);
                        }
                        
                        AppExecutors.getInstance().mainThread().execute(() -> {
                            isSyncing.setValue(false);
                            result.setValue(true);
                        });
                    } catch (Exception e) {
                        AppExecutors.getInstance().mainThread().execute(() -> {
                            syncError.setValue("Error syncing events: " + e.getMessage());
                            isSyncing.setValue(false);
                            result.setValue(false);
                        });
                    }
                });
            } else {
                isSyncing.setValue(false);
                syncError.setValue("No events found for date range");
                result.setValue(false);
            }
        });
        
        return result;
    }
    
    /**
     * Sync local events to API
     * @param userId User ID
     * @return LiveData containing success status
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<Boolean> syncEventsToApi(int userId) {
        MediatorLiveData<Boolean> result = new MediatorLiveData<>();
        isSyncing.setValue(true);
        syncError.setValue(null);
        
        AppExecutors.getInstance().diskIO().execute(() -> {
            try {
                // Get all events from local database
                List<Event> events = eventDao.getAllEventsSync();
                
                if (events.isEmpty()) {
                    AppExecutors.getInstance().mainThread().execute(() -> {
                        syncError.setValue("No local events to sync");
                        isSyncing.setValue(false);
                        result.setValue(false);
                    });
                    return;
                }
                
                // Convert to API model
                List<EventData> eventHistories = com.pac.ovum.data.services.mappers.EventDataMapper.toEventDataList(events, userId);
                
                // Send to API
                LiveData<List<EventData>> apiResult = eventDetailsService.syncEvents(userId, eventHistories);
                
                AppExecutors.getInstance().mainThread().execute(() -> {
                    result.addSource(apiResult, syncedData -> {
                        result.removeSource(apiResult);
                        isSyncing.setValue(false);
                        
                        if (syncedData != null && !syncedData.isEmpty()) {
                            result.setValue(true);
                        } else {
                            syncError.setValue("Error syncing events to server");
                            result.setValue(false);
                        }
                    });
                });
            } catch (Exception e) {
                AppExecutors.getInstance().mainThread().execute(() -> {
                    syncError.setValue("Error syncing events: " + e.getMessage());
                    isSyncing.setValue(false);
                    result.setValue(false);
                });
            }
        });
        
        return result;
    }
}
