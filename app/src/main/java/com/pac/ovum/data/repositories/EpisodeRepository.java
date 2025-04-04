package com.pac.ovum.data.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.pac.ovum.data.dao.EpisodeDao;
import com.pac.ovum.data.models.Episode;
import com.pac.ovum.data.services.CycleHistoryService;
import com.pac.ovum.utils.AppExecutors;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EpisodeRepository {
    private final EpisodeDao episodeDao;
    private final CycleHistoryService cycleHistoryService;
    
    // State variables for tracking sync status
    private final MutableLiveData<Boolean> isSyncing = new MutableLiveData<>(false);
    private final MutableLiveData<String> syncError = new MutableLiveData<>();

    public EpisodeRepository(EpisodeDao episodeDao) {
        this.episodeDao = episodeDao;
        this.cycleHistoryService = CycleHistoryService.getInstance();
    }

    public void insertEpisode(Episode episode) {
        AppExecutors.getInstance().diskIO().execute(() -> episodeDao.insertEpisode(episode));
    }

    public LiveData<List<Episode>> getEpisodesByCycleId(int cycleId) {
        return episodeDao.getEpisodesByCycleId(cycleId);
    }

    public LiveData<Episode> getEpisodeById(int episodeId) {
        return episodeDao.getEpisodeById(episodeId);
    }

    public LiveData<List<Episode>> getEpisodesBySymptomTypeAndCycle(String symptomType, int cycleId) {
        return episodeDao.getEpisodesBySymptomTypeAndCycle(symptomType, cycleId);
    }

    public LiveData<Map<LocalDate, Integer>> getEpisodesCountBetweenLive(LocalDate start, LocalDate end) {
        return Transformations.map(episodeDao.getEpisodesCountBetweenLive(start, end), episodeCounts -> {
            Map<LocalDate, Integer> countMap = new HashMap<>();
            for (EpisodeDao.EpisodeCount count : episodeCounts) {
                countMap.put(count.episodeDate, count.count);
            }
            return countMap;
        });
    }

    public void updateEpisode(Episode episode) {
        AppExecutors.getInstance().diskIO().execute(() -> episodeDao.updateEpisode(episode));
    }

    public void deleteEpisode(Episode episode) {
        AppExecutors.getInstance().diskIO().execute(() -> episodeDao.deleteEpisode(episode));
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
     * Insert or update a batch of episodes, typically used during sync
     * @param episodes List of episodes to insert or update
     * @return Boolean indicating success
     */
    public LiveData<Boolean> insertOrUpdateEpisodes(List<Episode> episodes) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        
        if (episodes == null || episodes.isEmpty()) {
            result.setValue(false);
            return result;
        }
        
        AppExecutors.getInstance().diskIO().execute(() -> {
            try {
                for (Episode episode : episodes) {
                    episodeDao.insertEpisode(episode);
                }
                AppExecutors.getInstance().mainThread().execute(() -> result.setValue(true));
            } catch (Exception e) {
                AppExecutors.getInstance().mainThread().execute(() -> result.setValue(false));
            }
        });
        
        return result;
    }
    
    /**
     * Get episodes for a list of cycle IDs, for batch operations like syncing
     * @param cycleIds List of cycle IDs
     * @return Map of cycle ID to list of episodes
     */
    public LiveData<Map<Integer, List<Episode>>> getEpisodesForCycles(List<Integer> cycleIds) {
        MutableLiveData<Map<Integer, List<Episode>>> result = new MutableLiveData<>();
        
        if (cycleIds == null || cycleIds.isEmpty()) {
            result.setValue(new HashMap<>());
            return result;
        }
        
        AppExecutors.getInstance().diskIO().execute(() -> {
            try {
                Map<Integer, List<Episode>> episodesMap = new HashMap<>();
                for (Integer cycleId : cycleIds) {
                    List<Episode> episodes = episodeDao.getEpisodesByCycleIdSync(cycleId);
                    episodesMap.put(cycleId, episodes);
                }
                AppExecutors.getInstance().mainThread().execute(() -> result.setValue(episodesMap));
            } catch (Exception e) {
                AppExecutors.getInstance().mainThread().execute(() -> result.setValue(new HashMap<>()));
            }
        });
        
        return result;
    }
    
    /**
     * Get all episodes between two dates synchronously (for background operations)
     * @param start Start date
     * @param end End date
     * @return Map of date to episode count
     */
    public Map<LocalDate, Integer> getEpisodesCountBetweenSync(LocalDate start, LocalDate end) {
        Map<LocalDate, Integer> countMap = new HashMap<>();
        try {
            List<EpisodeDao.EpisodeCount> episodeCounts = episodeDao.getEpisodesCountBetweenSync(start, end);
            for (EpisodeDao.EpisodeCount count : episodeCounts) {
                countMap.put(count.episodeDate, count.count);
            }
        } catch (Exception e) {
            // Error handling, return empty map
        }
        return countMap;
    }
}
