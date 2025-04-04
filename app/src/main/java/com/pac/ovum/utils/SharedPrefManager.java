package com.pac.ovum.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String PREF_NAME = "OvumPrefs";
    private static final String KEY_USER_ID = "userId";
    private static SharedPrefManager instance;
    private SharedPreferences sharedPreferences;

    private SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveUserId(int userId) {
        sharedPreferences.edit().putInt(KEY_USER_ID, userId).apply();
    }

    public int getUserId() {
        // Return a default user ID if not found (you might want to handle this differently)
        return sharedPreferences.getInt(KEY_USER_ID, 1);
    }

    public void clearUserData() {
        sharedPreferences.edit().clear().apply();
    }
} 