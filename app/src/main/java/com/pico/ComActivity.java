package com.pico;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public abstract class ComActivity extends AppCompatActivity implements ComInterface {

    protected final Sys sys = Sys.getSys();

    protected ComActivity activity ;
    protected int startCount;
    protected boolean paused ;

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
        Log.v( tag, "onResume");

        this.paused = false;
    }

    public void postDelayed( Runnable runnable, int delayMillis ) {
        new Handler(Looper.getMainLooper()).postDelayed(runnable, delayMillis);
    }
}
