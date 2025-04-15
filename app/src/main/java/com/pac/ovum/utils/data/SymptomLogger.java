package com.pac.ovum.utils.data;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.pac.ovum.data.models.CycleData;
import com.pac.ovum.data.models.Episode;
import com.pac.ovum.data.repositories.CycleRepository;
import com.pac.ovum.data.repositories.EpisodeRepository;
import com.pac.ovum.ui.dialogs.LogSymptomsDialogFragment;
import com.pac.ovum.utils.AppExecutors;
import com.pac.ovum.utils.AppModule;
import com.pac.ovum.utils.SharedPrefManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

/**
 * Handles symptom logging operations with proper lifecycle awareness and threading.
 * Ensures LiveData observations happen on main thread while maintaining database
 * operations on background threads.
 */
public class SymptomLogger {
    private final Context context;
    private final EpisodeRepository episodeRepository;
    private final CycleRepository cycleRepository;

    /**
     * Constructs a SymptomLogger with required dependencies.
     *
     * @param context The application context for resource access
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public SymptomLogger(Context context) {
        this.context = context;
        this.episodeRepository = AppModule.provideEpisodeRepository(context);
        this.cycleRepository = AppModule.provideCycleRepository(context);
    }

    /**
     * Main entry point for logging symptoms.
     * Follows Android threading guidelines:
     * - Synchronous DB operations on diskIO thread
     * - LiveData observation on main thread
     * - Follow-up processing on background threads
     *
     * @param selectedSymptoms Set of user-selected symptoms
     * @param dateRecorded     The date associated with the symptoms
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void logSymptoms(Set<LogSymptomsDialogFragment.Symptom> selectedSymptoms, LocalDate dateRecorded) {
        final int userId = SharedPrefManager.getInstance(context).getUserId();
        Log.d("SymptomLogger", "Initiating symptom logging for date: " + dateRecorded);

        AppExecutors.getInstance().diskIO().execute(() -> {
            CycleData ongoingCycle = cycleRepository.getOngoingCycleByUserIdSync(userId);

            if (ongoingCycle != null) {
                handleExistingCycle(ongoingCycle, selectedSymptoms, dateRecorded);
            } else {
                handleMissingCycleFlow(userId, selectedSymptoms, dateRecorded);
            }
        });
    }

    /**
     * Processes symptoms when an ongoing cycle is found.
     *
     * @param cycle            The ongoing cycle data
     * @param symptoms         Set of symptoms to process
     * @param date             Recording date
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleExistingCycle(CycleData cycle, Set<LogSymptomsDialogFragment.Symptom> symptoms, LocalDate date) {
        Log.d("SymptomLogger", "Processing " + symptoms.size() + " symptoms for cycle " + cycle.getCycleId());
        processSymptoms(cycle.getCycleId(), symptoms, date);
    }

    /**
     * Handles missing cycle scenario with proper thread switching.
     * Observes LiveData on main thread and processes results on background.
     */
    private void handleMissingCycleFlow(int userId, Set<LogSymptomsDialogFragment.Symptom> symptoms, LocalDate date) {
        Log.w("SymptomLogger", "Initiating async cycle lookup for user " + userId);

        AppExecutors.getInstance().mainThread().execute(() -> {
            LiveData<CycleData> cycleLiveData = cycleRepository.getOngoingCycleByUserId(userId);
            Observer<CycleData> cycleObserver = new Observer<CycleData>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onChanged(CycleData cycleData) {
                    cycleLiveData.removeObserver(this);

                    if (cycleData != null) {
                        AppExecutors.getInstance().diskIO().execute(() -> {
                            processSymptoms(cycleData.getCycleId(), symptoms, date);
                        });
                    } else {
                        AppExecutors.getInstance().diskIO().execute(() -> {
                            handleMissingCycle(userId, symptoms, date);
                        });
                    }
                }
            };
            cycleLiveData.observeForever(cycleObserver);
        });
    }

    /**
     * Processes and stores symptoms in the database.
     * Must be called from background thread.
     *
     * @param cycleId          Target cycle ID
     * @param selectedSymptoms Symptoms to process
     * @param dateRecorded     Event date
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void processSymptoms(int cycleId, Set<LogSymptomsDialogFragment.Symptom> selectedSymptoms, LocalDate dateRecorded) {
        for (LogSymptomsDialogFragment.Symptom symptom : selectedSymptoms) {
            Episode episode = createEpisode(cycleId, dateRecorded, symptom);
            episodeRepository.insertEpisode(episode);
            Log.d("SymptomLogger", "Stored symptom: " + episode.getNotes());
        }
    }

    /**
     * Creates an Episode object from symptom data.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private Episode createEpisode(int cycleId, LocalDate date, LogSymptomsDialogFragment.Symptom symptom) {
        Episode episode = new Episode();
        episode.setCycleId(cycleId);
        episode.setDate(date);
        episode.setTime(LocalTime.now());
        configureEpisodeDetails(episode, symptom);
        return episode;
    }

    /**
     * Configures episode details based on symptom type.
     */
    private void configureEpisodeDetails(Episode episode, LogSymptomsDialogFragment.Symptom symptom) {
        switch (symptom) {
            case HEAVY:
            case MID_LIGHT:
            case LIGHT:
            case VERY_LIGHT:
                episode.setSymptomType("Period");
                episode.setNotes(symptom.name() + " Flow");
                episode.setIntensity(getFlowIntensity(symptom));
                break;
            case RED_SPOT:
            case BROWN_SPOT:
            case LIGHT_BROWN_SPOT:
                episode.setSymptomType("Spotting");
                episode.setNotes(symptom.name());
                episode.setIntensity(2);
                break;
            case MOOD_SWINGS:
            case IS_NERVOUS:
            case IS_STRESSED:
            case IS_ANGRY:
                episode.setSymptomType("Mood");
                episode.setNotes(symptom.name().replace("IS_", ""));
                episode.setIntensity(3);
                break;
            case IS_BLOATING:
            case HAS_HEADACHE:
            case FEELS_STOMACH_PAIN:
                episode.setSymptomType("Pain");
                episode.setNotes(formatPainNote(symptom));
                episode.setIntensity(4);
                break;
            case HAS_HIGH_APPETITE:
            case HAS_LOW_APPETITE:
                episode.setSymptomType("Appetite");
                episode.setNotes(symptom.name().replace("HAS_", ""));
                episode.setIntensity(3);
                break;
            case FEELS_SLEEPY:
            case FEELS_TIRED:
                episode.setSymptomType("Energy");
                episode.setNotes(symptom.name().replace("FEELS_", ""));
                episode.setIntensity(3);
                break;
        }
    }

    /**
     * Formats pain-related symptom notes.
     */
    private String formatPainNote(LogSymptomsDialogFragment.Symptom symptom) {
        return symptom.name()
                .replace("IS_", "")
                .replace("HAS_", "")
                .replace("FEELS_", "");
    }

    /**
     * Handles cycle recovery when no ongoing cycle exists.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleMissingCycle(int userId, Set<LogSymptomsDialogFragment.Symptom> symptoms, LocalDate date) {
        Log.e("SymptomLogger", "Initiating cycle recovery for user " + userId);

        try {
            List<CycleData> cycles = cycleRepository.getCyclesByUserIdSync(userId);
            if (cycles == null || cycles.isEmpty()) {
                Log.e("SymptomLogger", "No cycles available for recovery");
                return;
            }

            CycleData recentCycle = findMostRecentValidCycle(cycles);
            if (recentCycle != null) {
                updateCycleAsOngoing(recentCycle);
                processSymptoms(recentCycle.getCycleId(), symptoms, date);
            }
        } catch (Exception e) {
            Log.e("SymptomLogger", "Cycle recovery failed: " + e.getMessage());
        }
    }

    /**
     * Finds the most recent valid cycle from a list.
     */
    private CycleData findMostRecentValidCycle(List<CycleData> cycles) {
        CycleData recent = null;
        for (CycleData cycle : cycles) {
            if (cycle.getStartDate() != null &&
                    (recent == null || cycle.getStartDate().isAfter(recent.getStartDate()))) {
                recent = cycle;
            }
        }
        return recent;
    }

    /**
     * Marks a cycle as ongoing and persists the change.
     */
    private void updateCycleAsOngoing(CycleData cycle) {
        cycle.setOngoing(true);
        cycleRepository.updateCycle(cycle);
        Log.d("SymptomLogger", "Updated cycle " + cycle.getCycleId() + " as ongoing");
    }

    /**
     * Maps flow symptoms to intensity values.
     */
    private int getFlowIntensity(LogSymptomsDialogFragment.Symptom symptom) {
        switch (symptom) {
            case HEAVY: return 5;
            case MID_LIGHT: return 4;
            case LIGHT: return 3;
            case VERY_LIGHT: return 2;
            default: return 3;
        }
    }
}