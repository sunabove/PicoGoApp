package com.pico.ui.autoDrive;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AutoDriveViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AutoDriveViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Auto Drive fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}