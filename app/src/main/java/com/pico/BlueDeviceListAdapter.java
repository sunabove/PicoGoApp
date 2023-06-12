package com.pico;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BlueDeviceListAdapter extends BaseAdapter implements ComInterface {
    private ArrayList<BluetoothDevice> devices = new ArrayList<>();

    private BluetoothInterface bluetoothInterface  ;
    private ComActivity activity ;

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
        boolean test = false  ;

        if( test ) {
            int testCount = (int) (5 + (5 * Math.random()));
            for (int i = 0; i < testCount; i++) {
                this.devices.add( null );
            }
        }

        if( null == device ) {
            this.devices.add( device );
        } else if ( ! this.devices.contains(device) ) {
            this.devices.add(device);
        }

        if( test ) {
            int testCount = (int) (5 + (5 * Math.random()));
            for (int i = 0; i < testCount; i++) {
                this.devices.add( null );
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
        return  this.getCount();
    }

    public BluetoothDevice getItem(int i) {
        return this.devices.get(i);
    }

    public static class ViewHolder {
        TextView rowNumber;
        TextView deviceName;
        TextView deviceAddress;

        ViewHolder() {
        }
    }

    @SuppressLint({"WrongViewCast", "MissingPermission"})
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null ;

        int bgColor = black;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.bluetoothInterface.getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate( R.layout.list_view_bluetooth, null );

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

        rowNumber = String.format( "%02d", i );

        if( device == null && i < 1 ) {
            bgColor = greenDark ;
            rowNumber = "    ";

            if( this.bluetoothInterface.isScanning() ) {
                name = "장치 검색 중";
                address = String.format( " (%d 개)", this.devices.size() -1 );
            } else {
                name = "장치 검색 완료";
                address = String.format( " (%d 개)", this.devices.size() -1 );
            }

        } else if( device == null ) {
            bgColor = greyLight ;

            name = "알 수 없는 장치";
            address = "" ; 
        } else {
            name = device.getName();
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