package com.pico;

import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
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
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;

public class ControlActivity extends ComActivity implements BluetoothInterface {

    protected boolean scanningBluetooth = false;
    private ProgressBar bluetoothProgressBar;
    private Spinner bluetoothListSpinner;
    private BlueDeviceListAdapter blueDeviceListAdapter;

    private Button blueScanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_control);

        this.bluetoothProgressBar = this.findViewById(R.id.bluetoothProgressBar);
        this.blueScanButton = this.findViewById(R.id.blueScanButton);
        this.bluetoothListSpinner = this.findViewById(R.id.bluetoothListSpinner);

        this.bluetoothProgressBar.setVisibility(View.GONE);

        this.blueScanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                blueScanButton.setEnabled(false);

                activity.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scanBlueDevices();
                    }
                }, 500);
            }
        });

        this.blueDeviceListAdapter = new BlueDeviceListAdapter(this);

        bluetoothListSpinner.setAdapter(blueDeviceListAdapter);

        this.scanBlueDevices();
    }

    public boolean isScanning() {
        return this.scanningBluetooth;
    }

    public boolean isScanAll() {
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        this.scanningBluetooth = false;

        this.whenBluetoothScanningFinished();
    }

    // Device scan callback.

    public void scanBlueDevices() {
        Log.v("sunabove", "scanBleDevices");

        this.blueScanButton.setEnabled(false);
        this.bluetoothProgressBar.setVisibility(View.VISIBLE);

        this.blueDeviceListAdapter.clear();
        this.blueDeviceListAdapter.notifyDataSetChanged();

        this.scanningBluetooth = true;

        this.blueDeviceListAdapter.addDevice(null);

        this.blueDeviceListAdapter.notifyDataSetChanged();

        boolean useIntentFilter = true;

        if (useIntentFilter) {
            this.scanBlueDevicesByIntentFilter();
        } else {
            this.scanBlueDevicesByScanner();
        }
    }

    private BroadcastReceiver receiver = null;

    private  BroadcastReceiver getReceiver () {
        BroadcastReceiver receiver = new BroadcastReceiver() {

            @SuppressLint("MissingPermission")
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                    BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    addBlueDevice(device);
                } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED) && scanningBluetooth) {
                    scanningBluetooth = false;
                }

                if (scanningBluetooth == false) {
                    whenBluetoothScanningFinished();
                }
            }
        };

        return receiver ;
    }

    public void whenBluetoothScanningFinished() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            BluetoothManager btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter btAdapter = btManager.getAdapter();

            btAdapter.cancelDiscovery();

            BroadcastReceiver receiver = this.receiver;

            if( null != this.receiver ) {
                this.receiver = null;

                activity.unregisterReceiver(receiver);
            }
        }

        blueDeviceListAdapter.notifyDataSetChanged();

        blueScanButton.setEnabled( true );
        bluetoothProgressBar.setVisibility(View.GONE);
    }

    @SuppressLint("MissingPermission")
    public void scanBlueDevicesByIntentFilter() {
        Log.v("sunabove", "scanBleDevicesByIntentFilter()");

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction( BluetoothDevice.ACTION_FOUND );
        intentFilter.addAction( BluetoothAdapter.ACTION_DISCOVERY_FINISHED );

        this.receiver = this.getReceiver() ;
        this.registerReceiver( receiver, intentFilter);

        BluetoothManager btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter btAdapter = btManager.getAdapter();
        btAdapter.startDiscovery();
    }

    public void addBlueDevice(BluetoothDevice device ) {
        @SuppressLint("MissingPermission") String name = device.getName();
        String address = device.getAddress();
        if( null != name ) {
            if( name.toUpperCase().endsWith( "SPP" ) ) {
                String msg = "BLE Device Name: " + name + " address: " + address + " appended";
                this.blueDeviceListAdapter.addDevice(device);
                this.blueDeviceListAdapter.notifyDataSetChanged();
                Log.v("sunabove", msg);
            } else {
                String msg = "BLE Device Name: " + name + " address: " + address + " not appended";
                Log.v("sunabove", msg);
            }
        }
    }

    public void scanBlueDevicesByScanner() {
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

}