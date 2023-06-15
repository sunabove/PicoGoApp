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
    private DataOutputStream out ;
    private DataInputStream in ;

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
    public boolean connectBluetoothDevice(BluetoothDevice bluetoothDevice ) {
        boolean success = false;

        this.bluetoothDevice = bluetoothDevice;

        this.bluetoothName = bluetoothDevice.getName();
        this.bluetoothAddress = bluetoothDevice.getAddress();

        success = this.reConnectBluetoothDevice();

        return success;
    }

    @SuppressLint("MissingPermission")
    public boolean reConnectBluetoothDevice() {
        boolean success = false ;

        BluetoothDevice bluetoothDevice = this.bluetoothDevice;

        if( null == bluetoothDevice ) {
            success = false;
        } else if( null != bluetoothDevice ) {
            try {
                @SuppressLint("MissingPermission") BluetoothSocket bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord( UUID.fromString(MY_UUID) );

                bluetoothSocket.connect();

                this.bluetoothSocket = bluetoothSocket;
                this.out = new DataOutputStream( bluetoothSocket.getOutputStream() );
                this.in = new DataInputStream( bluetoothSocket.getInputStream() );

                success = true;
            } catch (IOException e) {
                Log.v( "sunabove", "Cannot create bluetooth socket" );

                e.printStackTrace();

                this.bluetoothSocket = null;
                this.out = null;
                this.in = null;

                success = false;
            }
        }

        return success;

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

    public boolean sendMessage(final String message) {
        Log.v( "sunabove", "sendCommand() message = " + message );

        DataOutputStream out = this.out;

        if( null != out ) {
            try {
                byte startOfHeading = 1;
                byte endOfTransmission = 4 ;
                byte [] data = message.getBytes() ;
                short dataLen = (short) ( data.length );
                byte dataType = 's' ;

                out.writeByte( startOfHeading );
                out.writeByte( dataType );
                out.writeShort( dataLen );
                out.write( data );
                out.writeByte( endOfTransmission );

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
