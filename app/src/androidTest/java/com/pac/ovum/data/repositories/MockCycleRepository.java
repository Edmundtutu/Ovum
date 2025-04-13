package com.pac.ovum.data.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pac.ovum.data.models.CycleData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MockCycleRepository extends CycleRepository {
    private final MutableLiveData<List<CycleData>> cycleData = new MutableLiveData<>();

    public MockCycleRepository() {
        super(null,null);
        // Simulate data loading on the main thread
        loadData(); 
    }
    private void loadData() {
        // Simulate data loading
        List<CycleData> mockCycles = new ArrayList<>();
        // Populate cycleSummaries with test data
        // Add mock CycleData objects to the list
        mockCycles.add(createMockCycle(1, LocalDate.now().minusDays(10), LocalDate.now().plusDays(5), 28, 5, true));
        mockCycles.add(createMockCycle(2, LocalDate.now().minusDays(20), LocalDate.now().plusDays(10), 30, 7, false));
        mockCycles.add(createMockCycle(3, LocalDate.now().minusDays(5), LocalDate.now().plusDays(15), 26, 4, true));

        // Use postValue to ensure it's on the main thread
        cycleData.postValue(mockCycles);
    }

    @Override
    public LiveData<List<CycleData>> getCyclesByUserId(int userId) {
        return cycleData;
    }

    private CycleData createMockCycle(int cycleId, LocalDate startDate, LocalDate endDate, int cycleLength, int periodLength, boolean isOngoing) {
        CycleData cycle = new CycleData();
        cycle.setCycleId(cycleId);
        cycle.setStartDate(startDate);
        cycle.setEndDate(endDate);
        cycle.setCycleLength(cycleLength);
        cycle.setPeriodLength(periodLength);
        cycle.setOngoing(isOngoing);
        cycle.setUserId(1); // Assuming userId is 1 for testing
        return cycle;
    }
} 