package com.pac.ovum.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.pac.ovum.data.models.Event;

import java.time.LocalDate;
import java.util.List;

@Dao
public interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertEvent(Event event);

    @Query("SELECT * FROM Event WHERE cycleId = :cycleId")
    LiveData<List<Event>> getEventsByCycleId(int cycleId);

    @Query("SELECT * FROM Event WHERE cycleId = :cycleId")
    List<Event> getEventsByCycleIdSync(int cycleId);

    @Query("SELECT * FROM Event WHERE eventId = :eventId")
    LiveData<Event> getEventById(int eventId);

    @Query("SELECT * FROM Event WHERE eventId = :eventId")
    Event getEventByIdSync(int eventId);

    @Query("SELECT * FROM Event WHERE eventType = :eventType AND cycleId = :cycleId")
    LiveData<List<Event>> getEventsByTypeAndCycle(String eventType, int cycleId);

    @Query("SELECT * FROM Event WHERE eventType = :eventType AND cycleId = :cycleId")
    List<Event> getEventsByTypeAndCycleSync(String eventType, int cycleId);

    @Query("SELECT * FROM Event WHERE eventDate = :date")
    LiveData<List<Event>> getEventsForDate(LocalDate date);

    @Query("SELECT * FROM Event WHERE eventDate = :date")
    List<Event> getEventsForDateSync(LocalDate date);

    @Query("SELECT * FROM Event WHERE eventDate BETWEEN :startDate AND :endDate")
    LiveData<List<Event>> getEventsForDateRange(LocalDate startDate, LocalDate endDate);

    @Query("SELECT * FROM Event WHERE eventDate BETWEEN :startDate AND :endDate")
    List<Event> getEventsForDateRangeSync(LocalDate startDate, LocalDate endDate);

    @Query("SELECT * FROM Event WHERE eventDate >= :startDate")
    List<Event> getEventsFromDateSync(LocalDate startDate);

    @Query("SELECT * FROM Event")
    List<Event> getAllEventsSync();

    @Update
    void updateEvent(Event event);

    @Delete
    void deleteEvent(Event event);
}
