package com.pac.ovum.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.pac.ovum.MainActivity;
import com.pac.ovum.R;

/**
 * Splash screen activity that checks if a user is already logged in
 * and redirects them to the appropriate screen
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_TIME = 1500; // 1.5 seconds
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Wait for a moment to display the splash screen
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Check if user is logged in
            if (viewModel.isUserLoggedIn()) {
                // User is logged in, observe for user data to be loaded
                viewModel.getCurrentUser().observe(this, user -> {
                    if (user != null) {
                        // Navigate to main screen
                        navigateToMainActivity();
                    } else {
                        // If user data loading failed, go to login screen
                        navigateToLoginActivity();
                    }
                });
            } else {
                // User is not logged in, go to login screen
                navigateToLoginActivity();
            }
        }, SPLASH_DISPLAY_TIME);
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
} 