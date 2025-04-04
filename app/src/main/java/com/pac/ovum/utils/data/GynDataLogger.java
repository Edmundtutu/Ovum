package com.pac.ovum.utils.data;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;

import com.pac.ovum.data.models.Event;
import com.pac.ovum.data.repositories.CycleRepository;
import com.pac.ovum.data.repositories.EventRepository;
import com.pac.ovum.ui.dialogs.SetGynEventFragment;
import com.pac.ovum.utils.AppModule;
import com.pac.ovum.utils.SharedPrefManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

/**
 * A class responsible for logging gynecological data to the database.
 */
public class GynDataLogger {
    private Context context;
    private EventRepository eventRepository;
    private CycleRepository cycleRepository;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public GynDataLogger(Context context) {
        this.context = context;
        this.eventRepository = AppModule.provideEventRepository(context);
        this.cycleRepository = AppModule.provideCycleRepository(context);
    }

    /**
     * Logs the gynecological data to the database.
     *
     * @param selectedData The set of gynecological data to log.
     * @param dateRecorded The date when the data is being logged.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void logGynData(Set<SetGynEventFragment.GynEvent> selectedData, LocalDate dateRecorded) {
        // Get the user ID from shared preferences
        SharedPrefManager sharedPrefs = SharedPrefManager.getInstance(context);
        int userId = sharedPrefs.getUserId();

        // Get the current ongoing cycle for this user
        cycleRepository.getOngoingCycleByUserId(userId).observeForever(cycleData -> {
            if (cycleData != null) {
                int cycleId = cycleData.getCycleId();
                
                // Log each gynecological event
                for (SetGynEventFragment.GynEvent gynEvent : selectedData) {
                    Event event = new Event();
                    event.setCycleId(cycleId);
                    event.setEventDate(dateRecorded);
                    event.setEventTime(LocalTime.now());
                    event.setEventType("Gyn Event");
                    
                    // Set description based on the event type
                    switch (gynEvent) {
                        case MEET_DOC:
                            event.setDescription("Meet Doctor");
                            break;
                        case TALK_TO_DOC:
                            event.setDescription("Talk to Doctor");
                            break;
                        case LOG_ASYMPTOM:
                            event.setDescription("Logged a Symptom");
                            break;
                        case INVOLVE_IN_SEX:
                            event.setDescription("Sexual Intercourse");
                            break;
                        case TAKE_A_PILL:
                            event.setDescription("Took Medication");
                            break;
                        case MEET_YOUR_DOC:
                            event.setDescription("Meet with Doctor");
                            break;
                        case CALL_DOC:
                            event.setDescription("Called Doctor");
                            break;
                        case PRESCRIBED:
                            event.setDescription("Received Prescription");
                            break;
                    }
                    
                    // Insert the event into the database
                    eventRepository.insertEvent(event);
                    Log.d("GynDataLogger", "Logged event: " + gynEvent.name() + " for date: " + dateRecorded);
                }
                
                Log.d("GynDataLogger", "All gynecological events logged successfully for cycle ID: " + cycleId);
            } else {
                Log.e("GynDataLogger", "No ongoing cycle found for user ID: " + userId);
            }
        });
    }
} 