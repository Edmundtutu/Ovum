package com.pac.ovum.utils.data;

import android.content.Context;

import com.pac.ovum.ui.dialogs.LogSymptomsDialogFragment;

import java.time.LocalDate;
import java.util.Set;

public class SymptomLogger {
    private Context context;

    public SymptomLogger(Context context) {
        this.context = context;
    }

    /**
     * Logs the selected symptoms to the database.
     *
     * @param selectedSymptoms The set of symptoms to log.
     * @param dateRecorded The date when the symptoms are being logged.
     */
    public void logSymptoms(Set<LogSymptomsDialogFragment.Symptom> selectedSymptoms, LocalDate dateRecorded) {

        /*
        // Perform database operations
        try (OvumDbHelper ovumDbHelper = new OvumDbHelper(context)) {
            for (LogSymptomsDialogFragment.Symptom symptom : selectedSymptoms) {
                // Log each symptom to the database
                switch (symptom) {
                    case HEAVY:
                        ovumDbHelper.updateSymptoms(patientId, "Physical", "Period", "Heavy Flo", String.valueOf(dateRecorded), null, 5);
                        break;
                    case MID_LIGHT:
                        ovumDbHelper.updateSymptoms(patientId, "Physical", "Period", "Mid Flo", String.valueOf(dateRecorded), null, 5);
                        break;
                    case LIGHT:
                        ovumDbHelper.updateSymptoms(patientId, "Physical", "Period", "Light Flo", String.valueOf(dateRecorded), null, 5);
                        break;
                    // Add other symptoms as needed
                    // ...
                }
            }
            Log.v("SymptomLogger", "Symptoms have been logged successfully.");
        } catch (Exception e) {
            Log.e("SymptomLogger", "Error logging symptoms", e);
        }
        */
    }
}