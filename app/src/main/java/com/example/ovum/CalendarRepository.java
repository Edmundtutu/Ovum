package com.example.ovum;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ovum.models.Event;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalendarRepository {
    private SQLiteDatabase database;

    public CalendarRepository(SQLiteDatabase db) {
        this.database = db;
    }

    // Query the SQLite database for events on a specific date
    public List<Event> getEventsForDate(LocalDate date) {
        List<Event> eventList = new ArrayList<>();
        String query = "SELECT * FROM events WHERE date_occurred = ?";
        String[] args = {date.toString()}; // Assuming eventDate is stored as text

        Cursor cursor = database.rawQuery(query, args);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String eventDescription = cursor.getString(cursor.getColumnIndex("description"));
                Event event = new Event(date, eventDescription); // Assuming Event class has a constructor
                eventList.add(event);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return eventList; // Return a list of events for that date
    }
}
