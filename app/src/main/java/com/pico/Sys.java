package com.pico;

import android.annotation.SuppressLint;
import android.bluetooth.*;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;

import java.io.*;
import java.util.UUID;

public class Sys implements  ComInterface {

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

    public String sendMessage(final String message) {
        boolean directReply = false;

        return this.sendMessage(message, directReply);
    }

    public String sendMessage(final String message, final boolean directReply) {
        Log.v( tag, "sendCommand() message = " + message );

        String reply = "" ;

        DataOutputStream out = this.out;

        if( null == out ) {
            reply = null;
        } else if( null != out ) {
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

                Log.v( tag, "Success: sendCommand() message = " + message );

                if( directReply ) {
                    int replyReadCnt = REPLY_READ_CNT ++ ;

                    final DataInputStream in = this.in;
                    reply = in.readLine();

                    Log.v( tag, String.format( "Success: [%5d] reply direct = %s", replyReadCnt, reply ) );
                } else {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            readReplyUndirectByHandler();
                        }
                    }, 0);
                }

            } catch (IOException e) {
                reply = null;

                e.printStackTrace();
            }
        }

        return reply;
    }

    private static int REPLY_READ_CNT = 0 ;

    private synchronized String readReplyUndirectByHandler() {
        String reply = "" ;

        final DataInputStream in = this.in;

        if( null == in ) {
            reply = null ;
        } else if( null != in ) {
            int replyReadCnt = REPLY_READ_CNT ++ ;

            try {
                reply = in.readLine();

                Log.v( tag, String.format( "Success: [%5d] reply undirect = %s", replyReadCnt, reply ) );
            } catch (IOException e) {
                reply = null ;

                Log.v( tag, String.format( "Fail: [%5d] read reply undirect = %s. cannot read reply", replyReadCnt ) );
            }
        }

        return reply ;
    }
}
