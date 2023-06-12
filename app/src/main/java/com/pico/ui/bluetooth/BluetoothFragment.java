package com.pico.ui.bluetooth;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.pico.BlueDeviceListAdapter;
import com.pico.BluetoothInterface;
import com.pico.ComActivity;
import com.pico.ComFragment;
import com.pico.databinding.FragmentBluetoothBinding;

public class BluetoothFragment extends ComFragment implements BluetoothInterface  {

    private FragmentBluetoothBinding binding;

    protected boolean scanningBluetooth = false;
    private ProgressBar bluetoothProgressBar;
    private ListView bluetoothListView;
    private BlueDeviceListAdapter blueDeviceListAdapter;

    private Button blueScanButton ;
    private CheckBox autoConnect ;
    private CheckBox scanPicoOnlyCheckBox;

    private ProgressBar connectingProgressBar;
    private EditText connectingStatus;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v("sunabove", "onCreateView()");

        BluetoothViewModel bluetoothViewModel = new ViewModelProvider(this).get(BluetoothViewModel.class);

        binding = FragmentBluetoothBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        this.bluetoothProgressBar = binding.bluetoothProgressBar;
        this.bluetoothListView = binding.bluetoothListView;
        this.blueScanButton = binding.blueScanButton;
        this.scanPicoOnlyCheckBox = binding.scanPicoOnlyCheckBox;
        this.autoConnect = binding.autoConnect;

        this.connectingProgressBar = binding.connectingProgressBar;
        this.connectingStatus = binding.connectingStatus;

        this.connectingStatus.setText( "" );

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

        this.bluetoothListView.setAdapter(blueDeviceListAdapter);

        this.bluetoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                whenBluetoothListViewItemClick(adapterView,view, i, l);
            }
        });

        this.autoConnect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                whenAutoConnectChecked( compoundButton, checked );
            }
        });

        this.scanBlueDevices();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        this.sendBuzzerMessage( false );
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.v( tag, "onPause()");

        this.scanningBluetooth = false;

        this.connectingStatus.setText( "" );

        this.whenBluetoothScanningFinished();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.v("sunabove", "onDestroyView()");

        binding = null;
    }

    private void whenAutoConnectChecked( CompoundButton compoundButton, boolean checked ) {
        String addressLast = this.getProperty( BLUETOOTH_ADDRESS_KEY );

        if( checked && addressLast.length() > 0 ) {
            BlueDeviceListAdapter blueDeviceListAdapter = this.blueDeviceListAdapter;

            BluetoothDevice device ;

            for( int i = 0 ; i < blueDeviceListAdapter.getCount() ; i ++ ) {
                device = blueDeviceListAdapter.getItem( i );
                if( device != null && device.getAddress().equals( addressLast ) ) {
                    this.connectBluetooth( device );

                    return;
                }
            }
        }
    }

    private void whenBluetoothListViewItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String msg = "whenBluetoothListViewItemClick() i = " + i + ", l = " + l ;

        Log.v("sunabove", msg );

        BluetoothDevice device = this.blueDeviceListAdapter.getItem( i );

        if( null != device ) {
            this.connectBluetooth( device );
        }

    } // -- whenBluetoothListViewItemClick

    private boolean connectingBluetooth = false ;

    private void connectBluetooth(BluetoothDevice device ) {

        if( ! this.connectingBluetooth ) {
            this.connectingBluetooth = true;

            this.autoConnect.setEnabled( false );

            this.connectingProgressBar.setVisibility(View.VISIBLE);
            this.connectingProgressBar.setIndeterminate( true );

            this.connectingStatus.setTextColor( greenDark );
            this.connectingStatus.setText( "블루투스 장치를 연결중입니다.");

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    connectBluetoothImpl(device);
                }
            }, 2_500);
        }

    }

    private void connectBluetoothImpl(BluetoothDevice device ) {

        @SuppressLint("MissingPermission") String name = device.getName();
        String address = device.getAddress();
        String msg = " BLE Device Name : " + name + " address : " + address ;

        Log.v("sunabove", msg );

        boolean success = sys.connectBluetoothDevice( device );

        if( success ) {
            this.saveProperty(BLUETOOTH_ADDRESS_KEY, address );

            this.connectingProgressBar.setVisibility( View.VISIBLE) ;
            this.connectingProgressBar.setIndeterminate( true );

            this.connectingStatus.setTextColor( greenDark );
            this.connectingStatus.setText( "블루투스 장치 연결에 성공하였습니다.");

            if( activity.paused ) {
                Log.v( tag, "activity is paused. cannot move to fragment" );
            } else if( ! activity.paused ) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int navIdx = 1;
                        moveToFragment(navIdx);
                    }
                }, 1_000);
            }
        } else {
            this.connectingProgressBar.setVisibility(View.GONE );
            this.connectingProgressBar.setIndeterminate( false );

            this.connectingStatus.setTextColor( red );
            this.connectingStatus.setText( "블루투스 장치 연결에 실패하였습니다.");
        }

        this.autoConnect.setEnabled( true );

        this.connectingBluetooth = false ;

    }

    public ComActivity getComActivity() {
        return (ComActivity) super.getActivity();
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

    public void scanBlueDevices() {
        Log.v("sunabove", "scanBleDevices");

        this.blueScanButton.setEnabled(false);
        this.scanPicoOnlyCheckBox.setEnabled(false);
        this.bluetoothProgressBar.setVisibility(View.VISIBLE);

        this.connectingProgressBar.setIndeterminate( false );
        this.connectingProgressBar.setVisibility(View.GONE );
        this.connectingStatus.setText( "" );

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

    @SuppressLint("MissingPermission")
    public void whenBluetoothScanningFinished() {
        if ( activity.checkBadPermissions().getSize() < 1 ) {
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
                String msg = "BLE Device Name : " + name + " address : " + address + " appended";
                this.blueDeviceListAdapter.addDevice(device);
                this.blueDeviceListAdapter.notifyDataSetChanged();
                Log.v("sunabove", msg);

                if( this.autoConnect.isChecked() && address.equals( this.getProperty( BLUETOOTH_ADDRESS_KEY))) {

                    this.connectBluetooth( device );
                }
            } else {
                String msg = "BLE Device Name : " + name + " address : " + address + " not appended";
                Log.v("sunabove", msg);
            }
        }
    }
}