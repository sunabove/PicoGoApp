package com.pico;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class SplashActivity extends ComActivity  {

    private boolean paused = false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.v("sunabove", "onResume");

        paused = false ;

        this.moveToNextActivity();
    }

    @Override
    protected void onPause() {
        super.onPause();

        this.paused = true ;

        Log.v("sunabove", "onPause");
    }

    private void moveToNextActivity() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            public void run() {
                if ( ! paused ) {
                    startActivity(new android.content.Intent(SplashActivity.this, DriveActivity.class));
                }
            }
        }, 2000);
    }
}