package com.pac.ovum.data.services;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pac.ovum.data.services.helpers.CycleHistory;
import com.pac.ovum.data.services.network.CycleHistoryApiService;
import com.pac.ovum.data.services.network.RetrofitClient;
import com.pac.ovum.utils.AppExecutors;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Service for managing cycle history data.
 * This service will be used to  make CRUD operations via HTTP requests to the REST API on cycle history data in the central Db.
 * Object of this class should be a singleton instance and should be thread-safe.
 * Instance will be create at registration time of the user and and destroyed at user logout for secure data access.
 */
public class CycleHistoryService {

    private static CycleHistoryService instance;
    private final CycleHistoryApiService apiService;

    private CycleHistoryService() {
        apiService = RetrofitClient.getInstance().getService(CycleHistoryApiService.class);
    }

    public static CycleHistoryService getInstance() {
        if (instance == null) {
            instance = new CycleHistoryService();
        }
        return instance;
    }

    /**
     * Get all cycle history data for the user.
     * @param userId User ID
     * @return LiveData containing list of cycle history data.
     */
    public LiveData<List<CycleHistory>> getAllCycleHistory(int userId) {
        MutableLiveData<List<CycleHistory>> result = new MutableLiveData<>();
        
        AppExecutors.getInstance().networkIO().execute(() -> {
            apiService.getAllCycleHistory(userId).enqueue(new Callback<List<CycleHistory>>() {
                @Override
                public void onResponse(Call<List<CycleHistory>> call, Response<List<CycleHistory>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        result.postValue(response.body());
                    } else {
                        result.postValue(new ArrayList<>());
                    }
                }

                @Override
                public void onFailure(Call<List<CycleHistory>> call, Throwable t) {
                    result.postValue(new ArrayList<>());
                }
            });
        });
        
        return result;
    }

    /**
     * Get cycle history data by id.
     * @param id Cycle history id.
     * @return LiveData containing cycle history data.
     */
    public LiveData<CycleHistory> getCycleHistoryById(int id) {
        MutableLiveData<CycleHistory> result = new MutableLiveData<>();
        
        AppExecutors.getInstance().networkIO().execute(() -> {
            apiService.getCycleHistoryById(id).enqueue(new Callback<CycleHistory>() {
                @Override
                public void onResponse(Call<CycleHistory> call, Response<CycleHistory> response) {
                    if (response.isSuccessful()) {
                        result.postValue(response.body());
                    } else {
                        result.postValue(null);
                    }
                }

                @Override
                public void onFailure(Call<CycleHistory> call, Throwable t) {
                    result.postValue(null);
                }
            });
        });
        
        return result;
    }

    /**
     * Add new cycle history data.
     * @param cycleHistory Cycle history data.
     * @return LiveData containing the added cycle history data.
     */
    public LiveData<CycleHistory> addCycleHistory(CycleHistory cycleHistory) {
        MutableLiveData<CycleHistory> result = new MutableLiveData<>();
        
        AppExecutors.getInstance().networkIO().execute(() -> {
            apiService.addCycleHistory(cycleHistory).enqueue(new Callback<CycleHistory>() {
                @Override
                public void onResponse(Call<CycleHistory> call, Response<CycleHistory> response) {
                    if (response.isSuccessful()) {
                        result.postValue(response.body());
                    } else {
                        result.postValue(null);
                    }
                }

                @Override
                public void onFailure(Call<CycleHistory> call, Throwable t) {
                    result.postValue(null);
                }
            });
        });
        
        return result;
    }

    /**
     * Update cycle history data.
     * @param id Cycle history ID
     * @param cycleHistory Updated cycle history data.
     * @return LiveData containing the updated cycle history data.
     */
    public LiveData<CycleHistory> updateCycleHistory(int id, CycleHistory cycleHistory) {
        MutableLiveData<CycleHistory> result = new MutableLiveData<>();
        
        AppExecutors.getInstance().networkIO().execute(() -> {
            apiService.updateCycleHistory(id, cycleHistory).enqueue(new Callback<CycleHistory>() {
                @Override
                public void onResponse(Call<CycleHistory> call, Response<CycleHistory> response) {
                    if (response.isSuccessful()) {
                        result.postValue(response.body());
                    } else {
                        result.postValue(null);
                    }
                }

                @Override
                public void onFailure(Call<CycleHistory> call, Throwable t) {
                    result.postValue(null);
                }
            });
        });
        
        return result;
    }

    /**
     * Delete cycle history data by id.
     * @param id Cycle history id.
     * @return LiveData containing success status.
     */
    public LiveData<Boolean> deleteCycleHistory(int id) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        
        AppExecutors.getInstance().networkIO().execute(() -> {
            apiService.deleteCycleHistory(id).enqueue(new Callback<Void>() {
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
     * Sync cycle history data with server.
     * @param userId User ID
     * @param cycleHistoryList List of cycle history data to sync
     * @return LiveData containing synced cycle history data
     */
    public LiveData<List<CycleHistory>> syncCycleHistory(int userId, List<CycleHistory> cycleHistoryList) {
        MutableLiveData<List<CycleHistory>> result = new MutableLiveData<>();
        
        AppExecutors.getInstance().networkIO().execute(() -> {
            apiService.syncCycleHistory(userId, cycleHistoryList).enqueue(new Callback<List<CycleHistory>>() {
                @Override
                public void onResponse(Call<List<CycleHistory>> call, Response<List<CycleHistory>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        result.postValue(response.body());
                    } else {
                        result.postValue(new ArrayList<>());
                    }
                }

                @Override
                public void onFailure(Call<List<CycleHistory>> call, Throwable t) {
                    result.postValue(new ArrayList<>());
                }
            });
        });
        
        return result;
    }
}
