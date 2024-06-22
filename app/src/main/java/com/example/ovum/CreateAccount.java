package com.example.ovum;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.ovum.databinding.ActivityCreateAccountBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class CreateAccount extends AppCompatActivity { // implements DatePickerFragment.OnDateSelectedListener

    private ActivityCreateAccountBinding binding;

    // Declare the color for the hint string of the field
    private int paleHintColor = R.color.pale_hint_color;

    String strUsername;
    String strEmail;
    String strPassword;
    String strPhoneNumber;
    String strDob;
    Button signupBt;
    TextView loginAlt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Binding the layout
        binding = ActivityCreateAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize views
        initViews();

        // Set up listeners
        setupListeners();
    }

    private void initViews() {
        strUsername = "";
        strEmail = "";
        strPassword = "";
        strPhoneNumber = "";
        strDob = "";
        signupBt = binding.signUp;
        loginAlt = binding.loginAlt;
    }

    private void setupListeners() {
        signupBt.setOnClickListener(v -> createAccountMethod());

        loginAlt.setOnClickListener(v -> goToLogin());

        binding.DOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(binding.DOB.getId());
            }
        });

        // Set up hint behavior for input fields
        setHintBehavior(binding.name);
        setHintBehavior(binding.email);
        setHintBehavior(binding.password);
        setHintBehavior(binding.phoneno);
        setHintBehavior(binding.DOB);
    }

    private void createAccountMethod() {
        // Get the string values from the views and trim them
        String strUsername = binding.name.getText().toString().trim();
        String strEmail = binding.email.getText().toString().trim();
        String strPassword = binding.password.getText().toString().trim();
        String strPhoneNumber = binding.phoneno.getText().toString().trim();
        String strDob = binding.DOB.getText().toString().trim();

        // Check if any of the trimmed strings are empty after trimming
        if (Objects.requireNonNull(strUsername, "Username cannot be empty").isEmpty() ||
                Objects.requireNonNull(strEmail, "Email cannot be empty").isEmpty() ||
                Objects.requireNonNull(strPassword, "Password cannot be empty").isEmpty() ||
                Objects.requireNonNull(strPhoneNumber, "Phone number cannot be empty").isEmpty() ||
                Objects.requireNonNull(strDob, "Date of birth cannot be empty").isEmpty()) {
            // Show an error message to the user indicating that they must fill in all fields
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else {
            // Create a new Intent
            Intent intent;
            intent = new Intent(CreateAccount.this, UserIntention.class);

            // Put the string values as extras in the Intent
            intent.putExtra("username", strUsername);
            intent.putExtra("email", strEmail);
            intent.putExtra("password", strPassword);
            intent.putExtra("phone_number", strPhoneNumber);
            intent.putExtra("DOB", strDob);

            // Start the new activity
            startActivity(intent);

            // Finish the current activity (optional)
            finish();
        }
    }


    private void setHintBehavior(TextInputEditText editText) {
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // Hide hint when focused
                editText.setHint("");
            } else {
                // Show hint when lost focus
                editText.setHint(getString(R.string.hint_placeholder));
                editText.setHintTextColor(ContextCompat.getColor(this, paleHintColor));
            }
        });
    }

    private void goToLogin() {
        Intent intent = new Intent(CreateAccount.this, Login.class);
        startActivity(intent);
        finish();
    }

    private void showDatePickerDialog(int editTextId) {
//        DatePickerFragment newFragment = DatePickerFragment.newInstance(editTextId);
//        newFragment.show(getSupportFragmentManager(), "datePicker");
        // lets just do nothing only set the text of what has been entered
    }

//    @Override
//    public void onDateSelected(int year, int month, int dayOfMonth) {
//        EditText editText = findViewById(getSupportFragmentManager().findFragmentByTag("datePicker").getArguments().getInt("edit_text_id"));
//        if (editText != null) {
//            editText.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
//        }
//    }


}
