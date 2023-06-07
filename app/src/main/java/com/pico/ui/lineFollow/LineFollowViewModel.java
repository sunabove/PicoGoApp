package com.pico.ui.lineFollow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LineFollowViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public LineFollowViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is line follow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}