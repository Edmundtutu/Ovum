package com.pac.ovum.data.repositories;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pac.ovum.data.models.CycleData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MockCycleRepository extends CycleRepository {
    private final MutableLiveData<List<CycleData>> cycleData = new MutableLiveData<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    public MockCycleRepository() {
        super(null);
        // Simulate data loading on the main thread
        loadData(); 
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadData() {
        // Simulate data loading
        List<CycleData> mockCycles = new ArrayList<>();
        // Populate cycleSummaries with test data
        // Add mock CycleData objects to the list
        mockCycles.add(createMockCycle(1, LocalDate.now().minusDays(10), LocalDate.now().plusDays(5), 28, 5, true));
        mockCycles.add(createMockCycle(2, LocalDate.now().minusDays(20), LocalDate.now().plusDays(10), 30, 7, false));
        mockCycles.add(createMockCycle(3, LocalDate.now().minusDays(5), LocalDate.now().plusDays(15), 26, 4, false));
        mockCycles.add(createMockCycle(4, LocalDate.now().minusDays(30), LocalDate.now().plusDays(20), 32, 6, false));
        mockCycles.add(createMockCycle(5, LocalDate.now().minusDays(15), LocalDate.now().plusDays(25), 24, 3, false));
        mockCycles.add(createMockCycle(6, LocalDate.now().minusDays(25), LocalDate.now().plusDays(5), 29, 5, false));
        mockCycles.add(createMockCycle(7, LocalDate.now().minusDays(35), LocalDate.now().plusDays(15), 27, 4, false));
        mockCycles.add(createMockCycle(8, LocalDate.now().minusDays(40), LocalDate.now().plusDays(10), 31, 7, false));
        mockCycles.add(createMockCycle(9, LocalDate.now().minusDays(45), LocalDate.now().plusDays(5), 30, 6, false));
        mockCycles.add(createMockCycle(10, LocalDate.now().minusDays(50), LocalDate.now().plusDays(0), 28, 5, false));
        mockCycles.add(createMockCycle(11, LocalDate.now().minusDays(55), LocalDate.now().plusDays(-5), 26, 4, false));
        mockCycles.add(createMockCycle(12, LocalDate.now().minusDays(60), LocalDate.now().plusDays(-10), 29, 5, false));
        mockCycles.add(createMockCycle(13, LocalDate.now().minusDays(65), LocalDate.now().plusDays(-15), 27, 4, false));

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