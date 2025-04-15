package com.pac.ovum.utils;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.Room;

import com.pac.ovum.data.database.AppDatabase;
import com.pac.ovum.data.repositories.CycleRepository;
import com.pac.ovum.data.repositories.EpisodeRepository;
import com.pac.ovum.data.repositories.EventRepository;
import com.pac.ovum.data.repositories.UserPreferencesRepository;
import com.pac.ovum.data.repositories.UserRepository;

/**
 * AppModule is a utility class that provides methods for creating and managing
 * application-wide dependencies, such as the Room database and various repositories.
 * This class implements manual dependency injection to facilitate easier testing
 * and maintainability of the application.
 */
public class AppModule {
    private static AppDatabase appDatabase;
    private static final String DB_NAME = "ovum.db";

    /**
     * Provides a singleton instance of the AppDatabase.
     *
     * @param context The application context used to create the database instance.
     * @return An instance of AppDatabase.
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static AppDatabase provideAppDatabase(Context context) {
        // 1. Get the truly global application context
        Context appCtx = context.getApplicationContext();

        // 2. Double‑checked locking for thread safety
        if (appDatabase == null) {
            synchronized (AppModule.class) {
                if (appDatabase == null) {
                    appDatabase = Room.databaseBuilder(
                                    appCtx,                // <-- application context only
                                    AppDatabase.class,
                                    DB_NAME                // <-- single source of truth
                            )
                            // .addMigrations(...)      // re‑enable when you have migrations
                            .build();
                }
            }
        }
        return appDatabase;
    }


    /**
     * Provides an instance of UserRepository.
     *
     * @param context The application context used to access the database.
     * @return An instance of UserRepository.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static UserRepository provideUserRepository(Context context) {
        return new UserRepository(provideAppDatabase(context).userDao());
    }

    /**
     * Provides an instance of CycleRepository.
     *
     * @param context The application context used to access the database.
     * @return An instance of CycleRepository.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static CycleRepository provideCycleRepository(Context context) {
        AppDatabase db = provideAppDatabase(context);
        return new CycleRepository(db.cycleDao(), db.episodeDao());
    }

    /**
     * Provides an instance of EpisodeRepository.
     *
     * @param context The application context used to access the database.
     * @return An instance of EpisodeRepository.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static EpisodeRepository provideEpisodeRepository(Context context) {
        return new EpisodeRepository(provideAppDatabase(context).episodeDao());
    }

    /**
     * Provides an instance of EventRepository.
     *
     * @param context The application context used to access the database.
     * @return An instance of EventRepository.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static EventRepository provideEventRepository(Context context) {
        return new EventRepository(provideAppDatabase(context).eventDao());
    }

    /**
     * Provides an instance of UserPreferencesRepository.
     *
     * @param context The application context used to access the database.
     * @return An instance of UserPreferencesRepository.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static UserPreferencesRepository provideUserPreferencesRepository(Context context) {
        return new UserPreferencesRepository(provideAppDatabase(context).userPreferencesDao());
    }
} 