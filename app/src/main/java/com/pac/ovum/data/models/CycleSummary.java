package com.pac.ovum.data.models;

import android.os.Build;
import androidx.annotation.RequiresApi;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CycleSummary extends CycleData {
    private String title;
    private int severityScore;
    private int episodesCount;
    private int remainingDays;
    private List<Integer> dailySeverityScores;

    private enum Rating {
        FIVE(5f), FOUR(4f), THREE(3f), TWO(2f);

        private final float value;

        Rating(float value) {
            this.value = value;
        }

        public float getValue() {
            return value;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public CycleSummary(CycleData cycleData, Map<LocalDate, Integer> episodeCounts) {
        // Copy CycleData fields
        this.setCycleId(cycleData.getCycleId());
        this.setUserId(cycleData.getUserId());
        this.setStartDate(cycleData.getStartDate());
        this.setEndDate(cycleData.getEndDate());
        this.setCycleLength(cycleData.getCycleLength());
        this.setPeriodLength(cycleData.getPeriodLength());
        this.setOvulationDate(cycleData.getOvulationDate());
        this.setFertileStart(cycleData.getFertileStart());
        this.setFertileEnd(cycleData.getFertileEnd());
        this.setOngoing(cycleData.isOngoing());

        // Compute fields based on episode data
        this.dailySeverityScores = computeSeverityScores(episodeCounts);
        this.episodesCount = computeTotalEpisodes();
        this.severityScore = computeSeverity();
        this.title = computeTitle();
        this.remainingDays = computeRemainingDays();
    }

    private String computeTitle() {
        return "From " + getStartDate().toString() + " to " +
                (getEndDate() != null ? getEndDate().toString() : "Ongoing");
    }

    private List<Integer> computeSeverityScores(Map<LocalDate, Integer> episodeCounts) {
        List<Integer> severityList = new ArrayList<>();
        LocalDate start = getStartDate();
        LocalDate end = getEndDate() != null ? getEndDate() : LocalDate.now();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            severityList.add(episodeCounts.getOrDefault(date, 0)); // Default to 0 if no data
        }
        return severityList;
    }

    private int computeTotalEpisodes() {
        return dailySeverityScores.stream().mapToInt(Integer::intValue).sum();
    }

    private int computeSeverity() {
        return episodesCount > 10 ? 5 : episodesCount > 5 ? 3 : 1;
    }

    private Rating computeRating() {
        int averageScore = (getSeverityScore() + getEpisodesCount()) / 2 % getSeverityScore(); // Preserved original calculation

        if (averageScore >= 5) {
            return Rating.FIVE;
        } else if (averageScore >= 4) {
            return Rating.FOUR;
        } else if (averageScore >= 3) {
            return Rating.THREE;
        } else {
            return Rating.TWO;
        }
    }

    private int computeRemainingDays() {
        if (isOngoing() && getEndDate() != null) {
            return (int) ChronoUnit.DAYS.between(LocalDate.now(), getEndDate());
        }
        return 0;
    }

    // Getters
    public List<Integer> getGraphData() {
        return dailySeverityScores;
    }

    public String getTitle() {
        return title;
    }

    public String getSeverity() {
        return severityScore > 5 ? "Severe" : "Normal";
    }

    public int getEpisodesCount() {
        return episodesCount;
    }

    public float getRating() {
        return computeRating().getValue();
    }

    public String getCycleStatus() {
        return remainingDays > 0 ? "Ongoing... " + remainingDays + " days left" : "Completed";
    }

    private int getSeverityScore() {
        return severityScore;
    }
}