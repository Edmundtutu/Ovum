package com.pac.ovum.utils;

import android.content.Context;

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

    /**
     * Provides a singleton instance of the AppDatabase.
     *
     * @param context The application context used to create the database instance.
     * @return An instance of AppDatabase.
     */
    public static AppDatabase provideAppDatabase(Context context) {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(context, AppDatabase.class, "ovum.db").build();
        }
        return appDatabase;
    }

    /**
     * Provides an instance of UserRepository.
     *
     * @param context The application context used to access the database.
     * @return An instance of UserRepository.
     */
    public static UserRepository provideUserRepository(Context context) {
        return new UserRepository(provideAppDatabase(context).userDao());
    }

    /**
     * Provides an instance of CycleRepository.
     *
     * @param context The application context used to access the database.
     * @return An instance of CycleRepository.
     */
    public static CycleRepository provideCycleRepository(Context context) {
        return new CycleRepository(provideAppDatabase(context).cycleDao());
    }

    /**
     * Provides an instance of EpisodeRepository.
     *
     * @param context The application context used to access the database.
     * @return An instance of EpisodeRepository.
     */
    public static EpisodeRepository provideEpisodeRepository(Context context) {
        return new EpisodeRepository(provideAppDatabase(context).episodeDao());
    }

    /**
     * Provides an instance of EventRepository.
     *
     * @param context The application context used to access the database.
     * @return An instance of EventRepository.
     */
    public static EventRepository provideEventRepository(Context context) {
        return new EventRepository(provideAppDatabase(context).eventDao());
    }

    /**
     * Provides an instance of UserPreferencesRepository.
     *
     * @param context The application context used to access the database.
     * @return An instance of UserPreferencesRepository.
     */
    public static UserPreferencesRepository provideUserPreferencesRepository(Context context) {
        return new UserPreferencesRepository(provideAppDatabase(context).userPreferencesDao());
    }
} 