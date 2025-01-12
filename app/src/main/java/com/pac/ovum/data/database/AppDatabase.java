package com.pac.ovum.data.database;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

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
//        autoMigrations = {
//                @AutoMigration(from = 1, to = 2)
//        }
)
@TypeConverters({LocalDateTimeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract CycleDao cycleDao();
    public abstract EventDao eventDao();
    public abstract EpisodeDao episodeDao();
    public abstract UserPreferencesDao userPreferencesDao();

    // Updated migration to include the new column
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Create a new table with the new schema
            database.execSQL("CREATE TABLE Event_new ("
                    + "eventId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + "cycleId INTEGER NOT NULL, "
                    + "eventType TEXT, "
                    + "eventDate DATE, "
                    + "description TEXT, "
                    + "eventTime TIME, " // New column added
                    + "FOREIGN KEY(cycleId) REFERENCES CycleData(cycleId) ON DELETE CASCADE)");

            // Copy the data from the old table to the new table
            database.execSQL("INSERT INTO Event_new (eventId, cycleId, eventType, eventDate, description) "
                    + "SELECT eventId, cycleId, eventType, eventDate, description FROM Event");

            // Remove the old table
            database.execSQL("DROP TABLE Event");

            // Rename the new table to the old table's name
            database.execSQL("ALTER TABLE Event_new RENAME TO Event");
        }
    };
}
