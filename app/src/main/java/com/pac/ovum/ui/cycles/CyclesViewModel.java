package com.pac.ovum.ui.cycles;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.pac.ovum.data.models.CycleData;
import com.pac.ovum.data.models.CycleSummary;
import com.pac.ovum.data.repositories.CycleRepository;
import com.pac.ovum.data.repositories.EpisodeRepository;
import com.pac.ovum.utils.ui.LiveDataUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CyclesViewModel extends ViewModel {
    private final CycleRepository cycleRepository;
    private final EpisodeRepository episodeRepository;
    
    // LiveData for tracking sync state
    private final MediatorLiveData<Boolean> isSyncing = new MediatorLiveData<>();
    private final MediatorLiveData<String> syncError = new MediatorLiveData<>();
    private final MediatorLiveData<Boolean> syncSuccess = new MediatorLiveData<>();

    public CyclesViewModel(CycleRepository cycleRepository, EpisodeRepository episodeRepository) {
        this.cycleRepository = cycleRepository;
        this.episodeRepository = episodeRepository;
        
        // Initialize sync status observation
        isSyncing.addSource(cycleRepository.getIsSyncing(), value -> {
            if (value != null && value) {
                isSyncing.setValue(true);
            } else if (episodeRepository.getIsSyncing().getValue() == null || 
                    !episodeRepository.getIsSyncing().getValue()) {
                isSyncing.setValue(false);
            }
        });
        
        isSyncing.addSource(episodeRepository.getIsSyncing(), value -> {
            if (value != null && value) {
                isSyncing.setValue(true);
            } else if (cycleRepository.getIsSyncing().getValue() == null || 
                    !cycleRepository.getIsSyncing().getValue()) {
                isSyncing.setValue(false);
            }
        });
        
        // Initialize error observation
        syncError.addSource(cycleRepository.getSyncError(), value -> {
            if (value != null && !value.isEmpty()) {
                syncError.setValue(value);
            }
        });
        
        syncError.addSource(episodeRepository.getSyncError(), value -> {
            if (value != null && !value.isEmpty()) {
                syncError.setValue(value);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<List<CycleSummary>> getCycleSummaries(int userId) {
        // Fetch CycleData from the repository
        LiveData<List<CycleData>> cyclesLiveData = cycleRepository.getCyclesByUserId(userId);

        // Transform CycleData into CycleSummary with live episode data
        return Transformations.switchMap(cyclesLiveData, cycles -> {
            List<LiveData<CycleSummary>> summaryLiveDataList = new ArrayList<>();

            for (CycleData cycle : cycles) {
                LiveData<Map<LocalDate, Integer>> episodesLiveData =
                        episodeRepository.getEpisodesCountBetweenLive(cycle.getStartDate(), cycle.getEndDate());

                // Transform episode data into CycleSummary
                LiveData<CycleSummary> summaryLiveData = Transformations.map(episodesLiveData, episodeCounts ->
                        new CycleSummary(cycle, episodeCounts)
                );

                summaryLiveDataList.add(summaryLiveData);
            }

            // Merge all LiveData into one list
            return LiveDataUtil.combineLatest(summaryLiveDataList);
        });
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
     * Sync data from the API to local database
     * @param userId User ID
     * @return LiveData containing success status
     */
    public LiveData<Boolean> syncFromApi(int userId) {
        MediatorLiveData<Boolean> result = new MediatorLiveData<>();
        result.setValue(false);
        syncError.setValue(null);
        
        // Start sync from cycle repository
        LiveData<Boolean> cycleSync = cycleRepository.syncCyclesFromApi(userId);
        result.addSource(cycleSync, success -> {
            result.removeSource(cycleSync);
            
            if (success) {
                syncSuccess.setValue(true);
                result.setValue(true);
            } else {
                String error = syncError.getValue();
                if (error == null || error.isEmpty()) {
                    syncError.setValue("Failed to sync data from server");
                }
                result.setValue(false);
            }
        });
        
        return result;
    }
    
    /**
     * Sync data from local database to the API
     * @param userId User ID
     * @return LiveData containing success status
     */
    public LiveData<Boolean> syncToApi(int userId) {
        MediatorLiveData<Boolean> result = new MediatorLiveData<>();
        result.setValue(false);
        syncError.setValue(null);
        
        // Start sync to cycle repository
        LiveData<Boolean> cycleSync = cycleRepository.syncCyclesToApi(userId);
        result.addSource(cycleSync, success -> {
            result.removeSource(cycleSync);
            
            if (success) {
                syncSuccess.setValue(true);
                result.setValue(true);
            } else {
                String error = syncError.getValue();
                if (error == null || error.isEmpty()) {
                    syncError.setValue("Failed to sync data to server");
                }
                result.setValue(false);
            }
        });
        
        return result;
    }
}
