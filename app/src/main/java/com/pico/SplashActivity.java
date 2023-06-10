package com.pico;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.core.app.ActivityCompat;

public class SplashActivity extends ComActivity  {

    private boolean paused = false ;
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
                whenLogoImageClicked(view);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v("sunabove", "onStart");

        paused = false ;

        this.permissionButton.setText( "" );

        int index = this.checkBadPermissionIndex();

        if( index > -1 ) {
            this.requestPermissions( index );
        } else {
            this.moveToNextActivity( 2500 );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("sunabove", "onStart");

        // Do nothing, please!
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Log.v("sunabove", "SplashActivity onBackPressed()");
    }

    @Override
    protected void onPause() {
        super.onPause();

        this.paused = true ;

        Log.i("sunabove", "onPause");
    }

    public void whenLogoImageClicked(View view) {

        int badPermIdx = this.checkBadPermissionIndex();

        Log.i("sunabove", "whenLogoImageClicked perm badPermIdx = " + badPermIdx );

        if( badPermIdx < 0 ) {
            moveToNextActivity(100);
        }
    } // -- whenLogoImageClicked

    private final String[] allPermission = new String[]{
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN,

            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,

            android.Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
    };

    public int checkBadPermissionIndex() {
        int index = -1;

        Button permissionButton = this.permissionButton;

        for (String perm : allPermission) {
            boolean permitted = ActivityCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED;

            index += 1;

            if ( ! permitted ) {
                Log.i("sunabove", "permission check = " + perm + ", " + permitted);

                permissionButton.setText( "권한 설정 실패 " );
                permissionButton.setEnabled( false );

                return index;
            }
        }

        permissionButton.setText( "권한 설정 완료" );
        permissionButton.setEnabled( true );

        return -1;
    }

    public void requestPermissions(int index) {
        Log.i("sunabove", "requestPermissions");

        String perm = allPermission[index];
        boolean permitted = ActivityCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED;

        if ( ! permitted ) {
            Log.i("sunabove", "permission request = " + perm + " " + permitted );

            ActivityCompat.requestPermissions(this, new String[]{perm}, index);
        }
    }

    private int prePermReqIdx = -2 ;
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int index = checkBadPermissionIndex();

        Log.i( "sunabove", "badPermmisionIndex = " + index + ", prePermReqIdx = " + this.prePermReqIdx );

        if( index > -1 && this.prePermReqIdx != index ) {
            this.prePermReqIdx = index;

            requestPermissions( index );
        } else if( false && index > -1 && this.prePermReqIdx == index ){
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
        } else if( index < 0 ){
            this.moveToNextActivity( 2500 );
        }
    }

    private void moveToNextActivity( int delayMillis ) {
        Log.v( "sunabove", "moveToNextActivity" );

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            public void run() {
                if ( ! paused ) {
                    //Class klass = ControlActivity.class;
                    Class klass = TabActivity.class;

                    startActivity(new android.content.Intent(SplashActivity.this, klass ));
                }
            }
        }, delayMillis);
    }
}