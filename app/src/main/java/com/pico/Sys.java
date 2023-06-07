package com.pico;

import android.annotation.SuppressLint;
import android.bluetooth.*;
import android.util.Log;

import java.io.*;
import java.util.UUID;

public class Sys {

    private static final Sys sys = new Sys();

    private UUID uuid = UUID.randomUUID();

    private String bluetoothName;
    private String bluetoothAddress;

    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket ;
    private Writer writer;
    private Reader reader;

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
    public void setBluetoothDevice(BluetoothDevice bluetoothDevice ) {
        this.bluetoothDevice = bluetoothDevice;

        this.bluetoothName = bluetoothDevice.getName();
        this.bluetoothAddress = bluetoothDevice.getAddress();

        try {
            BluetoothSocket bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord( uuid );

            Writer writer = new OutputStreamWriter( bluetoothSocket.getOutputStream() );
            Reader reader = new InputStreamReader( bluetoothSocket.getInputStream() );

            this.bluetoothSocket = bluetoothSocket;
            this.writer = writer;
            this.reader = reader;
        } catch (IOException e) {
            Log.v( "sunabove", "Cannot create bluetooth socket" );

            e.printStackTrace();
            this.bluetoothSocket = null;
        }
    }

    public void disconnectBluetoothDevice() {
        BluetoothSocket bluetoothSocket = this.bluetoothSocket;
        Writer writer = this.writer;
        Reader reader = this.reader;

        this.writer = null;
        this.reader = null;
        this.bluetoothSocket = null;
        this.bluetoothDevice = null ;

        this.bluetoothName = null ;
        this.bluetoothAddress = null ;

        if( null != writer ) {
            try {
                writer.close();
            } catch (IOException e) {
            }
        }

        if( null != reader ) {
            try {
                reader.close();
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

    public int sendCommand( final String message ) {
        Log.v( "sunabove", "sendCommand() message = " + message );

        Writer writer = this.writer;

        if( null != writer ) {
            try {
                writer.write( message );
                writer.flush();

                Log.v( "sunabove", "Success: sendCommand() message = " + message );
            } catch (IOException e) {
                return -1;
            }

            return 0;
        } else {
            return -1 ;
        }
    }
}
