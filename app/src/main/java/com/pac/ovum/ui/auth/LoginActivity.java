package com.pac.ovum.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pac.ovum.MainActivity;
import com.pac.ovum.R;
import com.pac.ovum.data.models.User;

public class LoginActivity extends AppCompatActivity {

    private AuthViewModel viewModel;
    private TextInputLayout emailLayout, passwordLayout;
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword, tvError;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Initialize views
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvError = findViewById(R.id.tvError);
        progressBar = findViewById(R.id.progressBar);

        // Set up text watchers to clear errors as the user types
        setupTextWatchers();

        // Set up click listeners
        setupClickListeners();

        // Observe LiveData
        observeViewModel();
    }

    private void setupTextWatchers() {
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailLayout.setError(null);
                tvError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordLayout.setError(null);
                tvError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        tvForgotPassword.setOnClickListener(v -> {
            // Handle forgot password functionality
            Toast.makeText(this, "Forgot password functionality coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void attemptLogin() {
        // Get input values
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs
        boolean isValid = true;

        if (email.isEmpty()) {
            emailLayout.setError("Email cannot be empty");
            isValid = false;
        }

        if (password.isEmpty()) {
            passwordLayout.setError("Password cannot be empty");
            isValid = false;
        }

        if (isValid) {
            // Show progress and attempt login
            progressBar.setVisibility(View.VISIBLE);
            viewModel.login(email, password);
        }
    }

    private void observeViewModel() {
        // Observe current user
        viewModel.getCurrentUser().observe(this, user -> {
            progressBar.setVisibility(View.GONE);
            if (user != null) {
                // User logged in successfully, navigate to main activity
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
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
} 