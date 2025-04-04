package com.pac.ovum.data.services.network;

import com.pac.ovum.data.services.helpers.EventData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit API service interface for gynecological events operations
 */
public interface EventApiService {
    
    /**
     * Get all events for the user
     * @param userId User ID
     * @return List of events data
     */
    @GET("events")
    Call<List<EventData>> getAllEvents(@Query("userId") int userId);
    
    /**
     * Get events by date range
     * @param userId User ID
     * @param startDate Start date in format "yyyy-MM-dd"
     * @param endDate End date in format "yyyy-MM-dd"
     * @return List of events in the date range
     */
    @GET("events/range")
    Call<List<EventData>> getEventsByDateRange(
            @Query("userId") int userId,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate);
    
    /**
     * Get events by cycle ID
     * @param cycleId Cycle ID
     * @return List of events for the cycle
     */
    @GET("events/cycle/{cycleId}")
    Call<List<EventData>> getEventsByCycleId(@Path("cycleId") int cycleId);
    
    /**
     * Get event by ID
     * @param id Event ID
     * @return Event data
     */
    @GET("events/{id}")
    Call<EventData> getEventById(@Path("id") int id);
    
    /**
     * Add new event
     * @param eventData Event data
     * @return Added event data
     */
    @POST("events")
    Call<EventData> addEvent(@Body EventData eventData);
    
    /**
     * Update event
     * @param id Event ID
     * @param eventData Updated event data
     * @return Updated event data
     */
    @PUT("events/{id}")
    Call<EventData> updateEvent(@Path("id") int id, @Body EventData eventData);
    
    /**
     * Delete event
     * @param id Event ID
     * @return Success message
     */
    @DELETE("events/{id}")
    Call<Void> deleteEvent(@Path("id") int id);
    
    /**
     * Sync events with server
     * @param userId User ID
     * @param eventDataList List of events to sync
     * @return Synced events data
     */
    @POST("events/sync")
    Call<List<EventData>> syncEvents(@Query("userId") int userId,
                                     @Body List<EventData> eventDataList);
} 