package com.pac.ovum.ui.home;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.pac.ovum.data.models.CycleData;
import com.pac.ovum.data.models.Episode;
import com.pac.ovum.data.models.Event;
import com.pac.ovum.data.repositories.CycleRepository;
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
    private final CycleRepository cycleRepository;
    private final LocalDate today;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public HomeViewModel(EventRepository repository, EpisodeRepository symptomsRepository, CycleRepository cycleRepository) {
        eventsForSelectedDate = Transformations.switchMap(selectedDate, date -> {
            if (date != null) {
                return repository.getEventsForDate(date); // Returns LiveData<List<Event>> of events for the selected date
            } else {
                return new MutableLiveData<>(Collections.emptyList());
            }
        });
        today = LocalDate.now();
        DayInfo dayInfo = new DayInfo(
                today.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()),
                new DateUtils().formatDate(today),
                Collections.emptyList() // TODO: Get the events for today by any means
        );
        currentDate.setValue(dayInfo);
        // handling the Symptoms
        this.symptomsRepository = symptomsRepository;
        // handling the ongoing cycle data
        this.cycleRepository = cycleRepository;
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
                eventTitles.add(event.getDescription());
            }
            return eventTitles;
        });
    }


    public LiveData<List<Episode>> getSymptoms(LocalDate date, int userId) {
        MediatorLiveData<List<Episode>> result = new MediatorLiveData<>();

        LiveData<CycleData> ongoingCycleData = getOngoingCycleData(userId);

        result.addSource(ongoingCycleData, cycleData -> {
            if (cycleData != null) {
                int cycleId = cycleData.getCycleId();
                LiveData<List<Episode>> episodesForCurrentCycle = symptomsRepository.getEpisodesByCycleId(cycleId);

                result.addSource(episodesForCurrentCycle, episodes -> {
                    List<Episode> symptomsForDate = new ArrayList<>();
                    for (Episode episode : episodes) {
                        if (episode.getDate() != null && episode.getDate().equals(date)) {
                            symptomsForDate.add(episode);
                        }
                    }
                    result.setValue(symptomsForDate);
                });
            } else {
                Log.e("HomeViewModel", "No ongoing cycle data for user ID: " + userId);
                result.setValue(Collections.emptyList());
            }
        });

        return result;
    }


    public LiveData<CycleData> getOngoingCycleData(int userId) {
        LiveData<CycleData> liveData = cycleRepository.getOngoingCycleByUserId(userId);

        liveData.observeForever(cycle -> {
            if (cycle != null) {
                Log.d("HomeViewModel", "Cycle data found: " +
                        "ID: " + cycle.getCycleId() +
                        ", UserID: " + cycle.getUserId() +
                        ", StartDate: " + cycle.getStartDate() +
                        ", IsOngoing: " + cycle.isOngoing());
            } else {
                Log.e("HomeViewModel", "No ongoing cycle data found for user ID: " + userId);
            }
        });

        return liveData;
    }



    public String getDateToday() {
        // return the string value of today
        if (today != null) {
            return today.toString();
        }
        return LocalDate.now().toString();
    }
}