package com.picopy.ui.bluetooth;

import android.Manifest;
import android.app.Activity;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.picopy.BlueDeviceListAdapter;
import com.picopy.BluetoothInterface;
import com.picopy.ComActivity;
import com.picopy.ComFragment;
import com.picopy.R;
import com.picopy.TabActivity;
import com.picopy.databinding.FragmentBluetoothBinding;

public class BluetoothFragment extends ComFragment implements BluetoothInterface {

    private FragmentBluetoothBinding binding;

    protected boolean scanningBluetoothNow = false;
    private boolean connectingBluetoothNow = false;

    private ProgressBar bluetoothScanProgressBar;
    private ListView bluetoothListView;
    private BlueDeviceListAdapter blueDeviceListAdapter;

    private ToggleButton bluetoothScanButton;
    private CheckBox autoConnectCheckBox;
    private CheckBox scanPicoOnlyCheckBox;

    private ProgressBar connectingProgressBar;
    private EditText connectingStatus;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(tag, "onCreateView()");

        BluetoothViewModel bluetoothViewModel = new ViewModelProvider(this).get(BluetoothViewModel.class);

        binding = FragmentBluetoothBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        this.bluetoothScanProgressBar = binding.bluetoothScanProgressBar;
        this.bluetoothListView = binding.bluetoothListView;
        this.bluetoothScanButton = binding.bluetoothScanButton;
        this.scanPicoOnlyCheckBox = binding.scanPicoOnlyCheckBox;
        this.autoConnectCheckBox = binding.autoConnectCheckBox;

        this.connectingProgressBar = binding.connectingProgressBar;
        this.connectingStatus = binding.connectingStatus;

        this.connectingStatus.setText("");

