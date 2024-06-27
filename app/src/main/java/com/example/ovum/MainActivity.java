package com.example.ovum;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

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
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int MENU_HOME = R.id.navigation_home;
    private static final int MENU_CYCLES = R.id.navigation_more;
    private static final int MENU_TCALENDAR = R.id.navigation_Testcalendar;

    private Patient patient;

    // for the profile details and views
    private FrameLayout fragmentContainer;
    private View dimOverlay;
    private boolean isFragmentVisible = false;
    String profileDob = null;
    String profileDueDate = null;
    String profileUsername = null;
    String profileEmail = null;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AndroidThreeTen.init(this);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the toolbar
        setSupportActionBar(binding.toolBar);

        // seting the username to the textview
        String username = SharedPrefManager.getInstance(this).getUsername();
        binding.toolBar.setTitle("Hi "+username);

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

        // in case of the profile fragment
        fragmentContainer = findViewById(R.id.fragmentContainer);
        dimOverlay = findViewById(R.id.dimOverlay);

        // Initially hide the fragment
        fragmentContainer.setVisibility(View.GONE);
        dimOverlay.setVisibility(View.GONE);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
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

            // show the profile layout fragment
            showFragment();
        }
        // for logout option
        if (id == R.id.logout) {
            SharedPrefManager.getInstance(this).logout();
            finish();
        }


        // for the Calender option
        if(id == R.id.calendericon){
            // show the calender popup
            showCalendarPopup(new OnDateSelectedListener() {
                @Override
                public void onDateSelected(String date) {
                    // Handle the selected date by logging it or view symptoms of the selected date :: same like what is done by the calender days in the calender fragment
                    // we shall implement more of the change of the interface to match the selected date
                }
            });
        }
        return true;
    }

    private void hideFragment() {
        if (isFragmentVisible) {
            getSupportFragmentManager().beginTransaction().remove(Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer))).commit();
            fragmentContainer.setVisibility(View.GONE);
            dimOverlay.setVisibility(View.GONE);
            isFragmentVisible = false;
        }
    }

    private void showFragment() {
        // create a new instance of a profile fragment and pass the profile details to it
        ProfileFragment fragment = ProfileFragment.newInstance(profileDob, profileDueDate, profileUsername, profileEmail);

        if (!isFragmentVisible) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
            fragmentContainer.setVisibility(View.VISIBLE);
            dimOverlay.setVisibility(View.VISIBLE);
            // set an onclick listener for the transparent view
            dimOverlay.setOnClickListener(v -> {hideFragment();});
            // animating the fragment to slide in from the right
            fragmentContainer.animate()
                    .translationX(0)
                    .setDuration(300)
                    .withStartAction(() -> {
                        ViewGroup.LayoutParams params = fragmentContainer.getLayoutParams();
                        params.width = getResources().getDisplayMetrics().widthPixels;
                        fragmentContainer.setLayoutParams(params);
                    })
                    .start();
            isFragmentVisible = true;
        }
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
    public void onBackPressed() {
        if (isFragmentVisible) {
            hideFragment();
        } else {
            super.onBackPressed();
        }
    }

}