package com.pico;

import android.annotation.SuppressLint;
import android.bluetooth.*;
import android.util.Log;

import java.io.*;
import java.util.UUID;

public class Sys {

    private static final Sys sys = new Sys();

    private static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    private String bluetoothName;
    private String bluetoothAddress;

    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket ;
    private OutputStream out ;
    private InputStream in ;

    public static Sys getSys() {
        return sys;
    }

    private Sys() {
    }

    public String getBluetoothName() {
        return this.bluetoothName;
    }

    public String getBluetoothAddress() {
        return this.bluetoothAddress;
    }

    public BluetoothDevice getBluetoothDevice() {
        return this.bluetoothDevice;
    }

    @SuppressLint("MissingPermission")
    public boolean setBluetoothDevice(BluetoothDevice bluetoothDevice ) {
        boolean result = true;
        this.bluetoothDevice = bluetoothDevice;

        this.bluetoothName = bluetoothDevice.getName();
        this.bluetoothAddress = bluetoothDevice.getAddress();

        try {
            BluetoothSocket bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord( UUID.fromString(MY_UUID) );

            bluetoothSocket.connect();

            this.bluetoothSocket = bluetoothSocket;
            this.out = bluetoothSocket.getOutputStream();
            this.in = bluetoothSocket.getInputStream();

            result = true;
        } catch (IOException e) {
            Log.v( "sunabove", "Cannot create bluetooth socket" );

            e.printStackTrace();
            this.bluetoothSocket = null;
            this.out = null;
            this.in = null;

            result = false;
        }

        return result;
    }

    public void disconnectBluetoothDevice() {
        BluetoothSocket bluetoothSocket = this.bluetoothSocket;
        OutputStream out = this.out;
        InputStream in = this.in;

        this.out = null;
        this.in = null;
        this.bluetoothSocket = null;
        this.bluetoothDevice = null ;

        this.bluetoothName = null ;
        this.bluetoothAddress = null ;

        if( null != out ) {
            try {
                out.close();
            } catch (IOException e) {
            }
        }

        if( null != in ) {
            try {
                in.close();
            } catch (IOException e) {
            }
        }
        if( null != bluetoothSocket ) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
            }
        }

    }

    public boolean sendCommand( final String message ) {
        Log.v( "sunabove", "sendCommand() message = " + message );

        OutputStream out = this.out;

        if( null != out ) {
            try {
                out.write( message.getBytes() );
                out.flush();

                Log.v( "sunabove", "Success: sendCommand() message = " + message );
            } catch (IOException e) {
                e.printStackTrace();

                return false ;
            }

            return true ;
        } else {
            return false ;
        }
    }
}