        this.bluetoothScanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                whenBluetoothScanButtonClicked();
            }
        });

        this.blueDeviceListAdapter = new BlueDeviceListAdapter(this);

        this.bluetoothListView.setAdapter(blueDeviceListAdapter);

        this.bluetoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                whenBluetoothListViewItemClick(adapterView, view, i, l);
            }
        });

        this.autoConnectCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                whenAutoConnectChecked(compoundButton, checked);
            }
        });

        this.scanBluetoothDevices();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        this.sendBuzzerMessage(false);
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.v(tag, "onPause()");

        this.scanningBluetoothNow = false;

        this.connectingStatus.setText("");

        boolean isInterrupted = true;

        this.stopBluetoothScanning(isInterrupted);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.v(tag, "onDestroyView()");

        binding = null;
    }

    private void whenBluetoothScanButtonClicked() {
        ToggleButton blueScanButton = this.bluetoothScanButton;
        Log.d(tag, "whenBluetoothScanButtonClicked() : toggleButton checked = " + blueScanButton.isChecked());

        if (blueScanButton.isChecked()) {
            this.scanBluetoothDevices();
        } else {
            boolean isInterrupted = true;
            this.stopBluetoothScanning(isInterrupted);
        }
    }

    private void whenAutoConnectChecked(CompoundButton compoundButton, boolean checked) {
        String addressLast = this.getProperty(BLUETOOTH_ADDRESS_KEY);

        if (checked && addressLast.length() > 0) {
            BlueDeviceListAdapter blueDeviceListAdapter = this.blueDeviceListAdapter;

            BluetoothDevice device;

            for (int i = 0; i < blueDeviceListAdapter.getCount(); i++) {
                device = blueDeviceListAdapter.getItem(i);

                if (device != null && device.getAddress().equals(addressLast)) {
                    if (connectingBluetoothNow) {
                        Log.v(tag, "connectingBluetoothNow is true.");
                    } else if (!connectingBluetoothNow) {
                        this.connectBluetooth(device);
                    }

                    return;
                }
            }
        }
    }

    private void whenBluetoothListViewItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String msg = "whenBluetoothListViewItemClick() i = " + i + ", l = " + l;

        Log.v(tag, msg);

        BluetoothDevice device = this.blueDeviceListAdapter.getItem(i);

        if (null == device) {
            Log.v(tag, "Bluetooth device is null.");
        } else if (this.connectingBluetoothNow) {
            Log.v(tag, "connectingBluetoothNow is true.");
        }
        if (null != device && !this.connectingBluetoothNow) {
            boolean isInterrupted = true;

            this.stopBluetoothScanning(isInterrupted);

            this.connectBluetooth(device);
        }

    } // -- whenBluetoothListViewItemClick

    private void connectBluetooth(BluetoothDevice device) {

        if (!this.connectingBluetoothNow) {
            this.connectingBluetoothNow = true;

            this.connectingProgressBar.setVisibility(View.VISIBLE);
            this.connectingProgressBar.setIndeterminate(true);

            this.connectingStatus.setTextColor(greenDark);
            this.connectingStatus.setText("블루투스를 연결중입니다.");

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    connectBluetoothImpl(device);
                }
            }, 2_500);
        }

    }

    private boolean pairAlways() {
        return true;
    }

    private void connectBluetoothImpl(BluetoothDevice device) {
        String name = this.getBluetoothName( device );
        String address = device.getAddress();

        String msg = " BLE Device Name : " + name + " address : " + address;

        Log.v(tag, msg);

        this.autoConnectCheckBox.setEnabled(false);

        boolean success = sys.connectBluetoothDevice(device);

        boolean isPaired = this.isBluetoothPaired(address);
        boolean pairAlways = this.pairAlways();

        Log.v(tag, "isPared = " + isPaired + ", pairAlways = " + pairAlways);

        if (success && (pairAlways || isPaired == false)) {
            String paringCode = this.sendSendMeParingCodeMessage();

            Log.v(tag, "pairing Code = " + paringCode);

            this.showParingWindow(paringCode, address);
        } else if (success && isPaired == true) {
            Log.v(tag, "Sipped pairing. pairing has been done.");

            boolean hasParingTried = false;
            this.connectBluetoothImplAfterParing(success, address, hasParingTried);
        }
    }

    private void showParingWindow(final String pairingCode, String address) {
        TabActivity activity = this.activity;

        if (this.paused) {
            Log.d(tag, "activity is paused. skipped showParingWindow()");

            return;
        } else if (activity == null) {
            Log.d(tag, "activity is null. skipped showParingWindow()");

            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        View dialogView = this.getLayoutInflater().inflate(R.layout.dialog_bluetooth_pairing_code, null);

        builder.setView(dialogView);

        final TextView blueToothAddressOfParingCode = dialogView.findViewById(R.id.blueToothAddressOfParingCode);
        final TextView userInput = dialogView.findViewById(R.id.paringCodeUserInput);
        final ImageView invalidParingCode = dialogView.findViewById(R.id.invalidParingCode);
        final TextView paringStatus = dialogView.findViewById(R.id.paringStatus);
        final Button okBtn = dialogView.findViewById(R.id.okBtn);
        final Button cancelBtn = dialogView.findViewById(R.id.cancelBtn);

        userInput.setText("");
        userInput.setTextColor(grey);
        userInput.setHint("####");

        paringStatus.setText("");
        invalidParingCode.setVisibility(View.GONE);
        blueToothAddressOfParingCode.setText(address);
        okBtn.setEnabled(false);
        cancelBtn.setEnabled(true);

        // set modal dialog
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();

        userInput.setOnKeyListener(new View.OnKeyListener() {

            private int preTextLen = 0;

            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                invalidParingCode.setVisibility(View.GONE);
                paringStatus.setText("");
                cancelBtn.setEnabled(true);

                String userInputText = userInput.getText().toString().trim();

                if (pairingCode.equalsIgnoreCase(userInputText)) {
                    userInput.setTextColor(greenLight);
                } else if (pairingCode.startsWith(userInputText)) {
                    userInput.setTextColor(orangeLight);
                } else {
                    userInput.setTextColor(grey);
                }

                if (userInputText.length() < 4) {
                    okBtn.setEnabled(false);
                } else {
                    okBtn.setEnabled(true);
                }

                if (preTextLen == 3 && userInputText.length() == 4) {
                    // hide key board
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }

                this.preTextLen = userInputText.length();

                return false;
            }
        });

        // 확인 버튼 리스너
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelBtn.setEnabled(false);

                String userInputParingCode = userInput.getText().toString().trim();

                boolean success = userInputParingCode.equalsIgnoreCase(pairingCode);

                if (!success) {
                    userInput.setTextColor(redDark);
                    invalidParingCode.setVisibility(View.VISIBLE);

                    paringStatus.setText("페어링 코드가 일치하지 않습니다.");
                } else if (success) {
                    userInput.setTextColor(greenLight);
                    dialog.dismiss();

                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            boolean paringSuccess = true;
                            boolean hasParingTried = true;
                            connectBluetoothImplAfterParing(paringSuccess, address, hasParingTried);
                        }
                    }, 500);
                }

                cancelBtn.setEnabled(true);
            }
        });

        // 취소 버튼 리스너
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userInput.setText("");

                sys.disconnectBluetoothDevice();

                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        boolean paringSuccess = false;
                        boolean hasParingTriedhasParingTried = true;
                        connectBluetoothImplAfterParing(paringSuccess, address, true);
                    }
                }, 500);

                dialog.dismiss();
            }
        });

        dialog.show();

    } // showParingWindow

    private void connectBluetoothImplAfterParing(boolean success, String address, boolean hasParingTried) {

        if (success) {
            this.saveProperty(BLUETOOTH_ADDRESS_KEY, address);

            this.connectingProgressBar.setVisibility(View.VISIBLE);
            this.connectingProgressBar.setIndeterminate(true);

            this.connectingStatus.setTextColor(greenDark);
            this.connectingStatus.setText("블루투스 연결 성공");

            if (hasParingTried) {
                // 페어링 완료 여부 설정
                this.saveBluetoothPairedCodeProperty(address);

                this.sendParingCompletedMessage();
            }

            if (activity.paused) {
                Log.v(tag, "activity is paused. cannot move to fragment");
            } else if (!activity.paused) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int navIdx = 1;
                        moveToFragment(navIdx);
                    }
                }, 1_000);
            }
        } else {
            this.connectingProgressBar.setVisibility(View.GONE);
            this.connectingProgressBar.setIndeterminate(false);

            this.connectingStatus.setTextColor(red);

            this.connectingStatus.setText(hasParingTried ? "블루투스 페어링 취소" : "블루투스 연결 실패");
        }

        this.connectingBluetoothNow = false;

        this.autoConnectCheckBox.setEnabled(true);
    }

    public boolean isScanning() {
        return this.scanningBluetoothNow;
    }

    public boolean isScanAll() {
        return !this.scanPicoOnlyCheckBox.isChecked();
    }

    public void scanBluetoothDevices() {
        Log.v(tag, "scanBluetoothDevices()");

        this.bluetoothScanButton.setChecked(true);

        this.bluetoothScanProgressBar.setVisibility(View.VISIBLE);

        this.connectingProgressBar.setIndeterminate(false);
        this.connectingProgressBar.setVisibility(View.GONE);
        this.connectingStatus.setText("");

        this.blueDeviceListAdapter.clear();
        this.blueDeviceListAdapter.notifyDataSetChanged();

        this.scanningBluetoothNow = true;

        this.blueDeviceListAdapter.addDevice(null);
        this.blueDeviceListAdapter.setInterrupted(true);
        this.blueDeviceListAdapter.notifyDataSetChanged();

        scanBluetoothDevicesImpl(); 
    }

    private BroadcastReceiver receiver = null;

    private BroadcastReceiver getReceiver() {
        BroadcastReceiver receiver = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {
                whenBluetoothDeviceScannedByIntent(context, intent);
            }
        };

        return receiver;
    }

    private void whenBluetoothDeviceScannedByIntent(Context context, Intent intent) {
        String action = intent.getAction();

        boolean isInterrupted = true;

        if( action.equals(BluetoothDevice.ACTION_FOUND) ) {
            BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            this.addBluetoothDevice(device);
        } else if( action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED) && scanningBluetoothNow ) {
            scanningBluetoothNow = false;

            isInterrupted = false;

            this.blueDeviceListAdapter.setInterrupted(false);
        }

        if( scanningBluetoothNow == false ) {
            stopBluetoothScanning( isInterrupted );
        }
    }

    public synchronized void stopBluetoothScanning(boolean isInterrupted) {
        Log.d(tag, "stopBluetoothScanning()");

        this.scanningBluetoothNow = false;

        final TabActivity activity = this.activity;

        if( null == activity ) {
            Log.v(tag, "current activity is null.");
        } else if ( null != activity && activity.checkBadPermissions().getSize() < 1 ) {
            BluetoothManager btManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

            if (null != btManager) {
                BluetoothAdapter btAdapter = btManager.getAdapter();

                BroadcastReceiver receiver = this.receiver;

                if (null != this.receiver) {
                    this.receiver = null;

                    try {
                        activity.unregisterReceiver(receiver);
                    } catch ( Exception e ) {
                        // do nothing
                    }
                }

                if (null != btAdapter) {
                    String permission = Manifest.permission.BLUETOOTH_SCAN ;

                    if ( ActivityCompat.checkSelfPermission(activity, permission ) == PackageManager.PERMISSION_GRANTED ) {
                        btAdapter.cancelDiscovery();
                    } else if( this.shouldShowRequestPermissionRationale( permission ) ) {
                        String title = "블루투스 접근 권한 필요" ;
                        String text = "블루투스를 검색 및 연결을 위한 권한 승인이 필요합니다." ;

                        activity.showMessageDialog( title, text );
                    } else {
                        activity.requestPermissions( );
                    }

                }
            }
        }

        blueDeviceListAdapter.notifyDataSetChanged();

        bluetoothScanButton.setEnabled(true);
        bluetoothScanButton.setChecked(false);

        scanPicoOnlyCheckBox.setEnabled(true);

        bluetoothScanProgressBar.setVisibility(View.GONE);
    }

    public void scanBluetoothDevicesImpl() {
        Log.v(tag, "scanBluetoothDevicesImpl()");

        final ComActivity activity = this.getComActivity();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        this.receiver = this.getReceiver();

        if( null == activity ) {
            Log.d( tag, "activity is null." );
        } else if ( null != activity ) {
            activity.registerReceiver( receiver, intentFilter);

            BluetoothManager btManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

            if (null != btManager) {
                BluetoothAdapter btAdapter = btManager.getAdapter();

                if (null != btAdapter) {
                    String permission = Manifest.permission.BLUETOOTH_SCAN;

                    if ( ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED ) {
                        Log.d( tag, "btAdapter.startDiscovery()" ) ;

                        btAdapter.startDiscovery();
                    } else if (this.shouldShowRequestPermissionRationale(permission)) {
                        String title = "블루투스 접근 권한 필요";
                        String text = "블루투스를 검색 및 연결을 위한 권한 승인이 필요합니다.";

                        activity.showMessageDialog(title, text);
                    } else {
                        activity.requestPermissions( );
                    }
                }
            }
        }
    } // -- scanBluetoothDevicesImpl

    public void addBluetoothDevice(BluetoothDevice device) {
        boolean scanAll = this.isScanAll();

        String name = this.getBluetoothName( device ) ;
        String address = device.getAddress();

        if( null != name ) {
            if( scanAll || name.toUpperCase().endsWith( "SPP" ) ) {
                String msg = "BLE Device Name : " + name + " address : " + address + " appended";

                Log.v( tag, msg);

                if( this.autoConnectCheckBox.isChecked() && address.equals( this.getBluetoothAddressLastConnected()) ) {
                    int index = 1;
                    this.blueDeviceListAdapter.addDevice( device, index );
                    this.blueDeviceListAdapter.notifyDataSetChanged();

                    if( connectingBluetoothNow ) {
                        Log.v( tag, "connectingBluetoothNow is true." );
                    } else if( ! connectingBluetoothNow ) {
                        boolean isInterrupted = true;

                        this.stopBluetoothScanning( isInterrupted);

                        this.connectBluetooth(device);
                    }
                } else {
                    this.blueDeviceListAdapter.addDevice(device);
                    this.blueDeviceListAdapter.notifyDataSetChanged();
                }
            } else {
                String msg = "BLE Device Name : " + name + " address : " + address + " not appended";
                Log.v( tag, msg);
            }
        }
    }
}