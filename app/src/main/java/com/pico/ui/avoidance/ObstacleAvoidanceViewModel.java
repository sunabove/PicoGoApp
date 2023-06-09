package com.pico.ui.avoidance;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ObstacleAvoidanceViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ObstacleAvoidanceViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Auto Drive fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}