package com.pico;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class ComActivity extends AppCompatActivity implements ComInterface {

    protected static final int gray = Color.parseColor("#d3d3d3") ;
    protected static final int yellow = Color.parseColor("#ffff00") ;
    protected static final int green = Color.parseColor("#00FF00") ;
    protected static final int black = Color.parseColor("#FFFFFF") ;
    protected static final int red = Color.parseColor("#FF0000") ;

    protected static class Motion {
        public static final String FORWARD = "FORWARD" ;
        public static final String BACKWARD = "BACKWARD" ;

        public static final String LEFT = "LEFT" ;
        public static final String RIGHT = "RIGHT" ;

        public static final String STOP = "STOP" ;

        public static final String AUTOPILOT = "AUTOPILOT" ;
    }

    protected Context context ;

    protected SharedPreferences sharedPref = null;

    protected boolean motionEnabled = false ;

    protected RequestQueue requestQueue = null;

    protected FloatingActionButton goBack ;

    public abstract int getLayoutId() ;

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView( this.getLayoutId() );

        this.context = this.getApplicationContext();

        if( null == sharedPref ) {
            sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);
        }

        this.requestQueue = Volley.newRequestQueue(this);

        this.goBack = null ; // this.findViewById(R.id.goBack);

        if( null != goBack ) {
            this.goBack.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    TextView status = null; // findViewById(R.id.status);

                    if( null != status ) {
                        status.setText( "이전 화면으로 돌아갑니다." );
                    }

                    new Handler( Looper.getMainLooper() ).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 이전 화면으로 돌아감.
                            finish();
                        }
                    }, 300);
                }
            });
        }
    }

    public double prettyDegree( double degree ) {
        degree = degree % 360 ;

        if( 180 < degree ) {
            degree = degree - 360 ;
        }
        return degree ;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return (T) super.findViewById(id);
    }

    public void postDelayed(Runnable r, int delayMillis) {
        new Handler(Looper.getMainLooper()).postDelayed( r, delayMillis);
    }

    public void sleep( long millis ) {
        try {
            Thread.currentThread().sleep(millis);
        } catch ( Exception e ) {
            //
        }
    }

    public int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public void hideActionBar() {
        // If the Android version is lower than Jellybean, use this call to hide
        // the status bar.
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            // Remember that you should never show the action bar if the
            // status bar is hidden, so hide that too if necessary.
            ActionBar actionBar = getActionBar();
            if( null != actionBar ) {
                actionBar.hide();
            }
        }
    }

    protected static final int PERMISSION_ID = 44;

    protected boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    protected void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    protected boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    public double prettyAngle60(double angleDegDecimal ) {
        double angle = angleDegDecimal %360 ;

        int ang = (int) angle ;
        double deg = angle - ang ;
        deg = 60*deg;

        angle = angle + deg ;

        return angle ;
    }

}
