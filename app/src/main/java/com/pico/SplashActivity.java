package com.pico;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.core.app.ActivityCompat;

public class SplashActivity extends ComActivity  {

    private Button permissionButton;
    private ImageView logoImage;
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        this.permissionButton = this.findViewById(R.id.permissionButton);
        this.logoImage = this.findViewById(R.id.logoImage);
        this.status = this.findViewById(R.id.commStatus);

        this.setTitle( " ★ 안녕하세요? 반갑습니다. ★ ");

        this.logoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whenLogoImageClicked(view, 500);
            }
        });

        this.justifyLogoImageHeight();
    }

    private void justifyLogoImageHeight() {
        int sw = this.getScreenWidth();
        int sh = this.getScreenHeight();

        int h = (int) ( Math.min( sw, sh )*0.6);

        ImageView logoImage = this.logoImage;
        logoImage.getLayoutParams().height = h ;
        logoImage.setMaxHeight( h );

        Log.v( tag, "sw = " + sw + ", sh = " + sh + ", h = " + h );
    }

    @Override
    protected void onStart() {
        super.onStart();

        if( null == ComActivity.activityBefore ) {
            this.whenLogoImageClicked(this.logoImage, 2500 );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v( tag, "onResume");

        // Do nothing, please!
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        ComActivity.activityBefore = null;

        Log.v( tag, "SplashActivity onBackPressed()");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i( tag, "onPause");
    }

    private boolean logoImageClicked = false ;

    public void whenLogoImageClicked(View view, int delay) {

        if( this.logoImageClicked ) {
            return;
        }

        this.logoImageClicked = true;

        try {
            Button permissionButton = this.permissionButton;
            permissionButton.setText("");

            StringList badPermissions = this.checkBadPermissions();

            if (badPermissions.size() > 0) {
                permissionButton.setText("권한 설정 중 ... ");
                permissionButton.setEnabled(false);

                this.requestPermissions(badPermissions);
            } else {
                permissionButton.setText("권한 설정 완료");
                permissionButton.setEnabled(true);

                this.moveToNextActivity( delay );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            this.logoImageClicked = false;
        }

    } // -- whenLogoImageClicked

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean permitted = true;
        boolean showDialog = false;

        for( int i = 0; i < grantResults.length ; i ++ ) {
            if( grantResults[i] != PackageManager.PERMISSION_GRANTED ) {
                permitted = false;

                Log.v( tag, "OnRequestPermissionResult " + permissions[i] + " grantResult : " + grantResults[i] );
            }
        }

        if( permitted ) {
            StringList badPermissions = this.checkBadPermissions();

            if( badPermissions.size() > 0 ) {
                this.requestPermissions( badPermissions );
            } else {
                this.moveToNextActivity( 2500 );
            }
        } else if(! permitted ){
            String title = "권한 설정 실패" ;
            String message = "권할 설정을 다시 하여 주십시오.";

            status.setText( message );

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle( title );
            builder.setMessage( message );
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    status.setText( message );
                }
            });

            builder.show();
        }
    }

    private void moveToNextActivity( int delayMillis ) {
        Log.v( tag, "moveToNextActivity is requested." );

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            public void run() {
                if( paused ) {
                    Log.v( tag, "moveToNextActivity current activity is paused." );
                } else if ( ! paused ) {
                    //Class klass = ControlActivity.class;
                    Class klass = TabActivity.class;

                    startActivity(new android.content.Intent(SplashActivity.this, klass ));
                }
            }
        }, delayMillis);
    }
}