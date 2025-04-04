package com.pac.ovum.utils.data;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;

import com.pac.ovum.data.models.Episode;
import com.pac.ovum.data.repositories.CycleRepository;
import com.pac.ovum.data.repositories.EpisodeRepository;
import com.pac.ovum.ui.dialogs.LogSymptomsDialogFragment;
import com.pac.ovum.utils.AppModule;
import com.pac.ovum.utils.SharedPrefManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public class SymptomLogger {
    private Context context;
    private EpisodeRepository episodeRepository;
    private CycleRepository cycleRepository;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public SymptomLogger(Context context) {
        this.context = context;
        this.episodeRepository = AppModule.provideEpisodeRepository(context);
        this.cycleRepository = AppModule.provideCycleRepository(context);
    }

    /**
     * Logs the selected symptoms to the database.
     *
     * @param selectedSymptoms The set of symptoms to log.
     * @param dateRecorded The date when the symptoms are being logged.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void logSymptoms(Set<LogSymptomsDialogFragment.Symptom> selectedSymptoms, LocalDate dateRecorded) {
        // Get the user ID from shared preferences
        SharedPrefManager sharedPrefs = SharedPrefManager.getInstance(context);
        int userId = sharedPrefs.getUserId();

        // Get the current ongoing cycle for this user
        cycleRepository.getOngoingCycleByUserId(userId).observeForever(cycleData -> {
            if (cycleData != null) {
                int cycleId = cycleData.getCycleId();
                
                // Log each symptom as an Episode
                for (LogSymptomsDialogFragment.Symptom symptom : selectedSymptoms) {
                    Episode episode = new Episode();
                    episode.setCycleId(cycleId);
                    episode.setDate(dateRecorded);
                    episode.setTime(LocalTime.now());
                    
                    // Map the symptom enum to appropriate values
                    switch (symptom) {
                        case HEAVY:
                        case MID_LIGHT:
                        case LIGHT:
                        case VERY_LIGHT:
                            episode.setSymptomType("Period");
                            episode.setNotes(symptom.name() + " Flow");
                            episode.setIntensity(getIntensityForFlow(symptom));
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
                            episode.setNotes(symptom.name().replace("IS_", "").replace("HAS_", "").replace("FEELS_", ""));
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
                    
                    // Insert the episode into the database
                    episodeRepository.insertEpisode(episode);
                    Log.d("SymptomLogger", "Logged symptom: " + symptom.name() + " for date: " + dateRecorded);
                }
                
                Log.d("SymptomLogger", "All symptoms logged successfully for cycle ID: " + cycleId);
            } else {
                Log.e("SymptomLogger", "No ongoing cycle found for user ID: " + userId);
            }
        });
    }
    
    private int getIntensityForFlow(LogSymptomsDialogFragment.Symptom symptom) {
        switch (symptom) {
            case HEAVY: return 5;
            case MID_LIGHT: return 4;
            case LIGHT: return 3;
            case VERY_LIGHT: return 2;
            default: return 3;
        }
    }
}