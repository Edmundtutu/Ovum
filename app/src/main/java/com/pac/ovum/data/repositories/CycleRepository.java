package com.pac.ovum.data.repositories;

import androidx.lifecycle.LiveData;

import com.pac.ovum.data.dao.CycleDao;
import com.pac.ovum.data.models.CycleData;
import com.pac.ovum.utils.AppExecutors;

import java.util.List;

public class CycleRepository {
    private final CycleDao cycleDao;

    public CycleRepository(CycleDao cycleDao) {
        this.cycleDao = cycleDao;
    }

    public void insertCycle(CycleData cycleData) {
        AppExecutors.getInstance().diskIO().execute(() -> cycleDao.insertCycle(cycleData));
    }

    public LiveData<List<CycleData>> getCyclesByUserId(int userId) {
        return cycleDao.getCyclesByUserId(userId);
    }

    public LiveData<CycleData> getCycleById(int cycleId) {
        return cycleDao.getCycleById(cycleId);
    }

    public LiveData<CycleData> getOngoingCycleByUserId(int userId) {
        return cycleDao.getOngoingCycleByUserId(userId);
    }

    public void updateCycle(CycleData cycleData) {
        AppExecutors.getInstance().diskIO().execute(() -> cycleDao.updateCycle(cycleData));
    }

    public void deleteCycle(CycleData cycleData) {
        AppExecutors.getInstance().diskIO().execute(() -> cycleDao.deleteCycle(cycleData));
    }
}
