package com.pac.ovum.data.services.network;

import com.pac.ovum.data.services.helpers.CycleHistory;

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
 * Retrofit API service interface for cycle history operations
 */
public interface CycleHistoryApiService {
    
    /**
     * Get all cycle history data for the user
     * @return List of cycle history data
     */
    @GET("cycle-histories")
    Call<List<CycleHistory>> getAllCycleHistory();
    
    /**
     * Get cycle histories for a specific date range
     * @param dateRange Date range in format "2023-10-01,2023-10-31"
     * @return List of cycle histories in the date range
     */
    @GET("cycle-histories")
    Call<List<CycleHistory>> getCycleHistoriesByDateRange(@Query("month[within]") String dateRange);
    
    /**
     * Get cycle history by ID
     * @param id Cycle history ID
     * @return Cycle history data
     */
    @GET("cycle-histories/{id}")
    Call<CycleHistory> getCycleHistoryById(@Path("id") int id);
    
    /**
     * Add new cycle history
     * @param cycleHistory Cycle history data
     * @return Added cycle history data
     */
    @POST("cycle-histories")
    Call<CycleHistory> addCycleHistory(@Body CycleHistory cycleHistory);
    
    /**
     * Update cycle history
     * @param id Cycle history ID
     * @param cycleHistory Updated cycle history data
     * @return Updated cycle history data
     */
    @PUT("cycle-histories/{id}")
    Call<CycleHistory> updateCycleHistory(@Path("id") int id, @Body CycleHistory cycleHistory);
    
    /**
     * Delete cycle history
     * @param id Cycle history ID
     * @return Success message
     */
    @DELETE("cycle-histories/{id}")
    Call<Void> deleteCycleHistory(@Path("id") int id);
    
    /**
     * Sync cycle history with server
     * @param cycleHistoryList List of cycle history data to sync
     * @return Synced cycle history data
     */
    @POST("cycle-histories/sync")
    Call<List<CycleHistory>> syncCycleHistory(@Body List<CycleHistory> cycleHistoryList);
} 