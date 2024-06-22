package com.example.ovum;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ovum.databinding.ActivityMainBinding;
import com.example.ovum.databinding.DialogCalenderViewBinding;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int MENU_HOME = R.id.navigation_home;
    private static final int MENU_CYCLES = R.id.navigation_more;
    private static final int MENU_TCALENDAR = R.id.navigation_Testcalendar;

    private Patient patient;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AndroidThreeTen.init(this);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the toolbar
        setSupportActionBar(binding.toolBar);

//        // seting the username to the textview
//        String username = SharedPrefManager.getInstance(this).getUsername();
//        binding.username.setText("Hi "+username);
//        //setting the current date to the textview calenderIcon
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            LocalDate currentDate = LocalDate.now();
//            binding.textHeader.setText(currentDate.getMonth()+"    "+currentDate.getDayOfMonth()+"  ");
//
//        }
//
//        binding.textHeader.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showCalendarPopup(new OnDateSelectedListener() {
//                    @Override
//                    public void onDateSelected(String date) {
//                        // Handle the selected date here
//                        binding.textHeader.setText(date);
//                        // we shall implement more of the change of the interface to match the selected date
//                    }
//                });
//
//            }
//        });

        // set the HomeFragmenLayout as the first frame
        replaceFragment(new HomeFragment());

        binding.navView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();
            if (itemId == MENU_HOME) {
                replaceFragment(new HomeFragment());
            } else if (itemId == MENU_CYCLES) {
                replaceFragment(new CyclesFragment());
            } else if (itemId == MENU_TCALENDAR) {
                replaceFragment(new TestCalenderFragment());
            }
            return true;
        });




    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String profileDob = null;
        String profileDueDate = null;
        String profileUsername = null;
        String profileEmail = null;
        //for profile option
        if(id == R.id.accountProfile){
            patient = extractPatient();
            if(patient != null){
                 profileDob = patient.getDob();
                 // get the rest f the profile from the sharedPrefs
                SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this);
                 profileDueDate = sharedPrefManager.getDueDate();
                 profileUsername = sharedPrefManager.getUsername();
                 profileEmail = sharedPrefManager.getUserEmail();

                 // for debuging lets first log these values and ensure they are not null
                Log.v("Profile", "DOB: "+profileDob+" DueDate: "+profileDueDate+" Username: "+profileUsername+" Email: "+profileEmail);
            }
            ProfileFragment fragment = ProfileFragment.newInstance(profileDob, profileDueDate, profileUsername, profileEmail);
            replaceFragment(fragment);
        }
        // for logout option
        if (id == R.id.logout) {
            SharedPrefManager.getInstance(this).logout();
            finish();
        }
        return true;
    }

    public Patient extractPatient(){
        //  create a new instance of the Home fragment to use the get method of the patient info
        HomeFragment homeFragment = new HomeFragment();
        return homeFragment.getPatient();
    }

    // Method for Fragment infalation into the nav_host_fragment frame f
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment,fragment);
        fragmentTransaction.commit();

    }

    private void showCalendarPopup(OnDateSelectedListener listener) {
        // Create an instance of AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the layout using Data Binding
        DialogCalenderViewBinding binding = DialogCalenderViewBinding.inflate(LayoutInflater.from(this));

        // Set the custom view of the AlertDialog
        builder.setView(binding.getRoot());

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        // Set the CalendarView's date change listener
        binding.calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Month is zero-based in Java Calendar, so add 1
            String formattedDate = String.format(Locale.getDefault(), "%s %02d", new SimpleDateFormat("MMMM", Locale.getDefault()).format(new Date(year, month, dayOfMonth)), dayOfMonth);

            // Pass the formatted date to the listener
            listener.onDateSelected(formattedDate);

            // Dismiss the dialog
            dialog.dismiss();
        });

        // Show the AlertDialog
        dialog.show();
    }

    // Define an interface for the callback
    interface OnDateSelectedListener {
        void onDateSelected(String date);
    }


}