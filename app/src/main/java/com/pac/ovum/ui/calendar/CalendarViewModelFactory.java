package com.pac.ovum.ui.calendar;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.pac.ovum.data.repositories.EventRepository;

public class CalendarViewModelFactory implements ViewModelProvider.Factory {
    private final EventRepository eventRepository;

    public CalendarViewModelFactory(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CalendarViewModel.class)) {
            return (T) new CalendarViewModel(eventRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
} 