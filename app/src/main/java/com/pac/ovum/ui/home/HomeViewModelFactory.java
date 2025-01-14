package com.pac.ovum.ui.home;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.pac.ovum.data.repositories.EpisodeRepository;
import com.pac.ovum.data.repositories.SimulatedEventsRepository;

public class HomeViewModelFactory implements ViewModelProvider.Factory {
    private final SimulatedEventsRepository eventsRepository;
    private final EpisodeRepository symptomsRepository;

    public HomeViewModelFactory(SimulatedEventsRepository eventsRepository, EpisodeRepository symptomsRepository) {
        this.eventsRepository = eventsRepository;
        this.symptomsRepository = symptomsRepository;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(eventsRepository, symptomsRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}