package com.pac.ovum.utils.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;
import java.util.List;

public class LiveDataUtil {
    public static <T> LiveData<List<T>> combineLatest(List<LiveData<T>> liveDataList) {
        MediatorLiveData<List<T>> result = new MediatorLiveData<>();
        List<T> combinedList = new ArrayList<>();

        for (LiveData<T> liveData : liveDataList) {
            result.addSource(liveData, item -> {
                // Update the combined list when any source changes
                int index = liveDataList.indexOf(liveData);
                if (index >= combinedList.size()) {
                    combinedList.add(item);
                } else {
                    combinedList.set(index, item);
                }
                result.setValue(new ArrayList<>(combinedList));
            });
        }

        return result;
    }
}