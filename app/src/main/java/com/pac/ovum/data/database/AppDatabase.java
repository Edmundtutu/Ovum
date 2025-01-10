package com.pac.ovum.data.database;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.pac.ovum.data.dao.CycleDao;
import com.pac.ovum.data.dao.EpisodeDao;
import com.pac.ovum.data.dao.EventDao;
import com.pac.ovum.data.dao.UserDao;
import com.pac.ovum.data.dao.UserPreferencesDao;
import com.pac.ovum.data.models.CycleData;
import com.pac.ovum.data.models.Episode;
import com.pac.ovum.data.models.Event;
import com.pac.ovum.data.models.User;
import com.pac.ovum.data.models.UserPreferences;
import com.pac.ovum.utils.LocalDateTimeConverter;

@RequiresApi(api = Build.VERSION_CODES.O)
@Database(
        entities = {User.class, CycleData.class, Event.class, Episode.class, UserPreferences.class},
        version = 1
)
@TypeConverters({LocalDateTimeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract CycleDao cycleDao();
    public abstract EventDao eventDao();
    public abstract EpisodeDao episodeDao();
    public abstract UserPreferencesDao userPreferencesDao();
}
