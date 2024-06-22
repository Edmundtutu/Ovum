package com.example.ovum;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.Button;

import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class UserIntention extends AppCompatActivity {
    //delcaration of the useful variables for the pop in and info colloection

    // the database helper
    private OvumDbHelper dbHelper;
    private Dialog popupDialogforPeriodOpt;
    private Dialog popupDialogforPregnacyOpt;

    int periodOkBtnId = R.id.periodOkbtn;
    // int periodSkipBtnId = R.id.periodSkippedbtn;

    int pregnancyOkBtnId = R.id.pregancyOkbtn; //not yet implemented the Pregancy layout popup
    //int pregnancySkipBtnId = R.id.pregancySkippedbtn;

    private ProgressDialog progressDialog;
    private EditText editTextPeriodStarted, editTextPeriodEnded, editTextCycleLength, editTextPeriodLength;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_intention);

        progressDialog = new ProgressDialog(this);

        // initialise the popUpDialog for the collect_period_inital_data layout ress
        popupDialogforPeriodOpt = new Dialog(this); // intialise for the period option
        popupDialogforPeriodOpt.setContentView(R.layout.collect_period_initial_data);

        popupDialogforPregnacyOpt = new Dialog(this);
//        popupDialogforPregnacyOpt.setContentView(R.layout.collect_pregnancy_initial_data);

        //initialising the input fields
        editTextPeriodStarted = popupDialogforPeriodOpt.findViewById(R.id.Startperiod);
        editTextPeriodEnded = popupDialogforPeriodOpt.findViewById(R.id.Endperiod);
        editTextCycleLength = popupDialogforPeriodOpt.findViewById(R.id.cyleLength);
        editTextPeriodLength = popupDialogforPeriodOpt.findViewById(R.id.periodLength);

        // set onclick listener for the date picker of all the input fields
