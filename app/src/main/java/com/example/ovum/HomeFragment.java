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
        //query the Db to  identify the patient with the corresponding id and email

        extractRelevantInfoFromDb(userId, userEmail);
        Patient thePatient = getPatient();
//

        if (thePatient != null) {
            String nextPeriodDateStr = thePatient.getNextPeriodDate();

            if (nextPeriodDateStr == null || nextPeriodDateStr.isEmpty()) {
                DaysLeft.setText("0 Days");
                return view;
            }

            try {
                DateTimeFormatter formatter = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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
                DaysLeft.setText(String.valueOf(daysLeftCount+ " Days"));
                // store the number of days left in the shared preferences
                sharedPrefManager.storePatientInfo(String.valueOf(daysLeftCount));
                // now log it out
                Log.v("HomeFragmentTestPerStore", "Days Left: saved in log as days left " + sharedPrefManager.getDueDate());
            } catch (Exception e) {
                e.printStackTrace();
                DaysLeft.setText("Error");
            }
        }
//        if(thePatient!=null){
//            if(thePatient.getNextPeriodDate() == null){
//                DaysLeft.setText("0");
//                return view;
//            }
//            String nextProbableDateOfOvulation = thePatient.getNextPeriodDate().toString();
//            Log.v("Homefrag" ,"Next Probable Date of Ovulation: " + nextProbableDateOfOvulation);
//            String[] dateParts = nextProbableDateOfOvulation.split("-");
//            int year = Integer.parseInt(dateParts[0]);
//            int month = Integer.parseInt(dateParts[1]);
//            int day = Integer.parseInt(dateParts[2]);
//            // Get the current date
//            java.util.Calendar calendar = java.util.Calendar.getInstance();
//            int currentYear = calendar.get(java.util.Calendar.YEAR);
//            int currentMonth = calendar.get(java.util.Calendar.MONTH) + 1;
//            int currentDay = calendar.get(java.util.Calendar.DAY_OF_MONTH);
//
//            // Calculate the number of days left
//            int daysLeft = 0;
//            if (year == currentYear) {
//                if (month == currentMonth) {
//                    daysLeft = day - currentDay;
//                } else if (month > currentMonth) {
//                    daysLeft = (month - currentMonth) * 30 + (day - currentDay);
//                }
//            } else if (year > currentYear) {
//                daysLeft = (year - currentYear) * 365 + (month - currentMonth) * 30 + (day - currentDay);
//            }
//
//            // Set the number of days left
//            DaysLeft.setText(String.valueOf(daysLeft));



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

    private void extractRelevantInfoFromDb(int id, String email){
        Cursor cursor = db.getPatient(id,email);

        if (cursor != null && cursor.moveToFirst()) {
            // Get data from cursor
            String patientName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            String patientLocation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION));
            String patientEmail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
            String patientNextPeriodDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NEXT_PROBABLE_DATE_OF_OVULATION));
            String patientCycleLength = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CYCLE_LENGTH));
            String patientLatestPeriod = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_PERIOD_DATE));
            String patientAverageCylceLength = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AVERAGE_CYCLE_LENGTH));
            String patientAveragePeriodLength = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AVERAGE_PERIOD_LENGTH));
            String patientDob = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOB));

            // add these results to the patient's profile
            patient = new Patient(patientName, patientLocation, patientEmail, patientNextPeriodDate, patientCycleLength, patientLatestPeriod, patientAverageCylceLength, patientAveragePeriodLength, patientDob);

            // for now lets close the cursor
            cursor.close();
        } else {
            Toast.makeText(getContext(), "No patient data found", Toast.LENGTH_LONG).show();
        }
    }
    // return the Patient Obtained from the database
    public Patient getPatient(){
        return patient;
    }
}