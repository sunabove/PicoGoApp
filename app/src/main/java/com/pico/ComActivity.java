package com.pico;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public abstract class ComActivity extends AppCompatActivity implements ComInterface {

    protected final Sys sys = Sys.getSys();

    protected static ComActivity activityBefore ;

    protected ComActivity activity ;
    protected int startCount;
    protected int resumeCount = 0 ;
    public boolean paused ;

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

    private String [] getAllPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return new String[]{
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.BLUETOOTH_ADMIN,

                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,

                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.BLUETOOTH_SCAN,
            };
        } else {
            return new String[]{
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.BLUETOOTH_ADMIN,

                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
            };
        }
    }

    public StringList checkBadPermissions() {

        StringList badPermissions = new StringList();

        String [] allPermission = this.getAllPermissions();

        for (String perm : allPermission) {
            boolean permitted = ActivityCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED;

            if ( ! permitted ) {
                badPermissions.add( perm );
                Log.i( tag, "permission check = " + perm + ", " + permitted);
            }
        }

        return badPermissions ;
    }

    public void requestPermissions(StringList badPermissions) {
        Log.i( tag, "requestPermissions");

        String reqPermissions [] = { badPermissions.get(0) };
        ActivityCompat.requestPermissions( this, reqPermissions, 0 );
    }

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

    public String getBluetoothPairedCode(String address) {
        return this.getProperty( BLUETOOTH_PAIRING_KEY + address );
    }

    public boolean isBluetoothPaired( String address ) {
        String pairedCode = this.getBluetoothPairedCode( address );

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

}
