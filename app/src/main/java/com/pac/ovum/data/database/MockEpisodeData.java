package com.pac.ovum.data.database;

import android.os.Build;
import androidx.annotation.RequiresApi;
import com.pac.ovum.data.models.Episode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MockEpisodeData {
    private final List<Episode> mockEpisodes;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public MockEpisodeData() {
        this.mockEpisodes = new ArrayList<>();
        generateMockEpisodes();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void generateMockEpisodes() {
        mockEpisodes.add(createMockEpisode(1, "Pain", LocalDate.now().minusDays(1), LocalTime.of(8, 0), "Mild pain", 3));
        mockEpisodes.add(createMockEpisode(2, "Mood Change", LocalDate.now().minusDays(2), LocalTime.of(10, 0), "Feeling anxious", 5));
        mockEpisodes.add(createMockEpisode(3, "Fatigue", LocalDate.now().minusDays(3), LocalTime.of(14, 0), "Very tired", 7));
        mockEpisodes.add(createMockEpisode(4, "Cramps", LocalDate.now().minusDays(4), LocalTime.of(16, 0), "Severe cramps", 8));
        mockEpisodes.add(createMockEpisode(5, "Headache", LocalDate.now().minusDays(5), LocalTime.of(18, 0), "Mild headache", 4));
        mockEpisodes.add(createMockEpisode(6, "Nausea", LocalDate.now().minusDays(6), LocalTime.of(20, 0), "Feeling nauseous", 6));
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

    // Method to get all mock episodes
    public List<Episode> getMockEpisodes() {
        return new ArrayList<>(mockEpisodes); // Return a copy to prevent external modification
    }

    // Method to get episodes for today
    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<Episode> getTodayEpisodes() {
        List<Episode> todayEpisodes = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Episode episode : mockEpisodes) {
            if (episode.getDate().equals(today)) {
                todayEpisodes.add(episode);
            }
        }
        return todayEpisodes;
    }

    // Method to get episodes for a specific date
    public List<Episode> getEpisodesForDate(LocalDate date) {
        List<Episode> dateEpisodes = new ArrayList<>();

        for (Episode episode : mockEpisodes) {
            if (episode.getDate().equals(date)) {
                dateEpisodes.add(episode);
            }
        }
        return dateEpisodes;
    }
}