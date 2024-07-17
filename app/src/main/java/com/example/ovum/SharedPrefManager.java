package com.example.ovum;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.ParseException;

public class SharedPrefManager {

    // Singleton instance of SharedPrefManager
    private static SharedPrefManager mInstance;
    // Application context
    private static Context mCtx;

    // Name of the SharedPreferences file
    private static final String SHARED_PREF_NAME = "MyPrefs";
    // Keys for storing user data and state
    private static final String KEY_USERNAME = "name";
    private static final String KEY_USER_EMAIL = "useremail";
    private static final String KEY_USER_ID = "userid";
    private static final String KEY_DUE_DATE = "DueDate";
    private static final String KEY_DATE_RECORDED = "DueRecorded";
    private static final String KEY_DATE_OCCURRED = "DateOccurred";
    private static final String KEY_IS_FIRST_TIME = "isFirstTime";
    private static final String KEY_HAS_CREATED_ACCOUNT = "hasCreatedAccount";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_IS_LOGGED_OUT = "isLoggedOut";

    // Private constructor to prevent direct instantiation
    private SharedPrefManager(Context context) {
        mCtx = context.getApplicationContext();
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
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putBoolean(KEY_IS_LOGGED_OUT, false);

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
        // Check if the user is logged in
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Method to log out the user by clearing the logged-in state
    public boolean logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Clear logged-in state
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.putBoolean(KEY_IS_LOGGED_OUT, true);
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

    // Method to get the stored due date
    public DueDate getDueDate() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String dueDateString = sharedPreferences.getString(KEY_DUE_DATE, null);

        // how about returning the date in two fomarts i.e. in the format of 13-7-2024 and 13th July 2024 (reported and speech)

        if (dueDateString == null) {
            return null; // or handle the null case as needed
        }

        try {
            return new DueDate(dueDateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // or handle the parse exception as needed
        }
    }

    // Method to check if it's the first time the app is launched
    public boolean isFirstTime() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_IS_FIRST_TIME, true);
    }

    // Method to set the first time flag to false
    public void setFirstTime(boolean isFirstTime) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_FIRST_TIME, isFirstTime);
        editor.apply();
    }

    // Method to check if the user has created an account
    public boolean hasCreatedAccount() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_HAS_CREATED_ACCOUNT, false);
    }

    // Method to set the account creation flag
    public void setHasCreatedAccount(boolean hasCreatedAccount) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_HAS_CREATED_ACCOUNT, hasCreatedAccount);
        editor.apply();
    }

    // Method to check if the user has logged out
    public boolean isLoggedOut() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_OUT, false);
    }

    // Method to set the logged out flag
    public void setLoggedOut(boolean isLoggedOut) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_OUT, isLoggedOut);
        editor.apply();
    }
}
