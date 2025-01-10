package com.pac.ovum.ui.cycles;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.pac.ovum.data.models.CycleData;
import com.pac.ovum.data.models.CycleSummary;
import com.pac.ovum.data.repositories.CycleRepository;

import java.util.ArrayList;
import java.util.List;

public class CyclesViewModel extends ViewModel {
    private final CycleRepository cycleRepository;

    public CyclesViewModel(CycleRepository cycleRepository) {
        this.cycleRepository = cycleRepository;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<List<CycleSummary>> getCycleSummaries(int userId) {
        // Fetch CycleData from the repository
        LiveData<List<CycleData>> cyclesLiveData = cycleRepository.getCyclesByUserId(userId);

        // Map CycleData to CycleSummary using Transformations.map
        return Transformations.map(cyclesLiveData, cycles -> {
            List<CycleSummary> summaries = new ArrayList<>();
            for (CycleData cycle : cycles) {
                summaries.add(new CycleSummary(cycle));
            }
            return summaries;
        });
    }
}
