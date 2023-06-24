package com.picopy.ui.laneFollow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LaneFollowViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public LaneFollowViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Line Follow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}