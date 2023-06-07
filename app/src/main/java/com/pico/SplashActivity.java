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

import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;

public class SplashActivity extends ComActivity  {

    private boolean paused = false ;
    private Button permissionButton;
    private ImageView logoImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        this.permissionButton = this.findViewById(R.id.permissionButton);
        this.logoImage = this.findViewById(R.id.logoImage);

        this.setTitle( " ★☆ 안녕하세요? 반갑습니다.");

        this.logoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToNextActivity( 100 );
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("sunabove", "onResume");

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
    public void onBackPressed() {
        super.onBackPressed();
        Log.v("sunabove", "onBackPressed()");
    }

    @Override
    protected void onPause() {
        super.onPause();

        this.paused = true ;

        Log.v("sunabove", "onPause");
    }

    final String[] allPermission = new String[]{
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
        String text = "";

        for (String perm : allPermission) {
            boolean permitted = ActivityCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED;

            index += 1;

            if ( ! permitted ) {
                Log.i("sunabove", "permission check = " + perm + ", " + permitted);

                permissionButton.setText( "권한 승인 실패 : " + text );
                permissionButton.setEnabled( false );

                return index;
            } else {
                text += ( index + 1 ) + " ";
            }
        }

        permissionButton.setText( "권한 승인 완료" );
        permissionButton.setEnabled( true );

        return -1;
    }

    public void requestPermissions(int index) {
        Log.v("sunabove", "requestPermissions");

        String perm = allPermission[index];
        boolean permitted = ActivityCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED;

        if (!permitted) {
            Log.i("sunabove", "permission request = " + perm + ", " + permitted);

            ActivityCompat.requestPermissions(this, new String[]{perm}, index);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int index = checkBadPermissionIndex();

        if( index > -1 ) {
            requestPermissions( index );
        } else if( false ){
            String title = "블루투스 권한 설정 성공" ;
            String message = "블루투스를 검색할 수 있습니다.";

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle( title );
            builder.setMessage( message );
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    moveToNextActivity( 2500 );
                }
            });

            builder.show();
        } else {
            this.moveToNextActivity( 2500 );
        }
    }

    private void moveToNextActivity( int delayMillis ) {
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