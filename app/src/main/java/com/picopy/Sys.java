package com.picopy;

import android.annotation.SuppressLint;
import android.bluetooth.*;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

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
    private short requestNo = 0 ;

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

                this.requestNo = 0 ;

                success = true;
            } catch (IOException e) {
                Log.v( "sunabove", "Cannot create bluetooth socket" );

                e.printStackTrace();

                this.bluetoothSocket = null;
                this.out = null;
                this.in = null;
                this.requestNo = 0 ;

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

        this.requestNo = 0 ;

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
                short requestNo = this.requestNo;
                byte [] data = message.getBytes() ;
                short dataLen = (short) ( data.length );
                byte dataType = 's' ;

                out.writeByte( startOfHeading );
                out.writeShort( requestNo );
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
                            readReplyUndirectByHandler( requestNo );
                        }
                    }, 0);
                }
            } catch (IOException e) {
                reply = null;

                e.printStackTrace();
            } finally {
                this.requestNo += 1 ;
            }
        }

        return reply;
    }

    private static int REPLY_READ_CNT = 0 ;

    private synchronized String readReplyUndirectByHandler(short requestNo) {
        String reply = "" ;

        final DataInputStream in = this.in;

        if( null == in ) {
            reply = null ;
        } else if( null != in ) {
            int replyReadCnt = REPLY_READ_CNT ++ ;

            try {
                reply = in.readLine();

                Log.v( tag, String.format( "Success: [%5d] reply of request(%d) undirect = %s", replyReadCnt, requestNo, reply ) );
            } catch (IOException e) {
                reply = null ;

                Log.v( tag, String.format( "Fail: [%5d] read reply of reqeust(%d) undirect = %s. cannot read reply", replyReadCnt, requestNo ) );
            }
        }

        return reply ;
    }
}
