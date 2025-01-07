package com.pac.ovum.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.pac.ovum.data.models.Event;

import java.util.List;

@Dao
public interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEvent(Event event);

    @Query("SELECT * FROM Event WHERE cycleId = :cycleId")
    LiveData<List<Event>> getEventsByCycleId(int cycleId);

    @Query("SELECT * FROM Event WHERE eventId = :eventId")
    LiveData<Event> getEventById(int eventId);

    @Query("SELECT * FROM Event WHERE eventType = :eventType AND cycleId = :cycleId")
    LiveData<List<Event>> getEventsByTypeAndCycle(String eventType, int cycleId);

    @Update
    void updateEvent(Event event);

    @Delete
    void deleteEvent(Event event);
}
