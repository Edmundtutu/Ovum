package com.pac.ovum.data.models;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.pac.ovum.utils.data.calendarutils.DateUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class CycleSummary extends CycleData {
    private String title;             // Title of the cycle
    private int severityScore;        // Computed severity score
    private int episodesCount;        // Number of episodes (if applicable)
    private int remainingDays;        // Days left in the cycle (if ongoing)
    private List<Episode> episodes;

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
    public CycleSummary(CycleData cycleData, LiveData<List<Episode>> episodes) {
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
        this.episodes = (List<Episode>) episodes;
    }

    // Custom methods for computed fields
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String computeTitle() {
        // instantiate a new date utils class to convert the date to speech format for better UX
        DateUtils dateFormatter = new DateUtils();
        // Example: Title based on cycle dates
        return "From " + dateFormatter.formatDateToSpeech(String.valueOf(getStartDate())) + " to " + (getEndDate() != null ? dateFormatter.formatDateToSpeech(String.valueOf(getEndDate())) : "Ongoing");
    }

    private int computeSeverity() {
        // Example: Severity based on period length (arbitrary logic) TODO: Must use the practical logic given the Individual patient
        return getPeriodLength() > 5 ? 3 : 1;
    }


    private Rating computeRating() {
        int averageScore = (getSeverityScore() + getEpisodesCount()) / 2 % getSeverityScore(); // TODO: This calculation is arbitrary, may change

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
        // The number of symptoms logged from the Start of the cycle to the end of the cycle.
        // This can be got by looking up from the database for the resultSet size of the episodes from getCycleStartDate() to getCycleEndDate()
        // alternatively since the cycleId is the foreign key in the Episodes Entity: just return the number of episodes with the this cycleId
        return !episodes.isEmpty() ? episodes.size() : 0 ; // return 0 if the episodes cycle is empty
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
