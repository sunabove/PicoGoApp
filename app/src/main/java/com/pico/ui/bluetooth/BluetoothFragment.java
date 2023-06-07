package com.pico.ui.bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.pico.BlueDeviceListAdapter;
import com.pico.BluetoothInterface;
import com.pico.databinding.FragmentBluetoothBinding;

public class BluetoothFragment extends Fragment implements BluetoothInterface  {

    private FragmentBluetoothBinding binding;

    protected boolean scanningBluetooth = false;
    private ProgressBar bluetoothProgressBar;
    private ListView bluetoothListView;
    private BlueDeviceListAdapter blueDeviceListAdapter;

    private Button blueScanButton;
    private CheckBox scanPicoOnlyCheckBox;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BluetoothViewModel bluetoothViewModel = new ViewModelProvider(this).get(BluetoothViewModel.class);

        binding = FragmentBluetoothBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        this.bluetoothProgressBar = binding.bluetoothProgressBar;
        this.bluetoothListView = binding.bluetoothListView;
        this.blueScanButton = binding.blueScanButton;
        this.scanPicoOnlyCheckBox = binding.scanPicoOnlyCheckBox;

        this.blueScanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                blueScanButton.setEnabled(false);
                scanPicoOnlyCheckBox.setEnabled(false);

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scanBlueDevices();
                    }
                }, 500);
            }
        });

        this.blueDeviceListAdapter = new BlueDeviceListAdapter(this);

        bluetoothListView.setAdapter(blueDeviceListAdapter);

        this.scanBlueDevices();

        return root;
    }

    public boolean isScanning() {
        return this.scanningBluetooth ;
    }

    public boolean isScanAll() {
        return ! this.scanPicoOnlyCheckBox.isChecked();
    }

    public Application getApplication() {
        return this.getActivity().getApplication();
    }

    @Override
    public void onPause() {
        super.onPause();

        this.scanningBluetooth = false;

        this.whenBluetoothScanningFinished();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void scanBlueDevices() {
        Log.v("sunabove", "scanBleDevices");

        this.blueScanButton.setEnabled(false);
        this.scanPicoOnlyCheckBox.setEnabled(false);
        this.bluetoothProgressBar.setVisibility(View.VISIBLE);

        this.blueDeviceListAdapter.clear();
        this.blueDeviceListAdapter.notifyDataSetChanged();

        this.scanningBluetooth = true;

        this.blueDeviceListAdapter.addDevice(null);
        this.blueDeviceListAdapter.notifyDataSetChanged();

        this.scanBlueDevicesByIntentFilter();
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

        Activity activity = this.getActivity();

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            BluetoothManager btManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
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
        this.scanPicoOnlyCheckBox.setEnabled(true);

        bluetoothProgressBar.setVisibility(View.GONE);
    }

    @SuppressLint("MissingPermission")
    public void scanBlueDevicesByIntentFilter() {
        Log.v("sunabove", "scanBleDevicesByIntentFilter()");

        Activity activity = this.getActivity();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction( BluetoothDevice.ACTION_FOUND );
        intentFilter.addAction( BluetoothAdapter.ACTION_DISCOVERY_FINISHED );

        this.receiver = this.getReceiver() ;
        activity.registerReceiver( receiver, intentFilter);

        BluetoothManager btManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter btAdapter = btManager.getAdapter();
        btAdapter.startDiscovery();
    }

    public void addBlueDevice(BluetoothDevice device ) {
        boolean scanAll = this.isScanAll();

        @SuppressLint("MissingPermission") String name = device.getName();
        String address = device.getAddress();
        if( null != name ) {
            if( scanAll || name.toUpperCase().endsWith( "SPP" ) ) {
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
}