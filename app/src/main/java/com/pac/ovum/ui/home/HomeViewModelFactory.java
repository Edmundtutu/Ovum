package com.pac.ovum.ui.home;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.pac.ovum.data.repositories.CycleRepository;
import com.pac.ovum.data.repositories.EpisodeRepository;
import com.pac.ovum.data.repositories.EventRepository;

public class HomeViewModelFactory implements ViewModelProvider.Factory {
    private final EventRepository eventsRepository;
    private final EpisodeRepository symptomsRepository;
    private final CycleRepository cycleRepository;

    public HomeViewModelFactory(EventRepository eventsRepository, EpisodeRepository symptomsRepository, CycleRepository cycleRepository) {
        this.eventsRepository = eventsRepository;
        this.symptomsRepository = symptomsRepository;
        this.cycleRepository = cycleRepository;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(eventsRepository, symptomsRepository, cycleRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}