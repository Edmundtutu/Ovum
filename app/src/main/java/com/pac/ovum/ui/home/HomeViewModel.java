package com.pac.ovum.ui.home;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.pac.ovum.data.models.Episode;
import com.pac.ovum.data.models.Event;
import com.pac.ovum.data.repositories.EpisodeRepository;
import com.pac.ovum.data.repositories.EventRepository;
import com.pac.ovum.utils.data.calendarutils.DateUtils;
import com.pac.ovum.utils.data.calendarutils.DayInfo;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<LocalDate> selectedDate = new MutableLiveData<>();
    private final MutableLiveData<DayInfo> currentDate = new MutableLiveData<>();

    private final LiveData<List<Event>> eventsForSelectedDate;
    private final EpisodeRepository symptomsRepository;

    // TODO: Implement the Real EventsRepository
    @RequiresApi(api = Build.VERSION_CODES.O)
    public HomeViewModel(EventRepository repository, EpisodeRepository symptomsRepository) {
        eventsForSelectedDate = Transformations.switchMap(selectedDate, date -> {
            if (date != null) {
                return repository.getEventsForDate(date); // Returns LiveData<List<Event>> of events for the selected date
            } else {
                return new MutableLiveData<>(Collections.emptyList());
            }
        });
        LocalDate today = LocalDate.now();
        DayInfo dayInfo = new DayInfo(
                today.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()),
                new DateUtils().formatDate(today),
                Collections.emptyList() // TODO: Get the events for today by any means
        );
        currentDate.setValue(dayInfo);
        // handling the Symptoms
        this.symptomsRepository = symptomsRepository;

    }

    public void setSelectedDate(LocalDate date) {
        selectedDate.setValue(date);
    }

    public LiveData<DayInfo> getCurrentDate() {
        return currentDate;
    }

    public LiveData<List<String>> getEventsForSelectedDate() {
        return Transformations.map(eventsForSelectedDate, events -> {
            List<String> eventTitles = new ArrayList<>();
            for (Event event : events) {
                eventTitles.add(event.getDescription()); // TODO: Review this fetch may not be appropriate to return event description. Event Title may be more appropriate
            }
            return eventTitles;
        });
    }

    
    public LiveData<List<Episode>> getSymptoms(LocalDate date, int cycleId) {
        // Fetch all episodes for the current cycle
        LiveData<List<Episode>> episodesForCurrentCycle = symptomsRepository.getEpisodesByCycleId(cycleId);
    
        // Use Transformations.map to filter episodes for the specified date
        return Transformations.map(episodesForCurrentCycle, episodes -> {
            List<Episode> symptomsForDate = new ArrayList<>();
            for (Episode episode : episodes) {
                // first check if the episode's date is not null if null just skip it
                if (episode.getDate() == null) {
                    continue;
                }else{
                    if(episode.getDate().equals(date)){
                        symptomsForDate.add(episode);
                    }
                }
            }
            return symptomsForDate;
        });
    }
    


}