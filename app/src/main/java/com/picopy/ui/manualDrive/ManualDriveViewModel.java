package com.picopy.ui.manualDrive;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ManualDriveViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ManualDriveViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Manual Drive fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}