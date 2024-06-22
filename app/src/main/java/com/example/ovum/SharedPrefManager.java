package com.example.ovum;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    private static final String KEY_DUE_DATE = "DueDate";
    // Singleton instance of SharedPrefManager
    private static SharedPrefManager mInstance;
    // Application context
    private static Context mCtx;

    // Name of the SharedPreferences file
    private static final String SHARED_PREF_NAME = "MyPrefs";
    // Keys for storing user data
    private static final String KEY_USERNAME = "name";
    private static final String KEY_USER_EMAIL = "useremail";
    private static final String KEY_USER_ID = "userid";

    // Private constructor to prevent direct instantiation
    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    // Method to get the singleton instance of SharedPrefManager
    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    // Method to store user login details in SharedPreferences
    public boolean userLogin(int id, String username, String email) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Store user ID, email, username
        editor.putInt(KEY_USER_ID, id);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USERNAME, username);

        // Apply changes
        editor.apply();
        return true;
    }
    // Method to store other user patient info like due date in SharedPreferences
    public void storePatientInfo(String dueDate) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Store user due date
        editor.putString(KEY_DUE_DATE, dueDate);

        // Apply changes
        editor.apply();
    }

    // Method to check if the user is logged in
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        // Check if the username is stored in SharedPreferences
        return sharedPreferences.getString(KEY_USERNAME, null) != null;
    }

    // Method to log out the user by clearing the stored data
    public boolean logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Clear all stored data
        editor.clear();
        editor.apply();
        return true;
    }

    // Method to get the stored username
    public String getUsername() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    // Method to get the stored user email
    public String getUserEmail() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    // Method to get the stored user ID
    public int getUserId() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_USER_ID, 0);
    }
    public String getDueDate() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_DUE_DATE, null);
    }
}
