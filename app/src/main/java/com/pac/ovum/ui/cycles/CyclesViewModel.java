package com.pac.ovum.ui.cycles;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
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

    public CyclesViewModel(CycleRepository cycleRepository, EpisodeRepository episodeRepository) {
        this.cycleRepository = cycleRepository;
        this.episodeRepository = episodeRepository;
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
}
