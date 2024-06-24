package com.example.ovum;

import static com.example.ovum.OvumContract.PatientEntry.COLUMN_AVERAGE_CYCLE_LENGTH;
import static com.example.ovum.OvumContract.PatientEntry.COLUMN_AVERAGE_PERIOD_LENGTH;
import static com.example.ovum.OvumContract.PatientEntry.COLUMN_CYCLE_LENGTH;
import static com.example.ovum.OvumContract.PatientEntry.COLUMN_DOB;
import static com.example.ovum.OvumContract.PatientEntry.COLUMN_EMAIL;
import static com.example.ovum.OvumContract.PatientEntry.COLUMN_LAST_PERIOD_DATE;
import static com.example.ovum.OvumContract.PatientEntry.COLUMN_LOCATION;
import static com.example.ovum.OvumContract.PatientEntry.COLUMN_NAME;
import static com.example.ovum.OvumContract.PatientEntry.COLUMN_NEXT_PROBABLE_DATE_OF_OVULATION;
import static com.example.ovum.OvumContract.PatientEntry.COLUMN_NEXT_PROBABLE_DATE_OF_PERIOD;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;

public class HomeFragment extends Fragment{

    private Button SymptomsBtn;

    private Patient patient;
    private TextView DaysLeft;

    // db
    private OvumDbHelper db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        SymptomsBtn = view.findViewById(R.id.logSymptomsBtn);
        DaysLeft = view.findViewById(R.id.daysLeft);
        db = new OvumDbHelper(getContext());

        // Set the number of days left
        // check the databse for the patient's days according to their id from the shared preferences
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getContext());
        int userId = sharedPrefManager.getUserId();
        Log.v("HomeFragment", "User ID: " + userId);
        String userEmail = sharedPrefManager.getUserEmail();
        Log.v("HomeFragment", "User Email: " + userEmail);

        // Call the extractRelevantInfoFromDb to query the Db to  identify the patient with the corresponding id and email

        Patient thePatient = extractRelevantInfoFromDb(userId, userEmail);

        if (thePatient != null) {
            String nextPeriodDateStr = thePatient.getNextPeriodDate();

            // Log the next period date for debugging
            Log.v("HomeFragment", "Next Period Date: " + nextPeriodDateStr);

            // if Null set the days left to 0
            if (nextPeriodDateStr == null || nextPeriodDateStr.isEmpty()) {
                DaysLeft.setText("0 Days");
                return view;
            }

            // Calculate the number of days left by comparing the next period date with the current date
            try {
                DateTimeFormatter formatter = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    formatter = new DateTimeFormatterBuilder()
                            .appendPattern("[d-M-yyyy][dd-M-yyyy]")
                            .toFormatter();
                }

                LocalDate nextPeriodDate = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    nextPeriodDate = LocalDate.parse(nextPeriodDateStr, formatter);
                }

                LocalDate currentDate = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    currentDate = LocalDate.now();
                }

                long daysLeftCount = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    daysLeftCount = ChronoUnit.DAYS.between(currentDate, nextPeriodDate);
                }

                // Ensure that daysLeftCount is non-negative
                daysLeftCount = Math.max(daysLeftCount, 0);

                // Set the number of days left
                DaysLeft.setText(String.valueOf(daysLeftCount) + " Days");

                // Store the number of days left in the shared preferences
                sharedPrefManager.storePatientInfo(String.valueOf(daysLeftCount));

                // Log it out
                Log.v("HomeFragmentTestPerStore", "Days Left: saved in log as days left " + sharedPrefManager.getDueDate());
            } catch (Exception e) {
                e.printStackTrace();
                DaysLeft.setText("Error");
            }

        }

        SymptomsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get a reference to the activity
                MainActivity activity = (MainActivity) getActivity();
                if (activity != null) {
                    // Start the new activity
                    activity.startActivity(new Intent(activity, LogSymptoms.class));
                }else {
                    // Log an error
                    Log.d("HomeFragment", "Activity is null");
                }
            }
        });
        return view;
    }

    // Extract the relevant information from the database Method. Takes the id and email of the patient as arguments and returns the patient object
    private Patient extractRelevantInfoFromDb(int id, String email){
        Cursor cursor = db.getPatient(id,email);

        if (cursor != null && cursor.moveToFirst()) {
            // Get data from cursor
            String patientName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            String patientLocation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION));
            String patientEmail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
            String patientNextPeriodDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NEXT_PROBABLE_DATE_OF_PERIOD));
            String patientCycleLength = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CYCLE_LENGTH));
            String patientLatestPeriod = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_PERIOD_DATE));
            String patientAverageCylceLength = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AVERAGE_CYCLE_LENGTH));
            String patientAveragePeriodLength = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AVERAGE_PERIOD_LENGTH));
            String patientDob = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOB));

            // Log the patient's information for debugging
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                Log.v("HomeFragment", cursor.getColumnName(i) + ": " + cursor.getString(i));
            }

            // add these results to the patient's profile
            patient = new Patient(patientName, patientLocation, patientEmail, patientNextPeriodDate, patientCycleLength, patientLatestPeriod, patientAverageCylceLength, patientAveragePeriodLength, patientDob);

            // for now lets close the cursor
            cursor.close();
        } else {
            Toast.makeText(getContext(), "No patient data found", Toast.LENGTH_LONG).show();
        }
        // return the Patient Object Obtained from the database with all the Desired attributes
        return patient;
    }

    // this is a getter method that returns the object of the Patient got from this class
    // This same patient object returned can now be used in other classes (works indirectly as a sharedPref)
    public Patient getPatient(){
        return patient;
    }
}