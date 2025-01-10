package com.pac.ovum.ui.calendar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pac.ovum.data.models.Event;
import com.pac.ovum.data.repositories.EventRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarViewModel extends ViewModel {
    private final EventRepository eventRepository;
    private LiveData<List<Event>> events;

    public CalendarViewModel(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public LiveData<List<Event>> getEvents() {
        return events;
    }

    public LiveData<List<Event>> getEventsByCycleId(int cycleId) {
        events = eventRepository.getEventsByCycleId(cycleId);
        return events;
    }

    public void insertEvent(Event event) {
        eventRepository.insertEvent(event);
    }

    public void updateEvent(Event event) {
        eventRepository.updateEvent(event);
    }

    public void deleteEvent(Event event) {
        eventRepository.deleteEvent(event);
    }

    public List<Event> getEventsForDate(LocalDate date) {
        List<Event> currentEvents = events.getValue();
        if (currentEvents != null) {
            return currentEvents.stream()
                    .filter(event -> event.getEventDate().equals(date))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}