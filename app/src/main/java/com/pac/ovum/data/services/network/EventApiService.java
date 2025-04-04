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
 * These are majorly going to be appointments with the doctor since the other gyn events do not really involve the Doctor.
 */
public interface EventApiService {
    
    /**
     * Get all events for the user
     * @param userId User ID
     * @return List of events data
     */
    @GET("appointments")
    Call<List<EventData>> getAllEvents(@Query("userId") int userId);
    
    /**
     * Get events by date range
     * @param dateRange like "2023-10-01,2023-10-31"
     * @return List of appointments in the date range
     */
    @GET("appointments")
    Call<List<EventData>> getEventsByDateRange(@Query("appointment_date[within]") String dateRange);
    
    /**
     * Get events by cycle ID
     * @param cycleId Cycle ID
     * @return List of events for the cycle
     */
    @GET("appointments/cycle/{cycleId}") // Not yet Implemented in the REST Api
    Call<List<EventData>> getEventsByCycleId(@Path("cycleId") int cycleId);
    
    /**
     * Get event by ID
     * @param id Event ID
     * @return Event data
     */
    @GET("appointments/{id}")
    Call<EventData> getEventById(@Path("id") int id);
    
    /**
     * Add new event
     * @param eventData Event data
     * @return Added event data
     */
    @POST("appointments")
    Call<EventData> addEvent(@Body EventData eventData);
    
    /**
     * Update event
     * @param id Event ID
     * @param eventData Updated event data
     * @return Updated event data
     */
    @PUT("appointments/{id}")
    Call<EventData> updateEvent(@Path("id") int id, @Body EventData eventData);
    
    /**
     * Delete event
     * @param id Event ID
     * @return Success message
     */
    @DELETE("appointments/{id}/cancel")
    Call<Void> deleteEvent(@Path("id") int id);
    
    /**
     * Sync events with server
     * @param userId User ID
     * @param eventDataList List of events to sync
     * @return Synced events data
     */
    @POST("appointments/sync") // Not yet Implemented in the REST Api
    Call<List<EventData>> syncEvents(@Query("userId") int userId,
                                     @Body List<EventData> eventDataList);
} 