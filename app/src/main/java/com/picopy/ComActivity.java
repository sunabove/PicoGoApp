package com.picopy;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Map;

public abstract class ComActivity extends AppCompatActivity implements ComInterface {

    protected final Sys sys = Sys.getSys();

    protected static ComActivity activityBefore ;

    protected ComActivity activity ;

    public boolean paused ;
    protected int startCount;
    protected int resumeCount = 0 ;
    protected int permissionCheckCount = 0 ;
    private String [] allPermissions ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = this;

        if( this.isHideSupporingActionBar() ) {
            this.getSupportActionBar().hide();
        }

        boolean useActionBar = false ;

        if( useActionBar ) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.pico_bot_48);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.paused = false;

        this.startCount += 1;

        Log.v( tag, "onStart() startCount = " + this.startCount );
    }

    @Override
    protected void onPause() {
        super.onPause();

        this.paused = true;

        Log.i( tag, "onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.paused = false;
        this.resumeCount += 1 ;
        this.permissionCheckCount = 0 ;

        Log.v( tag, "onResume() resumeCount = " + this.resumeCount );
    }

    public void onBackPressed() {
        super.onBackPressed();

        ComActivity.activityBefore = this;

        Log.i( tag, "onBackPressed()");
    }

    public boolean isHideSupporingActionBar() {
        return true;
    }

    public void postDelayed( Runnable runnable ) {
        this.postDelayed(runnable, 0 );
    }

    public void postDelayed( Runnable runnable, int delayMillis ) {
        new Handler(Looper.getMainLooper()).postDelayed(runnable, delayMillis);
    }

    public String [] getAllPermissions() {
        // It turned out that since Android 11 ACCESS_BACKGROUND_LOCATION permission
        // shouldn't be requested alongside with other permissions.

        if( null == this.allPermissions ) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                this.allPermissions = new String[]{
                        android.Manifest.permission.BLUETOOTH,
                        android.Manifest.permission.BLUETOOTH_ADMIN,

                        android.Manifest.permission.BLUETOOTH_CONNECT,
                        android.Manifest.permission.BLUETOOTH_SCAN,

                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,

                       // android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,  // removed. should be located at last
                };
            } else {
                this.allPermissions = new String[]{
                        android.Manifest.permission.BLUETOOTH,
                        android.Manifest.permission.BLUETOOTH_ADMIN,

                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                };
            }
        }

        return this.allPermissions ;
    }

    public StringList checkBadPermissions() {

        this.permissionCheckCount += 1 ;

        StringList badPermissions = new StringList();

        String [] allPermission = this.getAllPermissions();

        for (String permission : allPermission) {
            boolean permitted = ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;

            if ( ! permitted ) {
                badPermissions.add( permission );
                Log.i( tag, String.format( "[%3d] permission check = %s, %s ", permissionCheckCount, permission, "" + permitted ) );
            }
        }

        return badPermissions ;
    }

    private ActivityResultLauncher<String[]> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {

                    boolean isAllGranted = true;

                    for (Boolean isGranted : result.values()) {
                        Log.d( tag, "onActivityResult: isGranted: " + isGranted);
                        isAllGranted = isAllGranted && isGranted;
                    }

                    if (isAllGranted) {
                        Log.d(tag, "onActivityResult: All permissions granted.");

                        whenAllPermissionsGranted();
                    } else {
                        //All or some Permissions were denied so can't do the task that requires that permission
                        Log.d(tag, "onActivityResult: All or some permissions denied.");

                        boolean isCanceled = false ;
                        whenPermissionIsNotPermitted( isCanceled );
                    }
                }
            }
    );

    public void requestPermissions( String permission ) {
        String [] permissions = { permission } ;

        this.requestPermissions( permissions );
    }

    public void requestPermissions() {
        String [] permissions = checkBadPermissions().toArray() ;

        this.requestPermissions( permissions );
    }

    public void requestPermissions( String [] permissions ) {
        Log.v( tag, "requestPermissions()" );

        Runnable okRunnable = new Runnable() {
            @Override
            public void run() {
                permissionLauncher.launch( permissions );
            }
        };

        Runnable cancelRunnable = new Runnable() {
            @Override
            public void run() {
                boolean isCanceled = true ;
                whenPermissionIsNotPermitted( isCanceled );
            }
        };

        this.showPermissionRequestDialog( okRunnable, cancelRunnable );
    } // requestPermissions

    public void whenPermissionIsNotPermitted( boolean isCanceled ) {
        String title = isCanceled ? "권한 설정 취소" : "권한 설정 실패" ;
        String message = isCanceled ? "앱을 다시 실행하여 권한을 허용하여주세요." : "앱을 다시 설치하여 권한을 재설정하세요.";

        final TextView status = this.findViewById(R.id.status) ;

        if( null != status ) {
            status.setText(message);
        }

        this.showMessageDialog( title, message );
    }

    public void whenAllPermissionsGranted() {
        Log.d( tag, "whenAllPermissionsGranted() " + this.getClass().getSimpleName() ) ;
    }

    public void showPermissionRequestDialog( Runnable okRunnable, Runnable cancelRunnable ) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( this );

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_permissions, null);

        builder.setView( dialogView );
        builder.setCancelable( false ); // set modal dialog

        Button okButton = dialogView.findViewById(R.id.dialog_ok_btn) ;
        Button cancelButton = dialogView.findViewById(R.id.dialog_cancel_btn) ;

        AlertDialog dialog = builder.create();

        okButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if( null != okRunnable ) {
                    okRunnable.run();
                }
            }
        });

        cancelButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if( null != cancelRunnable ) {
                    cancelRunnable.run();
                }
            }
        });

        dialog.show();
    } // showPermissionRequestDialog

    public void saveProperty( String key, String value ) {
        SharedPreferences sharedPref = this.activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString( key, value );
        editor.apply();

        Log.i( tag, "Property save: key = " + key + " value = " + value );
    }

    public String getBluetoothAddressLastConnected() {
        return this.getProperty( BLUETOOTH_ADDRESS_KEY );
    }

    public String getBluetoothPairedCodeProperty(String address) {
        String blueToothPairedAddressKey = BLUETOOTH_PAIRING_KEY + address ;

        return this.getProperty( blueToothPairedAddressKey );
    }

    public void saveBluetoothPairedCodeProperty(String address) {
        String blueToothPairedAddressKey = BLUETOOTH_PAIRING_KEY + address ;

        Log.v( tag, "saveBluetoothPairedCodeProperty() key = " + blueToothPairedAddressKey ); 

        this.saveProperty( blueToothPairedAddressKey, "true" );
    }

    public boolean isBluetoothPaired( String address ) {
        String pairedCode = this.getBluetoothPairedCodeProperty( address );

        if( null == pairedCode || pairedCode.trim().length() < 1 ) {
            return false;
        } else if( null != pairedCode ){
            pairedCode = pairedCode.trim();

            if( pairedCode.equalsIgnoreCase( "true" ) || pairedCode.equalsIgnoreCase( "0") ) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    public String getBluetoothName( BluetoothDevice device ) {
        String name = "" ;

        String permission = Manifest.permission.BLUETOOTH_CONNECT ;
        if (ActivityCompat.checkSelfPermission( activity, permission ) == PackageManager.PERMISSION_GRANTED) {
            name = device.getName();
        } else if( shouldShowRequestPermissionRationale( permission ) ) {
            String title = "블루투스 접근 권한 필요" ;
            String text = "블루투스를 검색 및 연결을 위한 권한 승인이 필요합니다." ;

            this.showMessageDialog( title, text );
        } else {
            this.requestPermissions( permission );
        }

        return name;
    }

    public String getProperty( String key ) {
        return this.getProperty( key, "" );
    }

    public String getProperty( String key, String def ) {
        SharedPreferences sharedPref = this.activity.getPreferences(Context.MODE_PRIVATE);

        String value = sharedPref.getString( key, def );

        Log.i( tag, "Property read : key = " + key + " value = " + value );

        return value;
    }

    public int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public void justifyLogoImageHeight( ImageView logoImage ) {
        int sw = this.getScreenWidth();
        int sh = this.getScreenHeight();

        int h = (int) ( Math.min( sw, sh )*0.6);

        logoImage.getLayoutParams().height = h ;
        logoImage.setMaxHeight( h );

        Log.v( tag, "sw = " + sw + ", sh = " + sh + ", h = " + h );
    }

    public Context getContext() {
        Context context = null;

        try {
            context = this.getApplicationContext() ;
        } catch ( Exception e ) {
            Log.d( tag, "Cannot get context." ) ;
        }

        return context ;
    }

    public void showMessageDialog( String title, String message ) {
        Runnable runnable = null ;
        this.showMessageDialog( title, message, runnable );
    }

    public void showMessageDialog( String title, String message, Runnable runnable ) {
        AlertDialog.Builder builder = new AlertDialog.Builder( this );

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_message, null);

        builder.setView( dialogView );
        builder.setCancelable( false ); // set modal dialog

        ImageView image = dialogView.findViewById(R.id.message_dialog_image ) ;
        TextView titleTextView = dialogView.findViewById(R.id.message_dialog_title) ;
        TextView messageTextView = dialogView.findViewById(R.id.message_dialog_message) ;
        Button okButton = dialogView.findViewById(R.id.dialog_ok_btn) ;

        titleTextView.setText( title );
        messageTextView.setText( message );

        AlertDialog dialog = builder.create();

        okButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if( null != runnable ) {
                    runnable.run();
                }
            }
        });

        dialog.show();
    }

}
