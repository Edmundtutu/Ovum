package com.pac.ovum.data.repositories;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pac.ovum.data.models.Episode;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MockEpisodesRepository extends EpisodeRepository {
    private final MutableLiveData<List<Episode>> episodes = new MutableLiveData<>();
    public MockEpisodesRepository() {
        super(null);
        loadData();
    }

    private void loadData() {
        // Simulate data loading
        // Use postValue to ensure it's on the main thread
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            episodes.postValue(generateMockEpisodes());
        }
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<Episode> generateMockEpisodes() {
        List<Episode> mockEpisodes = new ArrayList<>();
        
        // Today's episodes
        mockEpisodes.add(createMockEpisode(1, "Pain", LocalDate.now(), LocalTime.of(8, 0), "Severe cramps", 8));
        mockEpisodes.add(createMockEpisode(2, "Mood Change", LocalDate.now(), LocalTime.of(10, 30), "Feeling irritable", 6));
        mockEpisodes.add(createMockEpisode(12, "Fatigue", LocalDate.now(), LocalTime.of(12, 0), "Feeling tired", 4));
        mockEpisodes.add(createMockEpisode(13, "Mood Change", LocalDate.now(), LocalTime.of(15, 30), "Feeling anxious", 7));
        
        // Past episodes
        mockEpisodes.add(createMockEpisode(3, "Pain", LocalDate.now().minusDays(1), LocalTime.of(9, 15), "Mild pain", 3));
        mockEpisodes.add(createMockEpisode(4, "Mood Change", LocalDate.now().minusDays(2), LocalTime.of(11, 0), "Feeling anxious", 5));
        mockEpisodes.add(createMockEpisode(5, "Fatigue", LocalDate.now().minusDays(2), LocalTime.of(14, 30), "Very tired", 7));
        mockEpisodes.add(createMockEpisode(6, "Cramps", LocalDate.now().minusDays(3), LocalTime.of(16, 45), "Moderate cramps", 6));
        mockEpisodes.add(createMockEpisode(7, "Headache", LocalDate.now().minusDays(3), LocalTime.of(18, 20), "Severe headache", 8));
        mockEpisodes.add(createMockEpisode(8, "Nausea", LocalDate.now().minusDays(4), LocalTime.of(7, 30), "Morning sickness", 5));
        
        // Future episodes (scheduled)
        mockEpisodes.add(createMockEpisode(9, "Check-up", LocalDate.now().plusDays(1), LocalTime.of(10, 0), "Gynecologist appointment", 0));
        mockEpisodes.add(createMockEpisode(10, "Medication", LocalDate.now().plusDays(2), LocalTime.of(9, 0), "Take prescribed medicine", 0));
        mockEpisodes.add(createMockEpisode(11, "Follow-up", LocalDate.now().plusDays(3), LocalTime.of(14, 30), "Follow-up visit", 0));

        return mockEpisodes;
    }

    private static Episode createMockEpisode(int episodeId, String symptomType, LocalDate date, LocalTime time, String notes, int intensity) {
        Episode episode = new Episode();
        episode.setEpisodeId(episodeId);
        episode.setCycleId(1); // Assuming the cycleId is 1
        episode.setSymptomType(symptomType);
        episode.setDate(date);
        episode.setTime(time);
        episode.setNotes(notes);
        episode.setIntensity(intensity);
        return episode;
    }

    @Override
    public LiveData<List<Episode>> getEpisodesByCycleId(int cycleId) {
        return episodes;
    }
}
