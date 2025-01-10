package com.pac.ovum.data.models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class CycleSummary extends CycleData {
    private String title;             // Title of the cycle
    private int severityScore;        // Computed severity score
    private int episodesCount;        // Number of episodes (if applicable)
    private int remainingDays;        // Days left in the cycle (if ongoing)

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

    // Constructor for adding summary-specific fields
    @RequiresApi(api = Build.VERSION_CODES.O)
    public CycleSummary(CycleData cycleData) {
        // Copy all CycleData fields
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

        // Additional computed fields
        this.title = computeTitle();
        this.severityScore = computeSeverity();
        this.episodesCount = computeEpisodes();
        this.remainingDays = computeRemainingDays();
    }

    // Custom methods for computed fields
    private String computeTitle() {
        // Example: Title based on cycle dates
        return "Cycle from " + getStartDate() + " to " + (getEndDate() != null ? getEndDate() : "Ongoing");
    }

    private int computeSeverity() {
        // Example: Severity based on period length (arbitrary logic) TODO: Must use the practical logic given the Individual patient
        return getPeriodLength() > 5 ? 3 : 1;
    }


    private Rating computeRating() {
        int averageScore = (getSeverityScore() + getEpisodesCount()) / 2; // TODO: This calculation is arbitrary, may change

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

    private int computeEpisodes() {
        // Placeholder for episodes computation (if applicable)
        return 0; // Replace with actual logic
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private int computeRemainingDays() {
        if (isOngoing() && getEndDate() != null) {
            return (int) ChronoUnit.DAYS.between(LocalDate.now(), getEndDate());
        }
        return 0;
    }

    // Getters for summary-specific fields
    public String getTitle() {
        return title;
    }

    private int getSeverityScore() {
        return severityScore;
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
}
