package com.picopy;

import android.app.Application;

public interface BluetoothInterface {

    public Application getApplication();

    public ComActivity getComActivity();

    public boolean isScanning();

    public boolean isScanAll();

}
