package com.example.ovum;

import android.content.Context;

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
        // Get the patient ID from shared preferences
        SharedPrefManager sharedPrefs = SharedPrefManager.getInstance(context);
        int patientId = sharedPrefs.getUserId();
        /*
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
    }
} 