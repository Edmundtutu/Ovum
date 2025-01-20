package com.pac.ovum.data.repositories;

import androidx.lifecycle.LiveData;

import com.pac.ovum.data.dao.EventDao;
import com.pac.ovum.data.models.Event;
import com.pac.ovum.utils.AppExecutors;

import java.time.LocalDate;
import java.util.List;

public class EventRepository {
    private final EventDao eventDao;

    public EventRepository(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    public void insertEvent(Event event) {
        AppExecutors.getInstance().diskIO().execute(() -> eventDao.insertEvent(event));
    }

    public LiveData<List<Event>> getEventsByCycleId(int cycleId) {
        return eventDao.getEventsByCycleId(cycleId);
    }

    public LiveData<Event> getEventById(int eventId) {
        return eventDao.getEventById(eventId);
    }

    public LiveData<List<Event>> getEventsByTypeAndCycle(String eventType, int cycleId) {
        return eventDao.getEventsByTypeAndCycle(eventType, cycleId);
    }

    public void updateEvent(Event event) {
        AppExecutors.getInstance().diskIO().execute(() -> eventDao.updateEvent(event));
    }

    public void deleteEvent(Event event) {
        AppExecutors.getInstance().diskIO().execute(() -> eventDao.deleteEvent(event));
    }

    public LiveData<List<Event>> getEventsForDate(LocalDate date) {
        return eventDao.getEventsForDate(date);
    }
}
