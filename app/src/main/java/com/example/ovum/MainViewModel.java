package com.example.ovum;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ovum.models.Event;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//
//@RequiresApi(api = Build.VERSION_CODES.O)
//public class MainViewModel extends ViewModel {
//    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("LLL d, yyyy");
//    private final MutableLiveData<DayInfo> currentDate = new MutableLiveData<>();
//
//    // new live data for the events
//    private CalendarRepository repository;
//    private final MutableLiveData<Map<LocalDate, String>> eventsLiveData;
//    private final  List events_come_with_today = new ArrayList<Event>();
//
//    public MainViewModel() {
//        LocalDate today = LocalDate.now();
//        DayInfo dayInfo = new DayInfo(
//                today.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()),
//                dateFormatter.format(today),
//                events_come_with_today
//        );
//        currentDate.setValue(dayInfo);
//
//        this.repository = repository;
//        eventsLiveData = new MutableLiveData<>();
//        eventsLiveData.setValue(new HashMap<>());
//    }
//
//    public LiveData<DayInfo> getCurrentDate() {
//        return currentDate;
//    }
//
//
//    public void setCurrentDate(LocalDate date) {
//        DayInfo dayInfo = new DayInfo(
//                date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()),
//                dateFormatter.format(date),
//                events_come_with_today);
//        currentDate.setValue(dayInfo);
//    }
//
//    private Instant getCurrentInstant() {
//        return Instant.now();
//    }
//
//    // Fetch events from SQLite and update LiveData
//    public void fetchEventsForDate(LocalDate date) {
//        new Thread(() -> { // Execute SQLite query in a background thread
//            List<Event> events = repository.getEventsForDate(date);
//            Map<LocalDate, String> currentMap = eventsLiveData.getValue();
//            if (currentMap != null) {
//                for (Event event : events) {
//                    currentMap.put(date, event.getEventDescription());
//                }
//                // Post the updated map back to the main thread
//                eventsLiveData.postValue(currentMap);
//            }
//        }).start();
//    }
//
//    // Getter for LiveData
//    public LiveData<Map<LocalDate, String>> getEventsLiveData() {
//        return eventsLiveData;
//    }
@RequiresApi(api = Build.VERSION_CODES.O)
public class MainViewModel extends ViewModel {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("LLL d, yyyy");

    private final MutableLiveData<DayInfo> currentDate = new MutableLiveData<>();
    private final MutableLiveData<Map<LocalDate, String>> eventsLiveData = new MutableLiveData<>(new HashMap<>());

    private final List<Event> eventsComeWithToday = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final CalendarRepository repository;  // Repository initialized in the constructor

    // Constructor: Requires a CalendarRepository instance with SQLiteDatabase injected
    public MainViewModel(CalendarRepository repository) {
        this.repository = repository;

        // Initialize with today's date
        LocalDate today = LocalDate.now();
        DayInfo dayInfo = new DayInfo(
                today.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()),
                dateFormatter.format(today),
                eventsComeWithToday
        );
        currentDate.setValue(dayInfo);
    }

    // Getter for currentDate LiveData
    public LiveData<DayInfo> getCurrentDate() {
        return currentDate;
    }

    // Setter for the selected date
    public void setCurrentDate(LocalDate date) {
        DayInfo dayInfo = new DayInfo(
                date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()),
                dateFormatter.format(date),
                eventsComeWithToday
        );
        currentDate.setValue(dayInfo);
    }

    // Fetch events for a specific date from SQLite in a background thread
    public void fetchEventsForDate(LocalDate date) {
        executor.execute(() -> {
            List<Event> events = repository.getEventsForDate(date); // Query events from repository

            if (events != null && !events.isEmpty()) {
                Map<LocalDate, String> currentMap = eventsLiveData.getValue();
                if (currentMap != null) {
                    for (Event event : events) {
                        currentMap.put(date, event.getEventDescription());
                    }
                    eventsLiveData.postValue(currentMap); // Update LiveData on the main thread
                }
            }
        });
    }

    // Getter for eventsLiveData
    public LiveData<Map<LocalDate, String>> getEventsLiveData() {
        return eventsLiveData;
    }

    // Clean up executor when ViewModel is cleared
    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