//        editTextPeriodStarted.setOnClickListener(this::showDatePickerDialog());
//        editTextPeriodEnded.setOnClickListener(this::showDatePickerDialog);

        // initialize the dbHelper
        dbHelper = new OvumDbHelper(this);

    }

    // method to show the date picker dialog
    private void showDatePickerDialog() {
        // just do nothing for now lets just convert the text entered into date format
        editTextPeriodStarted.getText().toString();
    }

    // have failed to make this class dynamic for the now but will work on it lets first make progress

    // Method to show the popup dialog
    private void showPopup(Dialog popupDialog, int Id) {
        if (popupDialog != null && !popupDialog.isShowing()) {
            // set the relative position of the popup on screen
            WindowManager.LayoutParams layoutParams;
            layoutParams = Objects.requireNonNull(popupDialog.getWindow()).getAttributes();
            layoutParams.gravity = Gravity.BOTTOM;
            layoutParams.width = 750;
            popupDialog.show();

            // Animate popup slide-up entrance
            View popupView = popupDialog.getWindow().getDecorView();
            int translationY = popupView.getHeight();
            ObjectAnimator slideUp = ObjectAnimator.ofFloat(popupView, "translationY", translationY, 0);
            slideUp.setDuration(600);
            slideUp.setInterpolator(new BounceInterpolator());
            popupDialog.getWindow().setAttributes(layoutParams);

            AnimatorSet animationSet = new AnimatorSet();
            animationSet.play(slideUp);
            animationSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animation) {
                    popupDialog.show(); // Show after animation starts
                }

                @Override
                public void onAnimationEnd(@NonNull Animator animation) {
                    // Handle animation end (optional)
                }

                @Override
                public void onAnimationCancel(@NonNull Animator animation) {
                }

                @Override
                public void onAnimationRepeat(@NonNull Animator animation) {
                }
            });

            animationSet.start();

            // set the onclickListener to the skip and ok btn
            Button Dialogbutton = popupDialog.findViewById(Id);

            // Now you can work with the button as needed
            if (Dialogbutton != null) {
                // Button found, set OnClickListener
                Dialogbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        registerUser();
//                        startActivity(new Intent(UserIntention.this, MainActivity.class));
//                        finish();
                    }
                });
            } else {
                Toast.makeText(UserIntention.this, "Cannot find the btn ", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private void registerUser() {
        // Receive user data from the create_account intent
        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");
        final String password = intent.getStringExtra("password");
        final String email = intent.getStringExtra("email");
        final String phoneNumber = intent.getStringExtra("phone_number");
        final String DOB = intent.getStringExtra("DOB");

        // Get the string data from the string editText
        final String cycleLength = editTextCycleLength.getText().toString().trim();
        final String periodLength = editTextPeriodLength.getText().toString().trim();
        final String lastPeriodStartDate = editTextPeriodStarted.getText().toString().trim();
        final String lastPeriodEndDate = editTextPeriodEnded.getText().toString().trim();
        Log.d("input from create account", "Username: " + username + ", Email: " + email + ", Phone Number: " + phoneNumber + ", DOB: " + DOB + ", Password: " + password);
        Log.d("input from the Pop", "Cycle Length: " + cycleLength + ", Period Length: " + periodLength + ", Last Period Start Date: " + lastPeriodStartDate + ", Last Period End Date: " + lastPeriodEndDate);

        progressDialog.setMessage("Registering user...");
        progressDialog.show();

        // Inserting data into the SQLite database
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(OvumContract.PatientEntry.COLUMN_NAME, username);
        values.put(OvumContract.PatientEntry.COLUMN_PHONE_NUMBER, phoneNumber);
        values.put(OvumContract.PatientEntry.COLUMN_LOCATION, ""); // Add appropriate value
        values.put(OvumContract.PatientEntry.COLUMN_EMAIL, email);
        values.put(OvumContract.PatientEntry.COLUMN_PASSWORD, password);
        values.put(OvumContract.PatientEntry.COLUMN_PROFILE_PICTURE, ""); // Add appropriate value
        values.put(OvumContract.PatientEntry.COLUMN_DOB, DOB);
        values.put(OvumContract.PatientEntry.COLUMN_CYCLE_LENGTH, cycleLength);
        values.put(OvumContract.PatientEntry.COLUMN_PERIOD_LENGTH, periodLength);
        values.put(OvumContract.PatientEntry.COLUMN_LAST_PERIOD_DATE, lastPeriodStartDate);
        values.put(OvumContract.PatientEntry.COLUMN_LAST_PERIOD_END_DATE, lastPeriodEndDate);
        values.put(OvumContract.PatientEntry.COLUMN_AVERAGE_CYCLE_LENGTH, ""); // Add appropriate value
        values.put(OvumContract.PatientEntry.COLUMN_AVERAGE_PERIOD_LENGTH, ""); // Add appropriate value
        values.put(OvumContract.PatientEntry.COLUMN_NEXT_PROBABLE_DATE_OF_OVULATION, ""); // Add appropriate value
        values.put(OvumContract.PatientEntry.COLUMN_NEXT_PROBABLE_DATE_OF_PERIOD, ""); // Add appropriate value

        long newRowId = db.insert(OvumContract.PatientEntry.TABLE_NAME, null, values);

        progressDialog.dismiss();
        if (newRowId == -1) {
            Toast.makeText(this, "Error registering user", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "User registered with ID: " + newRowId, Toast.LENGTH_SHORT).show();
            // log the user in by adding their ID username and Pasword in the Sharedprefs
            SharedPrefManager.getInstance(getApplicationContext()).userLogin((int) newRowId, username, email);
            startActivity(new Intent(UserIntention.this, MainActivity.class));
            finish(); // Optional: Close the current activity
        }
    }

    public void goToPopFillPreiod(View view) {
        showPopup(popupDialogforPeriodOpt, periodOkBtnId);
    }

    public void goToPregnancyFillUpPop(View view) {
        showPopup(popupDialogforPregnacyOpt, pregnancyOkBtnId);
    }

}