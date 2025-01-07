package com.pac.ovum.data.repositories;

import androidx.lifecycle.LiveData;

import com.pac.ovum.data.dao.EpisodeDao;
import com.pac.ovum.data.models.Episode;
import com.pac.ovum.utils.AppExecutors;

import java.util.List;

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

    public void updateEpisode(Episode episode) {
        AppExecutors.getInstance().diskIO().execute(() -> episodeDao.updateEpisode(episode));
    }

    public void deleteEpisode(Episode episode) {
        AppExecutors.getInstance().diskIO().execute(() -> episodeDao.deleteEpisode(episode));
    }
}
