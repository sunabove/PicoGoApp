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

        boolean useActionBar = false ;

        if( useActionBar ) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.pico_bot_48);
        }
    }

    public void postDelayed( Runnable runnable, int delayMillis ) {
        new Handler(Looper.getMainLooper()).postDelayed(runnable, delayMillis);
    }

}
