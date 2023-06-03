package com.pico;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BlueDeviceListAdapter extends BaseAdapter {
    private ArrayList<BluetoothDevice> devices = new ArrayList<>();

    private ComActivity activity ;

    public BlueDeviceListAdapter(ComActivity activity) {
        this.activity = activity;
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public BlueDeviceListAdapter() {
    }

    public void addDevice(BluetoothDevice bluetoothDevice) {
        if ( ! this.devices.contains(bluetoothDevice) ) {
            this.devices.add(bluetoothDevice);
        }
    }

    public BluetoothDevice getDevice(int i) {
        return this.devices.get(i);
    }

    public void clear() {
        this.devices.clear();
    }

    public int getCount() {
        return this.devices.size();
    }

    public Object getItem(int i) {
        return this.devices.get(i);
    }

    static class ViewHolder {
        TextView deviceAddress;
        TextView deviceName;

        ViewHolder() {
        }
    }

    @SuppressLint("WrongViewCast")
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null ;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate( R.layout.listview_plain, null );
            viewHolder = new ViewHolder();
            viewHolder.deviceName = (TextView) view.findViewById( R.id.list_view_plain_title );
            viewHolder.deviceAddress = (TextView) view.findViewById( R.id.list_view_plain_desc );
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BluetoothDevice bluetoothDevice = this.devices.get(i);

        @SuppressLint("MissingPermission") String name = bluetoothDevice.getName();

        if (name == null || name.length() < 1 ) {
            viewHolder.deviceName.setText( " Unknow Device ");
        } else {
            viewHolder.deviceName.setText(" " + name);
        }

        viewHolder.deviceAddress.setText( "  " + bluetoothDevice.getAddress());

        return view;
    }
}