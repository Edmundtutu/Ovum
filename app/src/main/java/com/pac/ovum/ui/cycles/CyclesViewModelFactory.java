package com.pac.ovum.ui.cycles;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.pac.ovum.data.repositories.CycleRepository;
import com.pac.ovum.data.repositories.EpisodeRepository;

public class CyclesViewModelFactory implements ViewModelProvider.Factory {
    private final CycleRepository cycleRepository;
    private final EpisodeRepository episodeRepository;

    public CyclesViewModelFactory(CycleRepository cycleRepository, EpisodeRepository episodeRepository) {
        this.cycleRepository = cycleRepository;
        this.episodeRepository = episodeRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CyclesViewModel.class)) {
            return (T) new CyclesViewModel(cycleRepository, episodeRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}