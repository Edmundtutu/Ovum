package com.example.ovum;

import static com.example.ovum.CalendarUtils.dueDateAssociates;
import static com.example.ovum.OvumContract.PatientEntry.COLUMN_AVERAGE_CYCLE_LENGTH;
import static com.example.ovum.OvumContract.PatientEntry.COLUMN_AVERAGE_PERIOD_LENGTH;
import static com.example.ovum.OvumContract.PatientEntry.COLUMN_CYCLE_LENGTH;
import static com.example.ovum.OvumContract.PatientEntry.COLUMN_DOB;
import static com.example.ovum.OvumContract.PatientEntry.COLUMN_EMAIL;
import static com.example.ovum.OvumContract.PatientEntry.COLUMN_LAST_PERIOD_DATE;
import static com.example.ovum.OvumContract.PatientEntry.COLUMN_LOCATION;
import static com.example.ovum.OvumContract.PatientEntry.COLUMN_NAME;
import static com.example.ovum.OvumContract.PatientEntry.COLUMN_NEXT_PROBABLE_DATE_OF_PERIOD;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
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

import static com.example.ovum.CalendarUtils.daysInWeekArray;
import static com.example.ovum.CalendarUtils.selectedDate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ovum.models.Patient;
import com.example.ovum.models.Symptom;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class HomeFragment extends Fragment{

    private Patient patient;
    private TextView DaysLeft;
    // db
    private OvumDbHelper db;
    private SharedPrefManager sharedPrefManager;

    private ImageView centerImage;

    private MainViewModel mainViewModel;
    private TextView dayOfWeekTextView;
    private TextView dateTextView;
    private TextView dayTextView;
    private String currentDate;
    private CompositeDisposable disposable = new CompositeDisposable();
    private RecyclerView calendarRecyclerView;

    private RecyclerView symptomsRecycleView;
    private FloatingActionButton addSymptomButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint({"CheckResult", "NotifyDataSetChanged", "SetTextI18n"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = new OvumDbHelper(getContext());

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // initialize the shared preferences
        sharedPrefManager = SharedPrefManager.getInstance(getContext());
        // Initialize ViewModel
        MainViewModelFactory factory = new MainViewModelFactory(db.getReadableDatabase());
        mainViewModel = new ViewModelProvider(requireActivity(), factory).get(MainViewModel.class);

        // Initialize the DaysTextViews
        dayOfWeekTextView = view.findViewById(R.id.day);
        dateTextView = view.findViewById(R.id.date);
        dayTextView = view.findViewById(R.id.dateNum);

        
        addSymptomButton = view.findViewById(R.id.add_button);

        // Observe currentDate LiveData
        mainViewModel.getCurrentDate().observe(getViewLifecycleOwner(), currentDay -> { // changes from this->getViewLifecylceOwner
            // Update the UI

            String formattedDate = currentDay.getDate();
            // first log the fomattedDate
            Log.v("HomeFragment", "Formatted Date: " + formattedDate);
            //set the current date to the formatted date
            currentDate = formattedDate;
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

        // initalize the recycler view for the Horizontal calendar
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        // set the selected date to today according to the time zone
        selectedDate = LocalDate.now();
        // set the dueDateAssociates to the dates from the DueDate class using the shared preferences
        if(sharedPrefManager.getDueDate().getDatesList()!=null){
            dueDateAssociates = sharedPrefManager.getDueDate().getDatesList();
        }else{
            dueDateAssociates = null;
        }

        setWeekView();

        mainViewModel.getCurrentDate().observe((LifecycleOwner) getContext(), (Observer<? super DayInfo>) dayInfo -> {
            // Update UI with current date information if needed
        });
        // setting the center image
        centerImage = view.findViewById(R.id.center_image);


        DaysLeft = view.findViewById(R.id.days_left);

        // Set the number of days left
        // check the databse for the patient's days according to their id from the shared preferences
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
            DateTimeFormatter formatter = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                formatter = new DateTimeFormatterBuilder()
                        .appendPattern("[d-M-yyyy][dd-M-yyyy]")
                        .toFormatter();
            }

            // Calculate the number of days left by comparing the next period date with the current date
            try {
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

                // Set the number of days left with the proper count pronunciation
                if(daysLeftCount<1){
                    DaysLeft.setText("Your \nPeriod is Late");
                    // clear the due date from the shared preferences
                    sharedPrefManager.storePatientInfo("");
                    // set the background of the center image to redish gradient and add a pulsing effect with faster/stronger beat
                    centerImage.setBackgroundResource(R.drawable.shim_status_container);
                    // Create ObjectAnimator to change opacity of the button itself
                    @SuppressLint("ObjectAnimatorBinding")
                    ObjectAnimator animator = ObjectAnimator.ofFloat(centerImage, "alpha", 0.2f, 1.0f, 0.2f);
                    animator.setDuration(500); // Set duration to 500 milliseconds (0.5 second)
                    animator.setRepeatCount(ObjectAnimator.INFINITE); // Repeat animation indefinitely
                    animator.setRepeatMode(ObjectAnimator.REVERSE); // Reverse animation for smooth pulsing
                    animator.start(); // Start the animation

                    // check from the database if the next probable period date is beyond the current date, if true then set the due date of the shared preferences to the next probable period date
                    patient = extractRelevantInfoFromDb(userId, userEmail, db);
                    String changedNextPeriodDate = patient.getNextPeriodDate();
                    if(changedNextPeriodDate!=null){
                        if(LocalDate.parse(changedNextPeriodDate,formatter).isAfter(currentDate)){
                            String dueDateCalculated = String.valueOf(nextPeriodDate);
                            sharedPrefManager.storePatientInfo(dueDateCalculated);
                        }
                    }
                }else {
                    DaysLeft.setText(String.valueOf("Today\n"+daysLeftCount) + " days Left");
                    // set the background animation of the center image
                    if(daysLeftCount<=5){
                        // set the background of the center image to redish gradient and add a pulsing effect
                        centerImage.setBackgroundResource(R.drawable.shim_status_container);
                        // Create ObjectAnimator to change opacity of the button itself
                        @SuppressLint("ObjectAnimatorBinding")
                        ObjectAnimator animator = ObjectAnimator.ofFloat(centerImage, "alpha", 0.2f, 1.0f, 0.2f);
                        animator.setDuration(1000); // Set duration to 1000 milliseconds (1 second)
                        animator.setRepeatCount(ObjectAnimator.INFINITE); // Repeat animation indefinitely
                        animator.setRepeatMode(ObjectAnimator.REVERSE); // Reverse animation for smooth pulsing
                        animator.start(); // Start the animation
                        // if its one day left the the right pronunciation would be day left
                        if(daysLeftCount==1){DaysLeft.setText(String.valueOf("Today\n"+daysLeftCount) + " day Left");}
                    }

                }
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

        addSymptomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogSymptomsDialogFragment dialogFragment = new LogSymptomsDialogFragment(currentDate);
                dialogFragment.show(getParentFragmentManager(), "CustomDialog");
            }
        });


        // initialize the recycler view for the symptoms
        symptomsRecycleView = view.findViewById(R.id.days_symptoms_recycle_view);
        // set the adapter for the symptoms
        setSymptomsView(symptomsRecycleView);


        return view;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setWeekView() {
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);


        HorizontalCalendarAdapter horizontalCalendarAdapter = new HorizontalCalendarAdapter(mainViewModel, days, (parent, view, position, id) -> {
            CalendarUtils.selectedDate = days.get(position);
        },getViewLifecycleOwner(),getContext());
        // Set the layout manager and adapter for the recycler view
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7); // using the date_item.xml as the layout for the recycler view
        calendarRecyclerView.setLayoutManager(layoutManager);
//        calendarRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)); // using the item_local_cal.xml as the layout for the recycler view
        calendarRecyclerView.setAdapter(horizontalCalendarAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void  setSymptomsView(View view){
        // get the symptoms from the database
        ArrayList<Symptom> symptoms = new ArrayList<>();
        // get the symptoms from the database
        try (Cursor cursor = db.getSymptoms(sharedPrefManager.getUserId(), LocalDate.now().toString())) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Get data from cursor
                    String symptomDate = cursor.getString(cursor.getColumnIndexOrThrow(OvumContract.SymptomEntry.COLUMN_DATE_RECORDED));
                    String symptomDescription = cursor.getString(cursor.getColumnIndexOrThrow(OvumContract.SymptomEntry.COLUMN_DESCRIPTION));

                    // Log the symptom's information for debugging
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        Log.v("HomeFragment sypmtoms", cursor.getColumnName(i) + ": " + cursor.getString(i));
                    }

                    // add these results to the symptoms list
                    symptoms.add(new Symptom(new DateUtils().formatDateToLocalDate(symptomDate), symptomDescription));
                } while (cursor.moveToNext());
                // for now lets close the cursor
                cursor.close();
            } else {
                Toast.makeText(getContext(), "No symptoms data found", Toast.LENGTH_LONG).show();
            }
        }

        // set the adapter for the symptoms
        HomeSymptomsAdapter homeSymptomsAdapter = new HomeSymptomsAdapter(symptoms,this::onIconClick);
        symptomsRecycleView.setAdapter(homeSymptomsAdapter);
        symptomsRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    public void onIconClick(int position) {
        // handle the click on the eye icon
        // get the symptom at the position
        Symptom symptom = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            symptom = new Symptom(LocalDate.now(), "Headache");
        }
        // log the symptom description
        Log.v("HomeFragment", "Symptom Description: " + symptom.getSymptomDescription());
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