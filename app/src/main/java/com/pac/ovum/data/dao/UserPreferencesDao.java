package com.pac.ovum.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.pac.ovum.data.models.UserPreferences;

@Dao
public interface UserPreferencesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPreferences(UserPreferences preferences);

    @Query("SELECT * FROM UserPreferences WHERE userId = :userId")
    LiveData<UserPreferences> getPreferencesByUserId(int userId);

    @Update
    void updatePreferences(UserPreferences preferences);

    @Delete
    void deletePreferences(UserPreferences preferences);
}
