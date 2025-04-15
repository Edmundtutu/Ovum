package com.pac.ovum.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.pac.ovum.data.models.CycleData;

import java.util.List;

@Dao
public interface CycleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCycle(CycleData cycleData);

    @Query("SELECT * FROM CycleData WHERE userId = :userId")
    LiveData<List<CycleData>> getCyclesByUserId(int userId);

    @Query("SELECT * FROM CycleData WHERE userId = :userId")
    List<CycleData> getCyclesByUserIdSync(int userId);

    @Query("SELECT * FROM CycleData WHERE cycleId = :cycleId")
    LiveData<CycleData> getCycleById(int cycleId);

    @Query("SELECT * FROM CycleData WHERE cycleId = :cycleId")
    CycleData getCycleByIdSync(int cycleId);

    @Query("SELECT * FROM CycleData WHERE userId = :userId AND isOngoing = 1")
    LiveData<CycleData> getOngoingCycleByUserId(int userId);

    @Query("SELECT * FROM CycleData WHERE userId = :userId AND isOngoing = 1")
    CycleData getOngoingCycleByUserIdSync(int userId);

    @Update
    void updateCycle(CycleData cycleData);

    @Delete
    void deleteCycle(CycleData cycleData);
}
