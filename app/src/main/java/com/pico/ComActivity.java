package com.pico;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public abstract class ComActivity extends AppCompatActivity implements ComInterface {

    protected final Sys sys = Sys.getSys();

    protected ComActivity activity ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = this;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeAsUpIndicator( R.drawable.action_bar_logo_3 );
        actionBar.setHomeAsUpIndicator( R.drawable.picogo_front_round_48);
    }

    public void postDelayed( Runnable runnable, int delayMillis ) {
        new Handler(Looper.getMainLooper()).postDelayed(runnable, delayMillis);
    }

}
