package com.pac.ovum.utils.data;

import android.content.Context;

import com.pac.ovum.ui.dialogs.SetGynEventFragment;

import java.time.LocalDate;
import java.util.Set;

/**
 * A class responsible for logging gynecological data to the database.
 */
public class GynDataLogger {
    private Context context;

    public GynDataLogger(Context context) {
        this.context = context;
    }

    /**
     * Logs the gynecological data to the database.
     *
     * @param selectedData The set of gynecological data to log.
     * @param dateRecorded The date when the data is being logged.
     */
    public void logGynData(Set<SetGynEventFragment.GynEvent> selectedData, LocalDate dateRecorded) {
       /*
        // Get the patient ID from shared preferences
        SharedPrefManager sharedPrefs = SharedPrefManager.getInstance(context);
        int patientId = sharedPrefs.getUserId();

        // Perform database operations
        try (OvumDbHelper ovumDbHelper = new OvumDbHelper(context)) {
            for (String data : selectedData) {
                // Log each piece of gynecological data to the database
                ovumDbHelper.updateGynData(patientId, data, String.valueOf(dateRecorded));
            }
            Log.v("GynDataLogger", "Gynecological data has been logged successfully.");
        } catch (Exception e) {
            Log.e("GynDataLogger", "Error logging gynecological data", e);
        }

         */

        // @RequiresApi(api = Build.VERSION_CODES.O)
        // private void addEvents() {
        //     if (!selectedEvents.isEmpty()) {
        //         // Logic to add events
        //         LocalDate dateOfEvent = new DateUtils().formatDateToLocalDate(titleDate);
        //         Log.d("Date", dateOfEvent.toString());

        //         for (GynEvent event : selectedEvents) {
        //             switch (event) {
        //                 case MEET_DOC:
        //                     // Add logic for Meet the Doctor
        //                     break;
        //                 case TALK_TO_DOC:
        //                     // Add logic for Talk to the Doctor
        //                     break;
        //                 case LOG_ASYMPTOM:
        //                     // Add logic for Log a Symptom
        //                     break;
        //                 case INVOLVE_IN_SEX:
        //                     // Add logic for Involve in sex
        //                     break;
        //                 case TAKE_A_PILL:
        //                     // Add logic for Take a Pill
        //                     break;
        //                 case MEET_YOUR_DOC:
        //                     // Add logic for Meet your Doctor
        //                     break;
        //                 case CALL_DOC:
        //                     // Add logic for Call the Doctor
        //                     break;
        //                 case PRESCRIBED:
        //                     // Add logic for Prescribed
        //                     break;
        //             }
        //         }
        //         dismiss();
        //     } else {
        //         Log.v("Events", "None of the Events are selected");
        //     }
        // }

    }
} 