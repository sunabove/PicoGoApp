package com.pico;

import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.Spinner;

public class ControlActivity extends ComActivity {

    private Spinner blueSpinner;
    private BlueDeviceListAdapter blueDeviceListAdapter;

    private Button blueScanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        this.blueScanButton = this.findViewById(R.id.blueScanButton);

        this.blueSpinner = this.findViewById(R.id.blueToothSpinner);

        StringList list = new StringList();

        this.blueDeviceListAdapter = new BlueDeviceListAdapter( this );

        blueSpinner.setAdapter(blueDeviceListAdapter);

        this.checkBleDevices();
    }

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


    public void scanBleDevices() {
        Log.v("sunabove", "scanBleDevices");

        this.blueDeviceListAdapter.clear();
        this.blueDeviceListAdapter.notifyDataSetChanged();

        boolean useIntentFilter = true;

        if( useIntentFilter ) {
            this.scanBleDevicesByIntentFilter();
        } else {
            this.scanBleDevicesByScanner();
        }
    }

    @SuppressLint("MissingPermission")
    public void scanBleDevicesByIntentFilter() {
        Log.v("sunabove", "scanBleDevicesByIntentFilter()");

        final BroadcastReceiver receiver = new BroadcastReceiver() {

            boolean scanning = true;

            @SuppressLint("MissingPermission")
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                String actionFound = BluetoothDevice.ACTION_FOUND ;
                String extraDevice = BluetoothDevice.EXTRA_DEVICE ;
                String disFinished = BluetoothAdapter.ACTION_DISCOVERY_FINISHED ;

                if (action.equals( BluetoothDevice.ACTION_FOUND )) {
                    BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra( BluetoothDevice.EXTRA_DEVICE );
                    addBlueDevice( device );
                } else if (action.equals( BluetoothAdapter.ACTION_DISCOVERY_FINISHED ) && this.scanning) {
                    this.scanning = false;
                    BluetoothManager btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                    BluetoothAdapter btAdapter = btManager.getAdapter();
                    btAdapter.cancelDiscovery();
                    activity.unregisterReceiver( this );
                    blueDeviceListAdapter.notifyDataSetChanged();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction( BluetoothDevice.ACTION_FOUND );
        intentFilter.addAction( BluetoothAdapter.ACTION_DISCOVERY_FINISHED );

        this.registerReceiver( receiver, intentFilter);

        //BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        BluetoothManager btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter btAdapter = btManager.getAdapter();
        btAdapter.startDiscovery();
    }

    public void addBlueDevice(BluetoothDevice device ) {
        @SuppressLint("MissingPermission") String name = device.getName();
        String address = device.getAddress();
        if( null != name ) {
            String msg = "BLE Device Name: " + name + " address: " + address ;

            Log.v("sunabove", msg);

            this.blueDeviceListAdapter.addDevice( device );
            this.blueDeviceListAdapter.notifyDataSetChanged();
        }
    }

    public void scanBleDevicesByScanner() {
        Log.v("sunabove", "scanBleDevicesByScanner()");

        BluetoothManager btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter btAdapter = btManager.getAdapter();

        final BluetoothLeScanner btScanner = btAdapter.getBluetoothLeScanner();

        final ScanCallback bleScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);

                BluetoothDevice device = result.getDevice();
                addBlueDevice( device );
            }
        };

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @SuppressLint("MissingPermission")
            @Override
            public void run() {
                Log.v("sunabove", "btScanner.startScan(bleScanCallback)");

                btScanner.startScan( bleScanCallback );
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