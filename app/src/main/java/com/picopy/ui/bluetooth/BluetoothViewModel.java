package com.picopy.ui.bluetooth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BluetoothViewModel extends ViewModel {

    private final MutableLiveData<String> liveText;

    public BluetoothViewModel() {
        liveText = new MutableLiveData<>();

        liveText.setValue("This is home fragment");
    }

    public LiveData<String> getLiveText() {
        return liveText;
    }
}