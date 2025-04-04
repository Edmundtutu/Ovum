package com.pac.ovum.data.repositories;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.pac.ovum.data.dao.CycleDao;
import com.pac.ovum.data.dao.EpisodeDao;
import com.pac.ovum.data.models.CycleData;
import com.pac.ovum.data.models.Episode;
import com.pac.ovum.data.services.CycleHistoryService;
import com.pac.ovum.data.services.helpers.CycleHistory;
import com.pac.ovum.data.services.mappers.CycleHistoryMapper;
import com.pac.ovum.utils.AppExecutors;

import java.util.ArrayList;
import java.util.List;

public class CycleRepository {
    private final CycleDao cycleDao;
    private final EpisodeDao episodeDao;
    private final CycleHistoryService cycleHistoryService;

    // State variables for tracking sync status
    private final MutableLiveData<Boolean> isSyncing = new MutableLiveData<>(false);
    private final MutableLiveData<String> syncError = new MutableLiveData<>();

    public CycleRepository(CycleDao cycleDao, EpisodeDao episodeDao) {
        this.cycleDao = cycleDao;
        this.episodeDao = episodeDao;
        this.cycleHistoryService = CycleHistoryService.getInstance();
    }

    public void insertCycle(CycleData cycleData) {
        AppExecutors.getInstance().diskIO().execute(() -> cycleDao.insertCycle(cycleData));
    }

    public LiveData<List<CycleData>> getCyclesByUserId(int userId) {
        return cycleDao.getCyclesByUserId(userId);
    }

    public LiveData<CycleData> getCycleById(int cycleId) {
        return cycleDao.getCycleById(cycleId);
    }

    public LiveData<CycleData> getOngoingCycleByUserId(int userId) {
        return cycleDao.getOngoingCycleByUserId(userId);
    }

    public void updateCycle(CycleData cycleData) {
        AppExecutors.getInstance().diskIO().execute(() -> cycleDao.updateCycle(cycleData));
    }

    public void deleteCycle(CycleData cycleData) {
        AppExecutors.getInstance().diskIO().execute(() -> cycleDao.deleteCycle(cycleData));
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
     * Sync cycles from the API to local database
     * @param userId User ID
     * @return LiveData containing success status
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<Boolean> syncCyclesFromApi(int userId) {
        MediatorLiveData<Boolean> result = new MediatorLiveData<>();
        isSyncing.setValue(true);
        syncError.setValue(null);

        // Get cycle history from API
        LiveData<List<CycleHistory>> apiResult = cycleHistoryService.getAllCycleHistory(userId);
        
        result.addSource(apiResult, cycleHistories -> {
            result.removeSource(apiResult);
            
            if (cycleHistories != null && !cycleHistories.isEmpty()) {
                AppExecutors.getInstance().diskIO().execute(() -> {
                    try {
                        // Convert and save each cycle history to local database
                        for (CycleHistory cycleHistory : cycleHistories) {
                            CycleData cycleData = CycleHistoryMapper.toCycleData(cycleHistory, userId);
                            long cycleId = cycleDao.insertCycle(cycleData);
                            
                            // Convert and save episodes for this cycle
                            List<Episode> episodes = CycleHistoryMapper.toEpisodes(
                                    cycleHistory, (int) cycleId);
                            for (Episode episode : episodes) {
                                episodeDao.insertEpisode(episode);
                            }
                        }
                        
                        AppExecutors.getInstance().mainThread().execute(() -> {
                            isSyncing.setValue(false);
                            result.setValue(true);
                        });
                    } catch (Exception e) {
                        AppExecutors.getInstance().mainThread().execute(() -> {
                            syncError.setValue("Error syncing data: " + e.getMessage());
                            isSyncing.setValue(false);
                            result.setValue(false);
                        });
                    }
                });
            } else {
                // No data or error from API
                syncError.setValue("No data available from server");
                isSyncing.setValue(false);
                result.setValue(false);
            }
        });
        
        return result;
    }

    /**
     * Sync cycles from local database to API
     * @param userId User ID
     * @return LiveData containing success status
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<Boolean> syncCyclesToApi(int userId) {
        MediatorLiveData<Boolean> result = new MediatorLiveData<>();
        isSyncing.setValue(true);
        syncError.setValue(null);

        AppExecutors.getInstance().diskIO().execute(() -> {
            try {
                // Get all cycles for user from local database
                List<CycleData> cycleDataList = cycleDao.getCyclesByUserIdSync(userId);
                if (cycleDataList.isEmpty()) {
                    AppExecutors.getInstance().mainThread().execute(() -> {
                        syncError.setValue("No local data to sync");
                        isSyncing.setValue(false);
                        result.setValue(false);
                    });
                    return;
                }
                
                // Get episodes for each cycle
                List<List<Episode>> episodesByCycleId = new ArrayList<>();
                for (CycleData cycleData : cycleDataList) {
                    List<Episode> episodes = episodeDao.getEpisodesByCycleIdSync(cycleData.getCycleId());
                    episodesByCycleId.add(episodes);
                }
                
                // Convert to API model
                List<CycleHistory> cycleHistories = CycleHistoryMapper.toCycleHistoryList(
                        cycleDataList, episodesByCycleId);
                
                // Send to API
                LiveData<List<CycleHistory>> apiResult = cycleHistoryService.syncCycleHistory(
                        userId, cycleHistories);
                
                AppExecutors.getInstance().mainThread().execute(() -> {
                    result.addSource(apiResult, syncedData -> {
                        result.removeSource(apiResult);
                        isSyncing.setValue(false);
                        
                        if (syncedData != null && !syncedData.isEmpty()) {
                            result.setValue(true);
                        } else {
                            syncError.setValue("Error syncing to server");
                            result.setValue(false);
                        }
                    });
                });
            } catch (Exception e) {
                AppExecutors.getInstance().mainThread().execute(() -> {
                    syncError.setValue("Error syncing data: " + e.getMessage());
                    isSyncing.setValue(false);
                    result.setValue(false);
                });
            }
        });
        
        return result;
    }
    
    /**
     * Add a new cycle both locally and to the API
     * @param cycleData Cycle data to add
     * @param episodes Episodes for this cycle
     * @return LiveData containing success status
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<Boolean> addCycleWithSync(CycleData cycleData, List<Episode> episodes) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        
        // First add to local database
        AppExecutors.getInstance().diskIO().execute(() -> {
            try {
                long cycleId = cycleDao.insertCycle(cycleData);
                
                // Update episodes with cycleId
                if (episodes != null) {
                    for (Episode episode : episodes) {
                        episode.setCycleId((int) cycleId);
                        episodeDao.insertEpisode(episode);
                    }
                }
                
                // Then sync to API
                CycleHistory cycleHistory = CycleHistoryMapper.toCycleHistory(
                        cycleData, episodes);
                
                AppExecutors.getInstance().mainThread().execute(() -> {
                    LiveData<CycleHistory> apiResult = cycleHistoryService.addCycleHistory(cycleHistory);
                    
                    // We don't need to handle API result here as local add succeeded
                    // and data will be synced during next full sync
                    result.setValue(true);
                });
            } catch (Exception e) {
                AppExecutors.getInstance().mainThread().execute(() -> {
                    result.setValue(false);
                });
            }
        });
        
        return result;
    }
}
