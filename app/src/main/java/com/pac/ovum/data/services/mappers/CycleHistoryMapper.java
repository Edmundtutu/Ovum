package com.pac.ovum.data.services.mappers;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pac.ovum.data.models.CycleData;
import com.pac.ovum.data.models.Episode;
import com.pac.ovum.data.services.helpers.CycleHistory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mapper class to convert between API models and local database models
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class CycleHistoryMapper {
    private static final Gson gson = new Gson();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Convert CycleData to CycleHistory
     * @param cycleData Local cycle data
     * @param episodes List of episodes (symptoms) for this cycle
     * @return CycleHistory API model
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static CycleHistory toCycleHistory(CycleData cycleData, List<Episode> episodes) {
        if (cycleData == null) {
            return null;
        }
        
        // Format the month string in the expected API format
        String monthStr = cycleData.getStartDate().atStartOfDay().format(formatter);
        
        // Extract symptom types from episodes
        List<String> symptomTypes = new ArrayList<>();
        if (episodes != null) {
            for (Episode episode : episodes) {
                if (!symptomTypes.contains(episode.getSymptomType())) {
                    symptomTypes.add(episode.getSymptomType());
                }
            }
        }
        
        // Convert symptom types to JSON string
        String symptomsJson = gson.toJson(symptomTypes);
        
        return new CycleHistory(
                monthStr,
                cycleData.getCycleLength(),
                cycleData.getPeriodLength(),
                symptomsJson
        );
    }
    
    /**
     * Convert CycleHistory to CycleData
     * @param cycleHistory API cycle history
     * @param userId User ID for the cycle data
     * @return CycleData local model
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static CycleData toCycleData(CycleHistory cycleHistory, int userId) {
        if (cycleHistory == null) {
            return null;
        }
        
        // Parse the month string to LocalDateTime
        LocalDateTime startDateTime = LocalDateTime.parse(cycleHistory.getMonth(), formatter);
        LocalDate startDate = startDateTime.toLocalDate();
        
        // Calculate end date based on cycle length
        LocalDate endDate = startDate.plusDays(cycleHistory.getCycleLength() - 1);
        
        // Create and populate CycleData object
        CycleData cycleData = new CycleData();
        cycleData.setUserId(userId);
        cycleData.setStartDate(startDate);
        cycleData.setEndDate(endDate);
        cycleData.setCycleLength(cycleHistory.getCycleLength());
        cycleData.setPeriodLength(cycleHistory.getPeriodLength());
        
        // Set fertile and ovulation dates based on cycle length
        // These calculations are simplified; in reality, they should use more complex logic
        int cycleLength = cycleHistory.getCycleLength();
        LocalDate ovulationDate = startDate.plusDays(cycleLength - 14); // TODO: Typical ovulation is around 14 days before next period
        cycleData.setOvulationDate(ovulationDate);
        cycleData.setFertileStart(ovulationDate.minusDays(5));
        cycleData.setFertileEnd(ovulationDate.plusDays(1));
        
        // Set as not ongoing since this is historical data
        cycleData.setOngoing(false);
        
        return cycleData;
    }
    
    /**
     * Create episodes from cycle history
     * @param cycleHistory API cycle history
     * @param cycleId ID of the cycle these episodes belong to
     * @return List of episodes
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<Episode> toEpisodes(CycleHistory cycleHistory, int cycleId) {
        if (cycleHistory == null || cycleHistory.getSymptoms() == null) {
            return Collections.emptyList();
        }
        
        // Parse the symptoms JSON string
        List<String> symptomTypes = gson.fromJson(
                cycleHistory.getSymptoms(),
                new TypeToken<List<String>>(){}.getType()
        );
        
        if (symptomTypes == null || symptomTypes.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Parse the month string to LocalDateTime
        LocalDateTime startDateTime = LocalDateTime.parse(cycleHistory.getMonth(), formatter);
        LocalDate startDate = startDateTime.toLocalDate();
        
        // Create episodes for each symptom type, adding them on the first day of the cycle
        List<Episode> episodes = new ArrayList<>();
        for (String symptomType : symptomTypes) {
            Episode episode = new Episode();
            episode.setCycleId(cycleId);
            episode.setSymptomType(symptomType);
            episode.setDate(startDate);
            episode.setTime(LocalTime.NOON); // Default time
            episode.setIntensity(5); // Default medium intensity
            episodes.add(episode);
        }
        
        return episodes;
    }
    
    /**
     * Convert a list of CycleData and associated episodes to a list of CycleHistory objects
     * @param cycleDataList List of cycle data
     * @param episodesByCycleId Map of cycle ID to list of episodes
     * @return List of cycle history objects
     */
    public static List<CycleHistory> toCycleHistoryList(List<CycleData> cycleDataList, 
                                                      List<List<Episode>> episodesByCycleId) {
        if (cycleDataList == null || cycleDataList.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<CycleHistory> cycleHistoryList = new ArrayList<>();
        for (int i = 0; i < cycleDataList.size(); i++) {
            CycleData cycleData = cycleDataList.get(i);
            List<Episode> episodes = i < episodesByCycleId.size() ? episodesByCycleId.get(i) : Collections.emptyList();
            
            CycleHistory cycleHistory = toCycleHistory(cycleData, episodes);
            if (cycleHistory != null) {
                cycleHistoryList.add(cycleHistory);
            }
        }
        
        return cycleHistoryList;
    }
} 