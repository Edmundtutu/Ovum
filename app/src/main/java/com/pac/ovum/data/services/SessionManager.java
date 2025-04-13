package com.pac.ovum.data.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.pac.ovum.data.models.User;
import com.pac.ovum.utils.SharedPrefManager;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Manages user session and persistent login functionality
 * Uses EncryptedSharedPreferences for secure storage of sensitive data
 */
public class SessionManager {
    private static final String TAG = "SessionManager";
    private static final String PREF_NAME = "OvumUserSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_NAME = "userName";
    
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    
    public SessionManager(Context context) {
        this.context = context;
        try {
            // Create or retrieve the Master Key for encryption/decryption
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
                    
            // Initialize/open an instance of EncryptedSharedPreferences
            pref = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            
            editor = pref.edit();
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "Error initializing encrypted preferences", e);
            // Fallback to regular SharedPreferences if encryption fails
            pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            editor = pref.edit();
        }
    }
    
    /**
     * Save user login session
     */
    public void createLoginSession(User user) {
        if (user == null) {
            Log.e(TAG, "Cannot create session for null user");
            return;
        }
        
        if (user.getUserId() <= 0) {
            Log.e(TAG, "Cannot create session for user with invalid ID: " + user.getUserId());
            return;
        }
        
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, user.getUserId());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_NAME, user.getUserName());

        // Also add user ID to SharedPrefManager for easy access
        SharedPrefManager.getInstance(context).saveUserId(user.getUserId());
        
        // Force commit instead of apply to ensure data is written immediately
        boolean success = editor.commit();
        
        if (success) {
            Log.d(TAG, "User login session created for: " + user.getEmail() + " with ID: " + user.getUserId());
        } else {
            Log.e(TAG, "Failed to save session data for user: " + user.getEmail());
        }
    }
    
    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    /**
     * Get stored user ID
     */
    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }
    
    /**
     * Get stored user email
     */
    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, null);
    }
    
    /**
     * Get stored username
     */
    public String getUserName() {
        return pref.getString(KEY_USER_NAME, null);
    }
    
    /**
     * Clear session and log out user
     */
    public void logoutUser() {
        editor.clear();
        editor.apply();
        // Clear user ID from SharedPrefManager
        SharedPrefManager.getInstance(context).clearUserData();
        Log.d(TAG, "User logged out, session cleared");
    }
} 