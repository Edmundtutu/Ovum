package com.pac.ovum.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.pac.ovum.data.models.Episode;

import java.time.LocalDate;
import java.util.List;

@Dao
public interface EpisodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertEpisode(Episode episode);

    @Query("SELECT * FROM Episode WHERE cycleId = :cycleId")
    LiveData<List<Episode>> getEpisodesByCycleId(int cycleId);
    
    @Query("SELECT * FROM Episode WHERE cycleId = :cycleId")
    List<Episode> getEpisodesByCycleIdSync(int cycleId);

    @Query("SELECT * FROM Episode WHERE episodeId = :episodeId")
    LiveData<Episode> getEpisodeById(int episodeId);
    
    @Query("SELECT * FROM Episode WHERE episodeId = :episodeId")
    Episode getEpisodeByIdSync(int episodeId);

    @Query("SELECT * FROM Episode WHERE symptomType = :symptomType AND cycleId = :cycleId")
    LiveData<List<Episode>> getEpisodesBySymptomTypeAndCycle(String symptomType, int cycleId);
    
    @Query("SELECT * FROM Episode WHERE symptomType = :symptomType AND cycleId = :cycleId")
    List<Episode> getEpisodesBySymptomTypeAndCycleSync(String symptomType, int cycleId);

    @Query("SELECT date as episodeDate, COUNT(*) as count FROM Episode WHERE date BETWEEN :start AND :end GROUP BY date")
    LiveData<List<EpisodeCount>> getEpisodesCountBetweenLive(LocalDate start, LocalDate end);
    
    @Query("SELECT date as episodeDate, COUNT(*) as count FROM Episode WHERE date BETWEEN :start AND :end GROUP BY date")
    List<EpisodeCount> getEpisodesCountBetweenSync(LocalDate start, LocalDate end);

    @Update
    void updateEpisode(Episode episode);

    @Delete
    void deleteEpisode(Episode episode);

    class EpisodeCount {
        public LocalDate episodeDate;
        public int count;
    }
}
