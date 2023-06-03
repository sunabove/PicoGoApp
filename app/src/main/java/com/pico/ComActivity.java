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

    protected AppCompatActivity activity ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = this;
    }

}
