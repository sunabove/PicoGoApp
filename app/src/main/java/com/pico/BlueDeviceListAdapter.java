package com.pico;

import android.annotation.SuppressLint;
import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BlueDeviceListAdapter extends BaseAdapter {
    private ArrayList<BluetoothDevice> devices = new ArrayList<>();

    private BluetoothInterface bluetoothInterface  ;

    public BlueDeviceListAdapter(BluetoothInterface bluetoothInterface) {
        this.bluetoothInterface = bluetoothInterface;
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public BlueDeviceListAdapter() {
    }

    public void addDevice(BluetoothDevice device) {
        if( null == device ) {
            this.devices.add( device );
        } else if ( ! this.devices.contains(device) ) {
            this.devices.add(device);
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
        TextView rowNumber;
        TextView deviceName;
        TextView deviceAddress;

        ViewHolder() {
        }
    }

    @SuppressLint({"WrongViewCast", "MissingPermission"})
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null ;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.bluetoothInterface.getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate( R.layout.bluetooth_list_view, null );
            viewHolder = new ViewHolder();
            viewHolder.rowNumber = (TextView) view.findViewById( R.id.row_number);
            viewHolder.deviceName = (TextView) view.findViewById( R.id.device_name);
            viewHolder.deviceAddress = (TextView) view.findViewById( R.id.device_address);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BluetoothDevice device = this.devices.get(i);

        String rowNumber = "  ";
        String name = "" ;
        String address = "" ;

        if( device == null ) {
            if( this.bluetoothInterface.isScanning() ) {
                name = "장치 검색 중";
                address = String.format( " (%d 개)", this.devices.size() -1 );
            } else {
                name = "장치 검색 완료";
                address = String.format( " (%d 개)", this.devices.size() -1 );
            }
        } else {
            rowNumber = String.format( "%02d", i );
            name = device.getName();
            address = device.getAddress();

            if( name == null || name.trim().length() < 1 ) {
                name = "알 수 없는 장치";
            }
        }

        viewHolder.rowNumber.setText( rowNumber );
        viewHolder.deviceName.setText( name );
        viewHolder.deviceAddress.setText( address );

        return view;
    }
}