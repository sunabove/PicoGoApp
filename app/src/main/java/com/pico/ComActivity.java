package com.pico;

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public abstract class ComActivity extends AppCompatActivity implements ComInterface {

    protected final Sys sys = Sys.getSys();

    protected ComActivity activity ;
    protected int startCount;
    protected int resumeCount = 0 ;
    public boolean paused ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = this;

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

    protected void onResume() {
        super.onResume();

        this.paused = false;
        this.resumeCount += 1 ;

        Log.v( tag, "onResume() resumeCount = " + this.resumeCount );
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
}
