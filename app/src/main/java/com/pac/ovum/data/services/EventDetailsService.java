package com.pac.ovum.data.services;

import android.os.Build;

import androidx.annotation.RequiresApi;
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
 * Service for managing event data with the API.
 * This service handles CRUD operations via HTTP requests to the REST API for gynecological events.
 * This class should be a singleton and thread-safe.
 */
public class EventDetailsService {
    private static EventDetailsService instance;
    private final EventApiService apiService;

    private EventDetailsService() {
        apiService = RetrofitClient.getInstance().getService(EventApiService.class);
    }

    public static EventDetailsService getInstance() {
        if (instance == null) {
            instance = new EventDetailsService();
        }
        return instance;
    }

    /**
     * Get all events for the user
     * @return LiveData containing list of event data
     */
    public LiveData<List<EventData>> getAllEvents() {
        MutableLiveData<List<EventData>> result = new MutableLiveData<>();
        
        AppExecutors.getInstance().networkIO().execute(() -> {
            apiService.getAllEvents().enqueue(new Callback<List<EventData>>() {
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
     * Get events by date range
     * @param startDate Start date (format: "yyyy-MM-dd")
     * @param endDate End date (format: "yyyy-MM-dd")
     * @return LiveData containing list of event data
     */
    public LiveData<List<EventData>> getEventsByDateRange(String startDate, String endDate) {
        MutableLiveData<List<EventData>> result = new MutableLiveData<>();
        
        AppExecutors.getInstance().networkIO().execute(() -> {
            String dateRange = startDate + "," + endDate;
            apiService.getEventsByDateRange(dateRange).enqueue(new Callback<List<EventData>>() {
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
     * Get event by ID
     * @param id Event ID
     * @return LiveData containing event data
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
     * Add new event
     * @param eventData Event data
     * @return LiveData containing the added event data
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
     * Update event
     * @param id Event ID
     * @param eventData Updated event data
     * @return LiveData containing the updated event data
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
     * Delete event
     * @param id Event ID
     * @return LiveData containing success status
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
     * Sync events with server
     * @param eventDataList List of events to sync
     * @return LiveData containing synced events data
     */
    public LiveData<List<EventData>> syncEvents(List<EventData> eventDataList) {
        MutableLiveData<List<EventData>> result = new MutableLiveData<>();
        
        AppExecutors.getInstance().networkIO().execute(() -> {
            apiService.syncEvents(eventDataList).enqueue(new Callback<List<EventData>>() {
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