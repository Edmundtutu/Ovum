package com.pac.ovum.data.services;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pac.ovum.data.services.helpers.EventData;
import com.pac.ovum.data.services.network.EventApiService;
import com.pac.ovum.data.services.network.RetrofitClient;
import com.pac.ovum.utils.AppExecutors;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Service for managing event history data with the REST API.
 */
public class EventHistoryService {
    private static EventHistoryService instance;
    private final EventApiService apiService;

    private EventHistoryService() {
        apiService = RetrofitClient.getInstance().getService(EventApiService.class);
    }

    public static EventHistoryService getInstance() {
        if (instance == null) {
            instance = new EventHistoryService();
        }
        return instance;
    }

    /**
     * Get all events for a user.
     * @param userId User ID
     * @return LiveData containing list of events.
     */
    public LiveData<List<EventData>> getAllEvents(int userId) {
        MutableLiveData<List<EventData>> result = new MutableLiveData<>();
        
        AppExecutors.getInstance().networkIO().execute(() -> {
            apiService.getAllEvents(userId).enqueue(new Callback<List<EventData>>() {
                @Override
                public void onResponse(Call<List<EventData>> call, Response<List<EventData>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        result.postValue(response.body());
                    } else {
                        result.postValue(new ArrayList<>());
                    }
                }

                @Override
                public void onFailure(Call<List<EventData>> call, Throwable t) {
                    result.postValue(new ArrayList<>());
                }
            });
        });
        
        return result;
    }

    /**
     * Get events for a user within a date range.
     * @param userId User ID
     * @param startDate Start date in format "yyyy-MM-dd"
     * @param endDate End date in format "yyyy-MM-dd"
     * @return LiveData containing list of events.
     */
    public LiveData<List<EventData>> getEventsByDateRange(int userId, String startDate, String endDate) {
        MutableLiveData<List<EventData>> result = new MutableLiveData<>();
        
        AppExecutors.getInstance().networkIO().execute(() -> {
            apiService.getEventsByDateRange(userId, startDate, endDate)
                    .enqueue(new Callback<List<EventData>>() {
                @Override
                public void onResponse(Call<List<EventData>> call, Response<List<EventData>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        result.postValue(response.body());
                    } else {
                        result.postValue(new ArrayList<>());
                    }
                }

                @Override
                public void onFailure(Call<List<EventData>> call, Throwable t) {
                    result.postValue(new ArrayList<>());
                }
            });
        });
        
        return result;
    }

    /**
     * Get events for a specific cycle.
     * @param cycleId Cycle ID
     * @return LiveData containing list of events.
     */
    public LiveData<List<EventData>> getEventsByCycleId(int cycleId) {
        MutableLiveData<List<EventData>> result = new MutableLiveData<>();
        
        AppExecutors.getInstance().networkIO().execute(() -> {
            apiService.getEventsByCycleId(cycleId).enqueue(new Callback<List<EventData>>() {
                @Override
                public void onResponse(Call<List<EventData>> call, Response<List<EventData>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        result.postValue(response.body());
                    } else {
                        result.postValue(new ArrayList<>());
                    }
                }

                @Override
                public void onFailure(Call<List<EventData>> call, Throwable t) {
                    result.postValue(new ArrayList<>());
                }
            });
        });
        
        return result;
    }

    /**
     * Get event by ID.
     * @param id Event ID
     * @return LiveData containing event.
     */
    public LiveData<EventData> getEventById(int id) {
        MutableLiveData<EventData> result = new MutableLiveData<>();
        
        AppExecutors.getInstance().networkIO().execute(() -> {
            apiService.getEventById(id).enqueue(new Callback<EventData>() {
                @Override
                public void onResponse(Call<EventData> call, Response<EventData> response) {
                    if (response.isSuccessful()) {
                        result.postValue(response.body());
                    } else {
                        result.postValue(null);
                    }
                }

                @Override
                public void onFailure(Call<EventData> call, Throwable t) {
                    result.postValue(null);
                }
            });
        });
        
        return result;
    }

    /**
     * Add a new event.
     * @param eventData Event to add
     * @return LiveData containing added event.
     */
    public LiveData<EventData> addEvent(EventData eventData) {
        MutableLiveData<EventData> result = new MutableLiveData<>();
        
        AppExecutors.getInstance().networkIO().execute(() -> {
            apiService.addEvent(eventData).enqueue(new Callback<EventData>() {
                @Override
                public void onResponse(Call<EventData> call, Response<EventData> response) {
                    if (response.isSuccessful()) {
                        result.postValue(response.body());
                    } else {
                        result.postValue(null);
                    }
                }

                @Override
                public void onFailure(Call<EventData> call, Throwable t) {
                    result.postValue(null);
                }
            });
        });
        
        return result;
    }

    /**
     * Update an event.
     * @param id Event ID
     * @param eventData Updated event
     * @return LiveData containing updated event.
     */
    public LiveData<EventData> updateEvent(int id, EventData eventData) {
        MutableLiveData<EventData> result = new MutableLiveData<>();
        
        AppExecutors.getInstance().networkIO().execute(() -> {
            apiService.updateEvent(id, eventData).enqueue(new Callback<EventData>() {
                @Override
                public void onResponse(Call<EventData> call, Response<EventData> response) {
                    if (response.isSuccessful()) {
                        result.postValue(response.body());
                    } else {
                        result.postValue(null);
                    }
                }

                @Override
                public void onFailure(Call<EventData> call, Throwable t) {
                    result.postValue(null);
                }
            });
        });
        
        return result;
    }

    /**
     * Delete an event.
     * @param id Event ID
     * @return LiveData containing success status.
     */
    public LiveData<Boolean> deleteEvent(int id) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        
        AppExecutors.getInstance().networkIO().execute(() -> {
            apiService.deleteEvent(id).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    result.postValue(response.isSuccessful());
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    result.postValue(false);
                }
            });
        });
        
        return result;
    }

    /**
     * Sync events with server.
     * @param userId User ID
     * @param eventDataList List of events to sync
     * @return LiveData containing synced events.
     */
    public LiveData<List<EventData>> syncEvents(int userId, List<EventData> eventDataList) {
        MutableLiveData<List<EventData>> result = new MutableLiveData<>();
        
        AppExecutors.getInstance().networkIO().execute(() -> {
            apiService.syncEvents(userId, eventDataList).enqueue(new Callback<List<EventData>>() {
                @Override
                public void onResponse(Call<List<EventData>> call, Response<List<EventData>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        result.postValue(response.body());
                    } else {
                        result.postValue(new ArrayList<>());
                    }
                }

                @Override
                public void onFailure(Call<List<EventData>> call, Throwable t) {
                    result.postValue(new ArrayList<>());
                }
            });
        });
        
        return result;
    }
} 