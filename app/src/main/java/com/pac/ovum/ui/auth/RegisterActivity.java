package com.pac.ovum.ui.auth;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pac.ovum.MainActivity;
import com.pac.ovum.R;
import com.pac.ovum.data.models.CycleData;
import com.pac.ovum.data.models.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

@RequiresApi(api = Build.VERSION_CODES.O)
public class RegisterActivity extends AppCompatActivity {

    private static final int CYCLE_DATA_REQUEST_CODE = 1001;
    private AuthViewModel viewModel;
    private TextInputLayout nameLayout, emailLayout, passwordLayout, dobLayout;
    private TextInputEditText etName, etEmail, etPassword, etDob;
    private RadioGroup radioGroupGender;
    private Button btnRegister,btnSetupCycle;
    private TextView tvLogin, tvError, tvCycleStatus;
    private ProgressBar progressBar;
    private ImageView ivBack;

    private LocalDate selectedDate = null;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    // Cycle data
    private LocalDate cycleStartDate = null;
    private LocalDate cycleEndDate = null;
    private int cycleLength = 28; // Default
    private int periodLength = 5; // Default
    private LocalDate ovulationDate = null;
    private LocalDate fertileStartDate = null;
    private LocalDate fertileEndDate = null;
    private boolean cycleDataCollected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Initialize views
        nameLayout = findViewById(R.id.nameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        dobLayout = findViewById(R.id.dobLayout);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etDob = findViewById(R.id.etDob);
        radioGroupGender = findViewById(R.id.radioGroupGender); // TODO: This will function when we have included Features for the male gender as well
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        tvError = findViewById(R.id.tvError);
        progressBar = findViewById(R.id.progressBar);
        ivBack = findViewById(R.id.ivBack);
        btnSetupCycle = findViewById(R.id.btnSetupCycle);
        tvCycleStatus = findViewById(R.id.tvCycleStatus);

        // Initially hide register button and show setup cycle button
        btnRegister.setVisibility(View.GONE);
        btnSetupCycle.setVisibility(View.VISIBLE);
        tvCycleStatus.setVisibility(View.GONE);

        // Set up text watchers to clear errors as the user types
        setupTextWatchers();

        // Set up click listeners
        setupClickListeners();

        // Set up date picker
        setupDatePicker();

        // Observe LiveData
        observeViewModel();
    }

    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear any error messages
                nameLayout.setError(null);
                emailLayout.setError(null);
                passwordLayout.setError(null);
                dobLayout.setError(null);
                tvError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        etName.addTextChangedListener(textWatcher);
        etEmail.addTextChangedListener(textWatcher);
        etPassword.addTextChangedListener(textWatcher);
        etDob.addTextChangedListener(textWatcher);
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> attemptRegistration());

        btnSetupCycle.setOnClickListener(v -> {
            // Launch the InitialCyclesActivity to collect cycle data
            Intent intent = new Intent(RegisterActivity.this, InitialCyclesActivity.class);
            startActivityForResult(intent, CYCLE_DATA_REQUEST_CODE);
        });

        tvLogin.setOnClickListener(v -> {
            finish(); // Return to login activity
        });

        ivBack.setOnClickListener(v -> {
            finish(); // Return to previous screen
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupDatePicker() {
        etDob.setOnClickListener(v -> {
            // Get current date
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create date picker dialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    RegisterActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Update the EditText with the selected date
                        selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay);
                        etDob.setText(selectedDate.format(formatter));
                    },
                    year, month, day);

            // Set the maximum date to today (no future dates allowed)
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            
            // Show date picker dialog
            datePickerDialog.show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CYCLE_DATA_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            // Retrieve cycle data from result
            String startDateStr = data.getStringExtra("START_DATE");
            String endDateStr = data.getStringExtra("END_DATE");
            String ovulationDateStr = data.getStringExtra("OVULATION_DATE");
            String fertileStartStr = data.getStringExtra("FERTILE_START");
            String fertileEndStr = data.getStringExtra("FERTILE_END");

            cycleLength = data.getIntExtra("CYCLE_LENGTH", 28);
            periodLength = data.getIntExtra("PERIOD_LENGTH", 5);

            if (startDateStr != null) cycleStartDate = LocalDate.parse(startDateStr);
            if (endDateStr != null) cycleEndDate = LocalDate.parse(endDateStr);
            if (ovulationDateStr != null) ovulationDate = LocalDate.parse(ovulationDateStr);
            if (fertileStartStr != null) fertileStartDate = LocalDate.parse(fertileStartStr);
            if (fertileEndStr != null) fertileEndDate = LocalDate.parse(fertileEndStr);

            cycleDataCollected = true;
            updateCycleStatusText();
            
            // Show register button and hide setup cycle button
            btnRegister.setVisibility(View.VISIBLE);
            btnSetupCycle.setVisibility(View.GONE);
            tvCycleStatus.setVisibility(View.VISIBLE);
        }
    }

    private void updateCycleStatusText() {
        if (cycleDataCollected && tvCycleStatus != null) {
            tvCycleStatus.setText("Cycle data collected ✓\n" +
                    "Cycle Length: " + cycleLength + " days\n" +
                    "Period Length: " + periodLength + " days");
            tvCycleStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else if (tvCycleStatus != null) {
            tvCycleStatus.setText("Please set up your cycle information");
            tvCycleStatus.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }
    }

    private void attemptRegistration() {
        // for debuing first log that the button has beein clicke and is attempting to register
        Toast.makeText(this, "Attempting to register...", Toast.LENGTH_SHORT).show();
        // Get input values
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Get selected gender
        String gender = "Female"; // We shall adjust this when need arises to cater for other genders
        /**
            String gender;
            int selectedId = radioGroupGender.getCheckedRadioButtonId();
            if (selectedId == R.id.rbFemale) {
                gender = "Female";
            } else (selectedId == R.id.rbMale) {
                gender = "Male";
            }
        */

        // Validate inputs
        boolean isValid = true;

        if (name.isEmpty()) {
            nameLayout.setError("Name cannot be empty");
            isValid = false;
        }

        if (email.isEmpty()) {
            emailLayout.setError("Email cannot be empty");
            isValid = false;
        }

        if (password.isEmpty()) {
            passwordLayout.setError("Password cannot be empty");
            isValid = false;
        } else if (password.length() < 6) {
            passwordLayout.setError("Password must be at least 6 characters");
            isValid = false;
        }

        if (selectedDate == null) {
            dobLayout.setError("Please select your date of birth");
            isValid = false;
        }
        if (!cycleDataCollected) {
            Toast.makeText(this, "Please set up your cycle information", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (isValid) {
            // Log the registration attempt
            Toast.makeText(this, "Registering...", Toast.LENGTH_SHORT).show();
            // Show progress and attempt registration
            progressBar.setVisibility(View.VISIBLE);
            tvError.setVisibility(View.GONE); // Hide any previous errors

            if (cycleDataCollected) {
                CycleData userCycle = getCycleData();

                // Register user with cycle data
                // Since registration is async, we let the LiveData observers handle UI updates
                viewModel.registerWithCycle(name, email, password, selectedDate, gender, userCycle);
            } else {
                // Fallback to regular registration (though this shouldn't be reached due to validation)
                viewModel.register(name, email, password, selectedDate, gender);
            }
        }
    }

    @NonNull
    private CycleData getCycleData() {
        CycleData userCycle = new CycleData();
        userCycle.setStartDate(cycleStartDate);
        userCycle.setEndDate(cycleEndDate);
        userCycle.setCycleLength(cycleLength);
        userCycle.setPeriodLength(periodLength);
        userCycle.setOvulationDate(ovulationDate);
        userCycle.setFertileStart(fertileStartDate);
        userCycle.setFertileEnd(fertileEndDate);
        userCycle.setOngoing(true); // Current cycle is ongoing
        return userCycle;
    }

    private void observeViewModel() {
        // Observe current user
        viewModel.getCurrentUser().observe(this, user -> {
            progressBar.setVisibility(View.GONE);
            if (user != null) {
                // User registered and logged in successfully, navigate to main activity
                navigateToMainActivity(user);
            }
        });

        // Observe authentication errors
        viewModel.getAuthError().observe(this, error -> {
            progressBar.setVisibility(View.GONE);
            if (error != null && !error.isEmpty()) {
                tvError.setText(error);
                tvError.setVisibility(View.VISIBLE);
            } else {
                tvError.setVisibility(View.GONE);
            }
        });
    }

    private void navigateToMainActivity(User user) {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
} 