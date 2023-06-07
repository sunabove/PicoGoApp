package com.pico;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

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

    protected ComActivity activity ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = this;

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeAsUpIndicator( R.drawable.action_bar_logo_2 );
        actionBar.setHomeAsUpIndicator( R.drawable.picogo_front_round_48);
    }

    public void postDelayed( Runnable runnable, int delayMillis ) {
        new Handler(Looper.getMainLooper()).postDelayed(runnable, delayMillis);
    }

}
