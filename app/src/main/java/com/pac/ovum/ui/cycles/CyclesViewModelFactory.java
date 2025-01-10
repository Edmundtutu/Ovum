package com.pac.ovum.ui.cycles;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.pac.ovum.data.repositories.CycleRepository;

public class CyclesViewModelFactory implements ViewModelProvider.Factory {
    private final CycleRepository cycleRepository;

    public CyclesViewModelFactory(CycleRepository cycleRepository) {
        this.cycleRepository = cycleRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CyclesViewModel.class)) {
            return (T) new CyclesViewModel(cycleRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}