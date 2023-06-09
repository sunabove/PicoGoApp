package com.picopy;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

public class BlueDeviceListAdapter extends BaseAdapter implements ComInterface {
    private ArrayList<BluetoothDevice> devices = new ArrayList<>();

    private BluetoothInterface bluetoothInterface;
    private ComActivity activity;

    private boolean isInterrupted = true;

    public BlueDeviceListAdapter(BluetoothInterface bluetoothInterface) {
        this.bluetoothInterface = bluetoothInterface;
        this.activity = bluetoothInterface.getComActivity();
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public BlueDeviceListAdapter() {
    }

    public void addDevice(BluetoothDevice device) {
        int index = -1;
        this.addDevice(device, index);
    }

    public void addDevice(BluetoothDevice device, int index) {
        boolean test = false;

        if (test) {
            int testCount = (int) (5 + (5 * Math.random()));
            for (int i = 0; i < testCount; i++) {
                this.devices.add(null);
            }
        }

        if (null == device) {
            this.devices.add(0, device);
        } else if (!this.devices.contains(device)) {
            if (index < 0) {
                this.devices.add(device);
            } else {
                this.devices.add(index, device);
            }
        }

        if (test) {
            int testCount = (int) (5 + (5 * Math.random()));
            for (int i = 0; i < testCount; i++) {
                this.devices.add(null);
            }
        }
    }

    public void clear() {
        this.devices.clear();
    }

    public int getCount() {
        return this.devices.size();
    }

    public int getSize() {
        return this.getCount();
    }

    public BluetoothDevice getItem(int i) {
        return this.devices.get(i);
    }

    public boolean isInterrupted() {
        return isInterrupted;
    }

    public void setInterrupted(boolean interrupted) {
        isInterrupted = interrupted;
    }

    public static class ViewHolder {
        TextView rowNumber;
        TextView deviceName;
        TextView deviceAddress;

        ViewHolder() {
        }
    }

    @SuppressLint({"WrongViewCast"})
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;

        int bgColor = black;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.bluetoothInterface.getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_view_bluetooth, null);

            viewHolder = new ViewHolder();
            viewHolder.rowNumber = (TextView) view.findViewById(R.id.row_number);
            viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
            viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BluetoothDevice device = this.devices.get(i);

        String rowNumber = "";
        String name = "";
        String address = "";

        rowNumber = String.format("%02d", i);

        if (device == null && i < 1) {
            bgColor = greenDark;
            rowNumber = "";

            if (this.bluetoothInterface.isScanning()) {
                name = "장치 검색 중";
                address = String.format(" (%d 개)", this.devices.size() - 1);
            } else {
                name = this.isInterrupted ? "장치 검색 중지" : "장치 검색 완료";
                address = String.format(" (%d 개)", this.devices.size() - 1);
            }

        } else if (device == null) {
            bgColor = greyLight;

            name = "알 수 없는 장치";
            address = "";
        } else {
            if (ActivityCompat.checkSelfPermission( activity, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                name = device.getName();
            }
            address = device.getAddress();

            if( name == null || name.trim().length() < 1 ) {
                name = "알 수 없는 장치";
            }

            bgColor = address.equals( activity.getBluetoothAddressLastConnected()) ? redDark : greenDark ;
        }

        viewHolder.rowNumber.setText( rowNumber );
        viewHolder.deviceName.setText( name );
        viewHolder.deviceAddress.setText( address );

        viewHolder.rowNumber.setTextColor( bgColor );
        viewHolder.deviceName.setTextColor( bgColor );
        viewHolder.deviceAddress.setTextColor( bgColor );

        return view;
    }
}