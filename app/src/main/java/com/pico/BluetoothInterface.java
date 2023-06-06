package com.pico;

import android.app.Application;

public interface BluetoothInterface {

    public Application getApplication();

    public boolean isScanning();

    public boolean isScanAll();

}
