package com.pac.ovum.ui.cycles;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CyclesViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CyclesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Cycles fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}