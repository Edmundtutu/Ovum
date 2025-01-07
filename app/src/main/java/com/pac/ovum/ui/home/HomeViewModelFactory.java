package com.pac.ovum.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.pac.ovum.data.repositories.SimulatedEventsRepository;

public class HomeViewModelFactory implements ViewModelProvider.Factory {
    private final SimulatedEventsRepository repository;

    public HomeViewModelFactory(SimulatedEventsRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}