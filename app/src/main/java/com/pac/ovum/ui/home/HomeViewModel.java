package com.pac.ovum.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.pac.ovum.data.repositories.SimulatedEventsRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<LocalDate> selectedDate = new MutableLiveData<>();
    private final LiveData<List<String>> eventsForSelectedDate;

    // TODO: Implement the Real EventsRepository
    public HomeViewModel(SimulatedEventsRepository repository) {
        eventsForSelectedDate = Transformations.switchMap(selectedDate, date -> {
            if (date != null) {
                return repository.getEventsForDate(date); // Returns LiveData<List<String>>
            } else {
                return new MutableLiveData<>(Collections.emptyList());
            }
        });
    }

    public void setSelectedDate(LocalDate date) {
        selectedDate.setValue(date);
    }

    public LiveData<List<String>> getEventsForSelectedDate() {
        return eventsForSelectedDate;
    }

}