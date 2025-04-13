package com.pac.ovum.data.database;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
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

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
@Database(
        entities = {User.class, CycleData.class, Event.class, Episode.class, UserPreferences.class},
        version = 2,
        autoMigrations = {
                @AutoMigration(from = 1, to = 2)
        }
)
@TypeConverters({LocalDateTimeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;
    
    public abstract UserDao userDao();
    public abstract CycleDao cycleDao();
    public abstract EventDao eventDao();
    public abstract EpisodeDao episodeDao();
    public abstract UserPreferencesDao userPreferencesDao();
    
    /**
     * Inserts a user and cycle in a single transaction to prevent foreign key violations
     * @param user The user to insert
     * @param cycle The cycle to insert (user ID will be set automatically)
     * @return The ID of the newly inserted user, or -1 if the operation failed
     */
    @androidx.room.Transaction
    public int insertUserWithCycle(User user, CycleData cycle) {
        try {
            android.util.Log.d("AppDatabase", "Starting transaction to insert user and cycle");
            
            // Force commit any pending transactions first
            try {
                // This is a no-op query that forces Room to handle any pending transactions
                int count = userDao().isEmailTaken("force_commit_dummy_" + System.currentTimeMillis());
                android.util.Log.d("AppDatabase", "Forced commit of pending transactions, count=" + count);
            } catch (Exception e) {
                // Ignore, this is just to ensure a clean state
            }
            
            // Insert the user
            android.util.Log.d("AppDatabase", "Inserting user: " + user.getUserName() + ", " + user.getEmail());
            userDao().insertUser(user);
            
            // Verify insertion worked by querying DB
            android.util.Log.d("AppDatabase", "Verifying user insertion with email: " + user.getEmail());
            int emailCount = userDao().isEmailTaken(user.getEmail());
            if (emailCount == 0) {
                android.util.Log.e("AppDatabase", "Critical error: User insertion did not commit! Email not found in database.");
                return -1;
            }
            
            // Get the last inserted user ID
            android.util.Log.d("AppDatabase", "Fetching all users to find the one we just inserted");
            List<User> users = userDao().getAllUsersSync();
            if (users.isEmpty()) {
                android.util.Log.e("AppDatabase", "Failed to get users - list is empty after insertion!");
                return -1;
            }
            
            int userId = -1;
            for (User u : users) {
                if (u.getEmail().equals(user.getEmail())) {
                    userId = u.getUserId();
                    break;
                }
            }
            
            if (userId == -1) {
                userId = users.get(users.size() - 1).getUserId();
            }
            
            android.util.Log.d("AppDatabase", "Found user ID: " + userId);
            
            // Set user ID on cycle and insert it
            cycle.setUserId(userId);
            android.util.Log.d("AppDatabase", "Inserting cycle with userId: " + userId + ", cycleLength: " + cycle.getCycleLength());
            long cycleId = cycleDao().insertCycle(cycle);
            
            if (cycleId != -1) {
                android.util.Log.d("AppDatabase", "Transaction completed successfully. User ID: " + userId + ", Cycle ID: " + cycleId);
                return userId;
            } else {
                android.util.Log.e("AppDatabase", "Cycle insertion failed - returned invalid ID");
                return -1;
            }
        } catch (Exception e) {
            android.util.Log.e("AppDatabase", "Transaction failed with exception: " + e.getMessage(), e);
            e.printStackTrace();
            return -1;
        }
    }
    
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "ovum_database")
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

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
