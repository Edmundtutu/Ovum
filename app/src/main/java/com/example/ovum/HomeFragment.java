package com.example.ovum;

import static android.content.ContentValues.TAG;
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

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class HomeFragment extends Fragment{

    private Patient patient;
    private TextView DaysLeft;
    // db
    private OvumDbHelper db;

    private ImageView centerImage;

    private MainViewModel mainViewModel;
    private HorizontalCalendarAdapter adapter;
    private LinearLayoutManager layoutManager;
    private TextView dayOfWeekTextView;
    private TextView dateTextView;
    private TextView dayTextView;
    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint({"CheckResult", "NotifyDataSetChanged"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize ViewModel
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Initialize the TextViews
        dayOfWeekTextView = view.findViewById(R.id.day);
        dateTextView = view.findViewById(R.id.date);
        dayTextView = view.findViewById(R.id.dateNum);

        // Observe currentDate LiveData
        mainViewModel.getCurrentDate().observe(getViewLifecycleOwner(), currentDay -> { // changes from this->getViewLifecylceOwner
            // Update the UI

            String formattedDate = currentDay.getDate();
            // first log the fomattedDate
            Log.v("HomeFragment", "Formatted Date: " + formattedDate);
            // Split the formatted date string into parts
            String[] dateParts = formattedDate.split(" ");

            // Extract the month, day, and year
            String month = dateParts[0];  // "MMM"
            String day = dateParts[1].replace(",", "");  // "dd"
            String year = dateParts[2];  // "yyyy"

            // Combine the day and year into a single string
            String dayYear = day + ", " + year;
            dayOfWeekTextView.setText(currentDay.getDayOfWeek());
            dateTextView.setText(month);
            dayTextView.setText(dayYear);
        });

        // Initialize RecyclerView and set its layout manager
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

// Initialize the adapter
        HorizontalCalendarAdapter adapter = new HorizontalCalendarAdapter(new HorizontalCalendarAdapter.DiffCallback());
        recyclerView.setAdapter(adapter);

// Set the item click listener
        adapter.setOnItemClickListener(date -> {
            Toast.makeText(getContext(), "Selected date: " + date.toString(), Toast.LENGTH_SHORT).show();
            mainViewModel.setCurrentDate(date); // Update the current date in the ViewModel
        });

// Observe the paging data
        mainViewModel.getFlowablePagingData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pagingData -> {
                    adapter.submitData(getLifecycle(), pagingData);
                    Log.d(TAG, "Paging data received: " + pagingData);
                    recyclerView.post(() -> layoutManager.scrollToPosition(adapter.getItemCount() / 2));
                });

// Initialize and set due dates
        Set<LocalDate> dueDates = new HashSet<>();
        dueDates.add(LocalDate.of(2024, 7, 15));
        mainViewModel.setDueDates(dueDates);

        Set<LocalDate> oneFromDueDates = new HashSet<>();
        oneFromDueDates.add(LocalDate.of(2024, 7, 16));
        mainViewModel.setOneFromDueDates(oneFromDueDates);

        Set<LocalDate> oneToDueDates = new HashSet<>();
        oneToDueDates.add(LocalDate.of(2024, 7, 14));
        mainViewModel.setOneToDueDates(oneToDueDates);

        Set<LocalDate> twoFromDueDates = new HashSet<>();
        twoFromDueDates.add(LocalDate.of(2024, 7, 17));
        mainViewModel.setTwoFromDueDates(twoFromDueDates);

        Set<LocalDate> twoToDueDates = new HashSet<>();
        twoToDueDates.add(LocalDate.of(2024, 7, 13));
        mainViewModel.setTwoToDueDates(twoToDueDates);


        // setting the center image
        centerImage = view.findViewById(R.id.centerImage);
        // Create ObjectAnimator to change opacity of the button itself
        @SuppressLint("ObjectAnimatorBinding")
        ObjectAnimator animator = ObjectAnimator.ofFloat(centerImage, "alpha", 0.2f, 1.0f, 0.2f);
        animator.setDuration(1000); // Set duration to 1000 milliseconds (1 second)
        animator.setRepeatCount(ObjectAnimator.INFINITE); // Repeat animation indefinitely
        animator.setRepeatMode(ObjectAnimator.REVERSE); // Reverse animation for smooth pulsing
        animator.start(); // Start the animation

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

        Patient thePatient = extractRelevantInfoFromDb(userId, userEmail, db);

        if (thePatient != null) {
            String nextPeriodDateStr = thePatient.getNextPeriodDate();

            // Log the next period date for debugging
            Log.v("HomeFragment", "Next Period Date: " + nextPeriodDateStr);

            // if Null set the days left to 0
            if (nextPeriodDateStr == null || nextPeriodDateStr.isEmpty()) {
                DaysLeft.setText("");
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

                // dueDate now should be the next period date!! so we store it in the shared preferences
                String dueDateCalculated = String.valueOf(nextPeriodDate);
                sharedPrefManager.storePatientInfo(dueDateCalculated);

                // Log it out
                Log.v("HomeFragmentTestPerStore", "Due date: saved in log is " + sharedPrefManager.getDueDate());
            } catch (Exception e) {
                e.printStackTrace();
                DaysLeft.setText("Error");
            }

        }

        centerImage.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    // Extract the relevant information from the database Method. Takes the id and email of the patient as arguments and returns the patient object
    private Patient extractRelevantInfoFromDb(int id, String email, OvumDbHelper db){
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

}