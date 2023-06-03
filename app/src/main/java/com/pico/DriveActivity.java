package com.pico;

import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class DriveActivity extends ComActivity {

    private Spinner blueToothSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);

        this.blueToothSpinner = this.findViewById(R.id.blueToothSpinner);

        StringList list = new StringList();

        boolean test = false;

        if (test) {
            list.add("Mercury");
            list.add("Venus");
            list.add("Mercury");
            list.add("Earth");
            list.add("Mars");
            list.add("Jupiter");
        } else {
            list.add("");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        blueToothSpinner.setAdapter(adapter);

        this.checkBleDevices();
    }


    private final int REQUEST_ENABLE_BT = 2 ;

    final String[] ANDROID_BLE_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,

            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,

            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
    };

    public int checkBadPermissions() {
        int badPermissions = 0 ;
        for( String perm : ANDROID_BLE_PERMISSIONS ) {
            boolean permitted = ActivityCompat.checkSelfPermission( this, perm ) == PackageManager.PERMISSION_GRANTED ;

            Log.i("sunabove", "permission check = " + perm + ", " + permitted );

            if(  ! permitted )  {
                badPermissions += 1 ;
            }
        }

        return badPermissions ;
    }

    public void requestPermissions() {
        Log.v("sunabove", "requestPermissions");

        int index = 0 ;
        for( String perm : ANDROID_BLE_PERMISSIONS ) {
            boolean permitted = ActivityCompat.checkSelfPermission( this, perm ) == PackageManager.PERMISSION_GRANTED ;

            if(  ! permitted )  {
                Log.i("sunabove", "permission request = " + perm + ", " + permitted );

                ActivityCompat.requestPermissions(this, new String [] { perm }, index );
            }

            index += 1;
        }

    }

    @SuppressLint("MissingPermission")
    private void checkBleDevices() {

        Log.v("sunabove", "checkBleDevices");

        if ( this.checkBadPermissions() > 0 ) {
            requestPermissions();
        } else {
            this.scanBleDevices();
        }

        if( false ) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("블루투스 검색 권한 설정");
            builder.setMessage("블루투스 검색 권한을 허용하여 주세요.");

            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions();
                }
            });
            builder.show();
        }
    }

    // Device scan callback.
    private ScanCallback bleScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            if (ActivityCompat.checkSelfPermission( activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.v( "sunabove", "onScanResult failed" );

                return;
            }

            BluetoothDevice device = result.getDevice();
            String deviceName = device.getName();
            int rssi = result.getRssi();

            if( null != deviceName ) {
                String msg = "BLE Device Name: " + result.getDevice().getName() + " rssi: " + rssi ;

                Log.v("sunabove", msg);
            }
        }
    };

    public void scanBleDevices() {
        Log.v("sunabove", "scanBleDevices");

        BluetoothManager btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter btAdapter = btManager.getAdapter();
        final BluetoothLeScanner btScanner = btAdapter.getBluetoothLeScanner();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @SuppressLint("MissingPermission")
            @Override
            public void run() {
                Log.v("sunabove", "btScanner.startScan(bleScanCallback)");

                btScanner.startScan(bleScanCallback);
            }
        }, 10);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if( this.checkBadPermissions() > 0 ) {
            requestPermissions();
        } else {
            String title = "블루투스 권한 설정 성공" ;
            String message = "블루투스를 검색할 수 있습니다.";

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle( title );
            builder.setMessage( message );
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    scanBleDevices();
                }
            });
            builder.show();
        }
    }
}