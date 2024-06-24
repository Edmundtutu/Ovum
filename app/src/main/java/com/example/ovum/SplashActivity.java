package com.example.ovum;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.ovum.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private final long splashDuration = 500; // 0.5 second
    private final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            binding = ActivitySplashBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this);

            // Delay using Handler to display the splash screen
            new Handler().postDelayed(() -> {
                if (sharedPrefManager.isFirstTime()) {
                    // Set the isFirstTime flag to false
                    sharedPrefManager.setFirstTime(false);

                    // Redirect to the introduction or onboarding activity
                    startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                } else {
                    if (sharedPrefManager.isLoggedOut()) {
                        // Reset the isLoggedOut flag to false
                        sharedPrefManager.setLoggedOut(false);

                        // print that the user has logged out
                        Log.v(TAG, "User has logged out");

                        // Redirect to the login activity
                        startActivity(new Intent(SplashActivity.this, Login.class));
                    } else {
                        if (sharedPrefManager.hasCreatedAccount()) {
                            Log.v(TAG, "User has created an account");
                        }

                        if (sharedPrefManager.isLoggedIn() || sharedPrefManager.hasCreatedAccount()) {
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        } else {
                            Log.v(TAG, "User has not logged in");
                            startActivity(new Intent(SplashActivity.this, Login.class));
                        }
                    }
                }

                // Close the splash activity
                finish();
            }, splashDuration);
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }
}
