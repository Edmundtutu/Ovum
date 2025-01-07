package com.pac.ovum.data.repositories;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class SimulatedEventsRepository {
    public LiveData<List<String>> getEventsForDate(LocalDate date) {
        MutableLiveData<List<String>> events = new MutableLiveData<>();
        // Simulate fetching data from database or API
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            List<String> mockEvents = Arrays.asList("Event 1", "Event 2", "Event 3");
            events.setValue(mockEvents);
        }, 500); // Simulated delay
        return events;
    }
}
