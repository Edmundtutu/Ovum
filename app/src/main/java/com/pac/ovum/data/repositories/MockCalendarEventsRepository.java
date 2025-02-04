package com.pac.ovum.data.repositories;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pac.ovum.data.models.Event;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MockCalendarEventsRepository extends EventRepository{
    private MutableLiveData<List<Event>> events = new MutableLiveData<>();

    public MockCalendarEventsRepository() {
        super(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            loadData();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadData() {
        List<Event> mockCalendarEvents = new ArrayList<>();

        mockCalendarEvents.add(createMockEvent(1, LocalDate.now().plusDays(5), LocalTime.of(3,8), "Period Start", "Next Period Starts"));
        mockCalendarEvents.add(createMockEvent(2, LocalDate.now().plusDays(3), LocalTime.of(5,8), "Ovulation", "Next Ovulation Starts"));
        mockCalendarEvents.add(createMockEvent(3, LocalDate.now().plusDays(3), LocalTime.of(5,9), "Gyn Event", "Call Partner"));
        mockCalendarEvents.add(createMockEvent(4, LocalDate.now().plusDays(3), LocalTime.of(5,10), "Gyn Event", "Prepare Kit"));
        mockCalendarEvents.add(createMockEvent(5, LocalDate.now().plusDays(2), LocalTime.of(1,40), "Gyn Event", "Meet Doctor"));
        mockCalendarEvents.add(createMockEvent(6, LocalDate.now().plusDays(20), LocalTime.of(12,20), "Gyn Event", "Get pill"));
        mockCalendarEvents.add(createMockEvent(7, LocalDate.now().minusDays(5), LocalTime.of(13,12), "Gyn Event", "Call Doctor"));
        mockCalendarEvents.add(createMockEvent(8, LocalDate.now().plusDays(1), LocalTime.of(21,50), "Gyn Event", "Load a date"));
        mockCalendarEvents.add(createMockEvent(9, LocalDate.now().plusDays(1), LocalTime.of(21,51), "Gyn Event", "Meet Doctor"));
        mockCalendarEvents.add(createMockEvent(10, LocalDate.now().plusDays(1), LocalTime.of(21,52), "Gyn Event", "Get a pill"));
        mockCalendarEvents.add(createMockEvent(11, LocalDate.now().plusDays(1), LocalTime.of(21,53), "Gyn Event", "BMR Measure"));
        mockCalendarEvents.add(createMockEvent(12, LocalDate.now().plusDays(14), LocalTime.of(12,0), "Gyn Event", "Temperature Measure"));

        events.postValue(mockCalendarEvents);
    }
    private Event createMockEvent(int eventId, LocalDate date, LocalTime time, String eventType, String description){
        Event event = new Event();
        event.setCycleId(1); //  Place holder Given the current cycle is of id 1
        event.setEventId(eventId);
        event.setEventDate(date);
        event.setEventTime(time);
        event.setEventType(eventType);
        event.setDescription(description);
        return event;
    }

    @Override
    public LiveData<List<Event>> getEventsByCycleId(int cycleId) {
        return events;
    }

    @Override
    public LiveData<List<Event>> getEventsForDate(LocalDate date) {
        MutableLiveData<List<Event>> dateEvents = new MutableLiveData<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            List<Event> currentEvents = events.getValue();
            if (currentEvents != null) {
                List<Event> filtered = currentEvents.stream()
                        .filter(event -> event.getEventDate().equals(date))
                        .collect(Collectors.toList());
                dateEvents.setValue(filtered);
            }
        }
        return dateEvents;
    }

    @Override
    public LiveData<Event> getEventById(int eventId) {
        MutableLiveData<Event> event = new MutableLiveData<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            List<Event> currentEvents = events.getValue();
            if (currentEvents != null) {
                event.setValue(currentEvents.stream()
                        .filter(e -> e.getEventId() == eventId)
                        .findFirst()
                        .orElse(null));
            }
        }
        return event;
    }

    @Override
    public LiveData<List<Event>> getEventsByTypeAndCycle(String eventType, int cycleId) {
        MutableLiveData<List<Event>> filteredEvents = new MutableLiveData<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            List<Event> currentEvents = events.getValue();
            if (currentEvents != null) {
                List<Event> filtered = currentEvents.stream()
                        .filter(event -> event.getEventType().equals(eventType) 
                                && event.getCycleId() == cycleId)
                        .collect(Collectors.toList());
                filteredEvents.setValue(filtered);
            }
        }
        return filteredEvents;
    }

    @Override
    public void insertEvent(Event event) {
        List<Event> currentEvents = events.getValue();
        if (currentEvents != null) {
            currentEvents.add(event);
            events.postValue(currentEvents);
        }
    }

    @Override
    public void updateEvent(Event event) {
        List<Event> currentEvents = events.getValue();
        if (currentEvents != null) {
            for (int i = 0; i < currentEvents.size(); i++) {
                if (currentEvents.get(i).getEventId() == event.getEventId()) {
                    currentEvents.set(i, event);
                    break;
                }
            }
            events.postValue(currentEvents);
        }
    }

    @Override
    public void deleteEvent(Event event) {
        List<Event> currentEvents = events.getValue();
        if (currentEvents != null) {
            currentEvents.removeIf(e -> e.getEventId() == event.getEventId());
            events.postValue(currentEvents);
        }
    }
}
