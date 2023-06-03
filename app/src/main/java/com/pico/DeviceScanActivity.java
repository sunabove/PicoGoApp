package com.pico;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import java.util.ArrayList;

public class DeviceScanActivity extends ListActivity {
    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;
    /* access modifiers changed from: private */
    public BluetoothAdapter mBluetoothAdapter;
    public final Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 1) {
                DeviceScanActivity.this.mLeDeviceListAdapter.notifyDataSetChanged();
            }
        }
    };
    /* access modifiers changed from: private */
    public LeDeviceListAdapter mLeDeviceListAdapter;
    /* access modifiers changed from: private */
    public boolean mScanning = false;
    private BroadcastReceiver searchDevices = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.bluetooth.device.action.FOUND")) {
                DeviceScanActivity.this.mLeDeviceListAdapter.addDevice((BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE"));
                DeviceScanActivity.this.mHandler.sendEmptyMessage(1);
            } else if (action.equals("android.bluetooth.adapter.action.DISCOVERY_FINISHED") && DeviceScanActivity.this.mScanning) {
                boolean unused = DeviceScanActivity.this.mScanning = false;
                DeviceScanActivity.this.mBluetoothAdapter.cancelDiscovery();
                //Toast.makeText(DeviceScanActivity.this, "扫描完成，点击列表中的设备来尝试连接", 0).show();
                DeviceScanActivity.this.mHandler.sendEmptyMessage(1);
                DeviceScanActivity.this.invalidateOptionsMenu();
            }
        }
    };
    public int turnBluetooth = 0;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBluetoothAdapter = defaultAdapter;
        if (defaultAdapter.getState() != BluetoothAdapter.STATE_ON) {
            turnOnBluetooth(this, this.turnBluetooth);
            Log.i("sunabove", "turnBluetooth" + this.turnBluetooth);
        }

        final String[] permissions = new String[]{
                android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.BLUETOOTH_ADMIN,

                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,

                android.Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
        };

        boolean goodPermission = true ;
        for( String perm : permissions ) {
            Log.i("sunabove", "permission check = " + perm );
            if(  ActivityCompat.checkSelfPermission( this, perm ) != PackageManager.PERMISSION_GRANTED )  {
                goodPermission = false ;
                break;
            }
        }

        if( ! goodPermission ) {
            ActivityCompat.requestPermissions(this, permissions, 2 );
        } else {
            ActivityCompat.requestPermissions(this, permissions, 2 );
        }

        LeDeviceListAdapter leDeviceListAdapter = new LeDeviceListAdapter();
        this.mLeDeviceListAdapter = leDeviceListAdapter;
        setListAdapter(leDeviceListAdapter);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.device.action.FOUND");
        //intentFilter.addAction("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
        registerReceiver(this.searchDevices, intentFilter);

        if( goodPermission ) {
            scanLeDevice( true  );
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        // scanLeDevice(false);
    }

    @SuppressLint("MissingPermission")
    private void scanLeDevice(boolean z) {
        if (z) {
            this.mScanning = true;
            this.mLeDeviceListAdapter.clear();
            this.mHandler.sendEmptyMessage(1);
            this.mBluetoothAdapter.startDiscovery();
        } else {
            this.mScanning = false;
            this.mBluetoothAdapter.cancelDiscovery();
        }
        invalidateOptionsMenu();
    }

    /* access modifiers changed from: package-private */
    public void turnOnBluetooth(Activity activity, int i) {
        startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), i);
    }

    private class LeDeviceListAdapter extends BaseAdapter {
        private LayoutInflater mInflator;
        private ArrayList<BluetoothDevice> mLeDevices = new ArrayList<>();

        public long getItemId(int i) {
            return (long) i;
        }

        public LeDeviceListAdapter() {
            this.mInflator = DeviceScanActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice bluetoothDevice) {
            if (!this.mLeDevices.contains(bluetoothDevice)) {
                this.mLeDevices.add(bluetoothDevice);
            }
        }

        public BluetoothDevice getDevice(int i) {
            return this.mLeDevices.get(i);
        }

        public void clear() {
            this.mLeDevices.clear();
        }

        public int getCount() {
            return this.mLeDevices.size();
        }

        public Object getItem(int i) {
            return this.mLeDevices.get(i);
        }

        @SuppressLint("WrongViewCast")
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = this.mInflator.inflate(R.layout.listview_plain, (ViewGroup) null);
                viewHolder = new ViewHolder();
                viewHolder.deviceName = (TextView) view.findViewById(R.id.list_view_plain_title);
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.list_view_plain_desc);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            BluetoothDevice bluetoothDevice = this.mLeDevices.get(i);
            @SuppressLint("MissingPermission") String name = bluetoothDevice.getName();
            if (name == null || name.length() <= 0) {
                viewHolder.deviceName.setText( "Unknown device" );
            } else {
                viewHolder.deviceName.setText(name);
            }
            viewHolder.deviceAddress.setText(bluetoothDevice.getAddress());
            return view;
        }
    }

    static class ViewHolder {
        TextView deviceAddress;
        TextView deviceName;

        ViewHolder() {
        }
    }
}
