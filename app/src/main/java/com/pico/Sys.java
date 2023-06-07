package com.pico;

import android.bluetooth.*;

public class Sys {

    private static final Sys sys = new Sys();

    public String bluetoothName;
    public String bluetoothAddress;
    public BluetoothDevice bluetoothDevice;
    public BluetoothSocket bluetoothSocket ;

    private Sys() {

    }

    public static Sys getSys() {
        return sys;
    }
}
