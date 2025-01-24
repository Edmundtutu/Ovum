package com.pac.ovum.data.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.pac.ovum.data.dao.EpisodeDao;
import com.pac.ovum.data.models.Episode;
import com.pac.ovum.utils.AppExecutors;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EpisodeRepository {
    private final EpisodeDao episodeDao;

    public EpisodeRepository(EpisodeDao episodeDao) {
        this.episodeDao = episodeDao;
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
}
