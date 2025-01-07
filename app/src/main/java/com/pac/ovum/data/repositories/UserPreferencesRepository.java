package com.pac.ovum.data.repositories;

import androidx.lifecycle.LiveData;

import com.pac.ovum.data.dao.UserPreferencesDao;
import com.pac.ovum.data.models.UserPreferences;
import com.pac.ovum.utils.AppExecutors;

public class UserPreferencesRepository {
    private final UserPreferencesDao userPreferencesDao;

    public UserPreferencesRepository(UserPreferencesDao userPreferencesDao) {
        this.userPreferencesDao = userPreferencesDao;
    }

    public void insertPreferences(UserPreferences preferences) {
        AppExecutors.getInstance().diskIO().execute(() -> userPreferencesDao.insertPreferences(preferences));
    }

    public LiveData<UserPreferences> getPreferencesByUserId(int userId) {
        return userPreferencesDao.getPreferencesByUserId(userId);
    }

    public void updatePreferences(UserPreferences preferences) {
        AppExecutors.getInstance().diskIO().execute(() -> userPreferencesDao.updatePreferences(preferences));
    }

    public void deletePreferences(UserPreferences preferences) {
        AppExecutors.getInstance().diskIO().execute(() -> userPreferencesDao.deletePreferences(preferences));
    }
}
