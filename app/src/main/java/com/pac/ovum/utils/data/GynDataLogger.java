package com.pac.ovum.utils.data;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.pac.ovum.data.models.CycleData;
import com.pac.ovum.data.models.Event;
import com.pac.ovum.data.repositories.CycleRepository;
import com.pac.ovum.data.repositories.EventRepository;
import com.pac.ovum.ui.dialogs.SetGynEventFragment;
import com.pac.ovum.utils.AppExecutors;
import com.pac.ovum.utils.AppModule;
import com.pac.ovum.utils.SharedPrefManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

/**
 * Handles logging of gynecological events to database with proper lifecycle awareness.
 * Manages cycle associations and maintains thread safety for LiveData operations.
 */
public class GynDataLogger {
    private final Context context;
    private final EventRepository eventRepository;
    private final CycleRepository cycleRepository;

    /**
     * Constructs a GynDataLogger with required dependencies.
     *
     * @param context The application context for resource access
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public GynDataLogger(Context context) {
        this.context = context;
        this.eventRepository = AppModule.provideEventRepository(context);
        this.cycleRepository = AppModule.provideCycleRepository(context);
    }

    /**
     * Main entry point for logging gynecological events.
     * Follows Android threading guidelines by:
     * - Performing sync DB operations on diskIO thread
     * - Observing LiveData on main thread
     * - Processing results on appropriate background threads
     *
     * @param selectedData  Set of user-selected gynecological events
     * @param dateRecorded  The date associated with the events
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void logGynData(Set<SetGynEventFragment.GynEvent> selectedData, LocalDate dateRecorded) {
        final int userId = SharedPrefManager.getInstance(context).getUserId();
        Log.d("GynDataLogger", "Initiating logging for date: " + dateRecorded);

        // Start with diskIO thread for synchronous cycle check
        AppExecutors.getInstance().diskIO().execute(() -> {
            final CycleData ongoingCycle = cycleRepository.getOngoingCycleByUserIdSync(userId);

            if (ongoingCycle != null) {
                handleExistingCycle(ongoingCycle, selectedData, dateRecorded);
            } else {
                handleMissingCycleFlow(userId, selectedData, dateRecorded);
            }
        });
    }

    /**
     * Processes events when an ongoing cycle is found.
     *
     * @param cycle The ongoing cycle data
     * @param events Set of events to process
     * @param date Recording date
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleExistingCycle(CycleData cycle, Set<SetGynEventFragment.GynEvent> events, LocalDate date) {
        Log.d("GynDataLogger", "Processing " + events.size() + " events for cycle " + cycle.getCycleId());
        processGynEvents(cycle.getCycleId(), events, date);
    }

    /**
     * Handles the case when no ongoing cycle is found synchronously.
     * Switches to main thread for LiveData observation per Android guidelines.
     */
    private void handleMissingCycleFlow(int userId, Set<SetGynEventFragment.GynEvent> events, LocalDate date) {
        Log.w("GynDataLogger", "Initiating async cycle lookup for user " + userId);

        AppExecutors.getInstance().mainThread().execute(() -> {
            final LiveData<CycleData> cycleLiveData = cycleRepository.getOngoingCycleByUserId(userId);
            final Observer<CycleData> cycleObserver = new Observer<CycleData>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onChanged(CycleData cycleData) {
                    cycleLiveData.removeObserver(this);

                    if (cycleData != null) {
                        AppExecutors.getInstance().diskIO().execute(() -> {
                            processGynEvents(cycleData.getCycleId(), events, date);
                        });
                    } else {
                        AppExecutors.getInstance().diskIO().execute(() -> {
                            handleMissingCycle(userId, events, date);
                        });
                    }
                }
            };
            cycleLiveData.observeForever(cycleObserver);
        });
    }

    /**
     * Processes and stores gynecological events in the database.
     * Must be called from background thread for database operations.
     *
     * @param cycleId      Target cycle ID for event association
     * @param selectedData Events to process
     * @param dateRecorded Event date
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void processGynEvents(int cycleId, Set<SetGynEventFragment.GynEvent> selectedData, LocalDate dateRecorded) {
        for (SetGynEventFragment.GynEvent eventType : selectedData) {
            final Event event = createEventObject(cycleId, dateRecorded, eventType);
            eventRepository.insertEvent(event);
            Log.d("GynDataLogger", "Stored event: " + event.getDescription());
        }
    }

    /**
     * Creates an Event object from raw data.
     *
     * @param cycleId      Associated cycle ID
     * @param dateRecorded Event date
     * @param eventType    Type of gynecological event
     * @return Prepared Event object
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private Event createEventObject(int cycleId, LocalDate dateRecorded, SetGynEventFragment.GynEvent eventType) {
        final Event event = new Event();
        event.setCycleId(cycleId);
        event.setEventDate(dateRecorded);
        event.setEventTime(LocalTime.now());
        event.setEventType(mapEventDescriptionToEventCategory(translateEventType(eventType)));
        event.setDescription(translateEventType(eventType));
        return event;
    }

    /**
     * Converts UI event types to database descriptions.
     *
     * @param eventType UI event enumeration
     * @return Human-readable description
     */
    private String translateEventType(SetGynEventFragment.GynEvent eventType) {
        switch (eventType) {
            case MEET_DOC: return "Meet Doctor";
            case TALK_TO_DOC: return "Talk to Doctor";
            case LOG_ASYMPTOM: return "Logged a Symptom";
            case INVOLVE_IN_SEX: return "Sexual Intercourse";
            case TAKE_A_PILL: return "Took Medication";
            case MEET_YOUR_DOC: return "Meet with Doctor";
            case CALL_DOC: return "Called Doctor";
            case PRESCRIBED: return "Received Prescription";
            default: return "Unknown Medical Event";
        }
    }
    /**
     * Maps event descriptions to event categories.
     * Categories are Gyn Event, Appointment, Emergency and Medication.
     *
     * @return Event category string
     */
    private String mapEventDescriptionToEventCategory(String eventDescription){
        switch (eventDescription) {
            case "Talk to Doctor":
            case "Logged a Symptom":
            case "Sexual Intercourse":
                return "Gyn Event";
            case "Took Medication":
                return "Medication";
            case "Meet Doctor":
            case "Called Doctor":
                return "Appointment";
            case "Received Prescription":
                return "Emergency";
            default:
                return "Unknown Category";
        }
    }

    /**
     * Fallback handler when no cycles are found.
     * Attempts cycle recovery on background thread.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleMissingCycle(int userId, Set<SetGynEventFragment.GynEvent> events, LocalDate date) {
        Log.e("GynDataLogger", "Cycle recovery initiated for user " + userId);

        try {
            final List<CycleData> cycles = cycleRepository.getCyclesByUserIdSync(userId);
            if (cycles == null || cycles.isEmpty()) {
                Log.e("GynDataLogger", "No cycles available for recovery");
                return;
            }

            final CycleData recentCycle = findMostRecentCycle(cycles);
            if (recentCycle != null) {
                updateCycleAsOngoing(recentCycle);
                processGynEvents(recentCycle.getCycleId(), events, date);
            }
        } catch (Exception e) {
            Log.e("GynDataLogger", "Cycle recovery failed: " + e.getMessage());
        }
    }

    /**
     * Identifies the most recent cycle from a list.
     *
     * @param cycles List of candidate cycles
     * @return Most recent cycle by start date
     */
    private CycleData findMostRecentCycle(List<CycleData> cycles) {
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
     * Updates a cycle to mark it as ongoing.
     *
     * @param cycle Cycle to update
     */
    private void updateCycleAsOngoing(CycleData cycle) {
        cycle.setOngoing(true);
        cycleRepository.updateCycle(cycle);
        Log.d("GynDataLogger", "Marked cycle " + cycle.getCycleId() + " as ongoing");
    }
}