package com.example.ovum;

import static com.example.ovum.OvumContract.PatientEntry.COLUMN_AVERAGE_CYCLE_LENGTH;
import static com.example.ovum.OvumContract.PatientEntry.COLUMN_AVERAGE_PERIOD_LENGTH;
import static com.example.ovum.OvumContract.PatientEntry.COLUMN_CYCLE_LENGTH;
import static com.example.ovum.OvumContract.PatientEntry.COLUMN_DOB;
import static com.example.ovum.OvumContract.PatientEntry.COLUMN_EMAIL;
import static com.example.ovum.OvumContract.PatientEntry.COLUMN_LAST_PERIOD_DATE;
import static com.example.ovum.OvumContract.PatientEntry.COLUMN_LOCATION;
import static com.example.ovum.OvumContract.PatientEntry.COLUMN_NAME;
import static com.example.ovum.OvumContract.PatientEntry.COLUMN_NEXT_PROBABLE_DATE_OF_PERIOD;
import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ovum.databinding.ActivityMainBinding;
import com.example.ovum.databinding.DialogCalenderViewBinding;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.text.ParseException;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int MENU_HOME = R.id.navigation_home;
    private static final int MENU_CYCLES = R.id.navigation_more;
    private static final int MENU_TCALENDAR = R.id.navigation_Testcalendar;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;

    private Patient patient;

    // for the profile details and views
    String profileDob = null;
    String profileDueDate = null;
    String profileUsername = null;
    String profileEmail = null;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // set the background of the bottom navigation to null
        binding.navView.setBackground(null);

        // Initialize the toolbar
        setSupportActionBar(binding.toolBar);

        // setting the username to the textView
        String username = SharedPrefManager.getInstance(this).getUsername();
        binding.toolBar.setTitle("Hi " + username);

        drawerLayout = binding.drawerLayout;
        navigationView = binding.navViewDrawer;

        toggle = new ActionBarDrawerToggle(this, drawerLayout, binding.toolBar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        // handling the extraction of Patien data to be displayed in the navigation view
        // from the shared refs get the Id and email and use them to invoke the patient DOB....
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this);
        int userId = sharedPrefManager.getUserId();
        profileEmail = sharedPrefManager.getUserEmail();

        // initialise a new instance of the Database helper
        OvumDbHelper db = new OvumDbHelper(this);
        patient = extractRelevantInfoFromDb(userId,profileEmail,db);
        if (patient != null) {
            profileDob = patient.getDob();
            profileDueDate = sharedPrefManager.getDueDate();
            profileUsername = sharedPrefManager.getUsername();
            Log.v("Profile", "DOB: " + profileDob + " DueDate: " + profileDueDate + " Username: " + profileUsername + " Email: " + profileEmail);
        }
        // instantiate the date utils class to make age and date conversions
        DateUtils dateUtils = new DateUtils();
        String age = dateUtils.calculateAge(profileDob);
        setNavigationViewItemTitle(R.id.nav_username, profileEmail);
        setNavigationViewItemTitle(R.id.nav_age, "Aged: "+age);
        setNavigationViewItemTitle(R.id.nav_duedate, "Due Date: "+dateUtils.formatDateToSpeech(profileDueDate));

        // set the HomeFragment as the first frame
        replaceFragment(new HomeFragment());

        binding.navView.getMenu().getItem(3).setEnabled(false);

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

        // the floatingButton
        binding.fab.setOnClickListener(view -> {
            // show Custom dialog
            ShowPayDialog dialog = new ShowPayDialog(this);
            dialog.show(getSupportFragmentManager(), "ShowPayDialog");
        });
    }

    @Override
    @SuppressLint("NonConstantResourceId")
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new HomeFragment()).commit();
        }
        if(itemId == R.id.nav_settings) {
            Toast.makeText(this, "Settings will appear shortly!", Toast.LENGTH_SHORT).show();
        }
        if(itemId == R.id.nav_about) {
            Toast.makeText(this, "About Us will appear shortly!", Toast.LENGTH_SHORT).show();
        }
        if(itemId == R.id.nav_back) {
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (id == R.id.logout) {
            SharedPrefManager.getInstance(this).logout();
            finish();
        }

        if (id == R.id.calendericon) {
            showCalendarPopup(date -> {
                // Handle the selected date
            });
        }
        return super.onOptionsItemSelected(item);
    }


    // Method for Fragment inflation into the nav_host_fragment frame
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void showCalendarPopup(OnDateSelectedListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DialogCalenderViewBinding binding = DialogCalenderViewBinding.inflate(LayoutInflater.from(this));
        builder.setView(binding.getRoot());
        AlertDialog dialog = builder.create();
        binding.calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String formattedDate = String.format(Locale.getDefault(), "%s %02d", new SimpleDateFormat("MMMM", Locale.getDefault()).format(new Date(year, month, dayOfMonth)), dayOfMonth);
            listener.onDateSelected(formattedDate);
            dialog.dismiss();
        });
        dialog.show();
    }

    interface OnDateSelectedListener {
        void onDateSelected(String date);
    }

    public void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);
        TextView confirmButton = dialog.findViewById(R.id.confirmButton);
        TextView cancelButton = dialog.findViewById(R.id.cancelButton);
        confirmButton.setOnClickListener(v -> dialog.dismiss());
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public static class ShowPayDialog extends DialogFragment {
        private MainActivity mainActivity;

        public ShowPayDialog(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Alert Your Doctor about Today's Symptoms");
            builder.setMessage("You are required to pay for the service.");
            builder.setPositiveButton("Make payment", (dialog, which) -> {
                mainActivity.showBottomDialog();
                dismiss();
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dismiss());
            return builder.create();
        }
    }
    private void setNavigationViewItemTitle(int itemId, String title) {
        MenuItem item = navigationView.getMenu().findItem(itemId);
        if (item != null) {
            item.setTitle(title);
        }
    }

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
            Toast.makeText(this, "No patient data found", Toast.LENGTH_LONG).show();
        }
        // return the Patient Object Obtained from the database with all the Desired attributes
        return patient;
    }
}
