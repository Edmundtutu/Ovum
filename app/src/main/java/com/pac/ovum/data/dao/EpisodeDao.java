package com.pac.ovum.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.pac.ovum.data.models.Episode;

import java.util.List;

@Dao
public interface EpisodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEpisode(Episode episode);

    @Query("SELECT * FROM Episode WHERE cycleId = :cycleId")
    LiveData<List<Episode>> getEpisodesByCycleId(int cycleId);

    @Query("SELECT * FROM Episode WHERE episodeId = :episodeId")
    LiveData<Episode> getEpisodeById(int episodeId);

    @Query("SELECT * FROM Episode WHERE symptomType = :symptomType AND cycleId = :cycleId")
    LiveData<List<Episode>> getEpisodesBySymptomTypeAndCycle(String symptomType, int cycleId);

    @Update
    void updateEpisode(Episode episode);

    @Delete
    void deleteEpisode(Episode episode);
}
