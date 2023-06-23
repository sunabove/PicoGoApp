package com.picopy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

public class SplashActivity extends ComActivity  {

    private Button permissionButton;
    private ImageView logoImage;
    private TextView status;
    private TextView versionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        this.permissionButton = this.findViewById(R.id.permissionButton) ;
        this.logoImage = this.findViewById(R.id.logoImage) ;
        this.status = this.findViewById(R.id.commStatus) ;
        this.versionCode = this.findViewById(R.id.versionCode) ;

        this.setTitle( " ★ 안녕하세요? 반갑습니다. ★ ");

        try {
            Context context = this.getApplicationContext();
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String versionName = pInfo.versionName;
            int versionCode = pInfo.versionCode;

            if( null != this.versionCode ) {
                this.versionCode.setText( "" + versionCode );
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        this.logoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean aniRotation = false ;
                whenLogoImageClicked( aniRotation, 500);
            }
        });

        this.justifyLogoImageHeight( this.logoImage );
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v( tag, "onResume() " + this.getClass().getSimpleName() );

        this.logoImage.clearAnimation();

        if( null == ComActivity.activityBefore ) {
            boolean aniRotation = true;
            whenLogoImageClicked( aniRotation, 2000 );
        }

        ComActivity.activityBefore = this ;
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

        this.logoImage.clearAnimation();
    }

    private boolean logoImageClicked = false ;

    public void whenLogoImageClicked(boolean aniRotation, int delay) {
        Log.v( tag, "whenLogoImageClicked()" ) ;

        if( this.logoImageClicked ) {
            return;
        }

        this.logoImageClicked = true;

        try {
            Button permissionButton = this.permissionButton;
            permissionButton.setText("");

            StringList badPermissions = this.checkBadPermissions();

            if (badPermissions.size() > 0) {
                permissionButton.setText("권한 설정 중");
                permissionButton.setEnabled(false);

                int index = 0 ;
                this.requestPermissions( index );
            } else {
                permissionButton.setText("권한 설정 완료");
                permissionButton.setEnabled(true);

                ImageView logoImage = this.logoImage;
                int dir = 1 ; int count = 0 ;
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        moveToNextActivity( delay );
                    }
                } ;
                if( aniRotation ) {
                    this.animateRotation(logoImage, dir, count, runnable);
                } else {
                    this.animateTranslation(logoImage, -dir, runnable );
                }

            }
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            this.logoImageClicked = false;
        }
    } // -- whenLogoImageClicked

    public void requestPermissions(int index) {
        Log.v( tag, "requestPermissions() index = " + index );

        if( index < 1 ) {
            Runnable okRunnable = new Runnable() {
                @Override
                public void run() {
                    requestPermissionsImpl( index ) ;
                }
            };

            Runnable cancelRunnable = new Runnable() {
                @Override
                public void run() {
                    boolean isCanceled = true ;
                    whenPermissionIsNotPermitted( isCanceled );
                }
            };

            this.showPermissionRequestDialog( okRunnable, cancelRunnable );
        } else {
            requestPermissionsImpl( index ) ;
        }
    } // requestPermissions

    public void requestPermissionsImpl(int index) {
        Log.v( tag, "requestPermissionsImpl() index = " + index );

        String allPermissions [] = this.getAllPermissions();

        for( int i = index ; i < allPermissions.length ; i ++ ) {
            final String permission = allPermissions[ i ];
            final boolean permitted = ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;

            Log.v( tag, String.format( "[%2d] req permission = %s, permitted = %s", i + 1, permission, "" + permitted ) );

            if( ! permitted ) {
                final int requestCode = i;

                Log.v( tag, String.format( "[%2d] requesting a permission = %s, requestCode = %d", i + 1, permission, requestCode ) ) ;

                ActivityCompat.requestPermissions( this, new String[] { permission }, requestCode );

                i = allPermissions.length ;

                break ;
            }
        }
    } // requestPermissionsImpl

    public void showPermissionRequestDialog( Runnable okRunnable, Runnable cancelRunnable ) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( this );

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_permissions, null);

        builder.setView( dialogView );
        builder.setCancelable( false ); // set modal dialog

        Button okButton = dialogView.findViewById(R.id.dialog_ok_btn) ;
        Button cancelButton = dialogView.findViewById(R.id.dialog_ok_btn) ;

        AlertDialog dialog = builder.create();

        okButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if( null != okRunnable ) {
                    okRunnable.run();
                }
            }
        });

        cancelButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if( null != cancelRunnable ) {
                    cancelRunnable.run();
                }
            }
        });

        dialog.show();
    } // showPermissionRequestDialog

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean permitted = true;
        boolean showDialog = false;
        final TextView status = this.status ;

        for( int i = 0; i < grantResults.length ; i ++ ) {
            if( grantResults[i] != PackageManager.PERMISSION_GRANTED ) {
                permitted = false;

                Log.v( tag, "OnRequestPermissionResult " + permissions[i] + " grantResult : " + grantResults[i] );
            }
        }

        StringList badPermission = this.checkBadPermissions() ;

        if( permitted && badPermission.size() > 0 && requestCode < this.getAllPermissions().length -1 ) {
            // when not all permissions are granted
            int index = requestCode + 1 ;
            this.requestPermissions( index );
        } else if( permitted && badPermission.size() < 1 ) { // when all permissions are granted.

            String title = "권한 설정 성공" ;
            String message = "권한 설정이 완료되었습니다.";

            if( null != status ) {
                status.setText(message);
            }

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    boolean aniRotation = false ;
                    whenLogoImageClicked( aniRotation, 1000 );
                }
            } ;

            this.showMessageDialog( title, message, runnable );

        } else if( ! permitted ) { // when a permission is not permitted
            boolean isCanceled = false ;
            this.whenPermissionIsNotPermitted( isCanceled );
        }
    }

    private void whenPermissionIsNotPermitted( boolean isCanceled ) {
        String title = isCanceled ? "권한 설정 취소" : "권한 설정 실패" ;
        String message = "앱을 재설치후에 재설정 하세요.";

        final TextView status = this.status ;

        if( null != status ) {
            status.setText(message);
        }

        this.showMessageDialog( title, message );
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

    private int mode = 0;

    private void animateRotation(ImageView imageView, final int dir, final int count, Runnable runnableAfterAnimation ) {

        final int maxCount = 1;

        int duration360 = 20_000;

        int angleDegree = 20 ;

        int frDegree = dir > 0 ? angleDegree : - angleDegree ;
        int toDegree = count == maxCount ? 0 : -frDegree ;

        int duration = duration360*Math.abs( toDegree - frDegree)/360;

        int relative = Animation.RELATIVE_TO_SELF ;
        Animation animation = new RotateAnimation( frDegree, toDegree,
                relative, 0.5f, relative, 0.5f);

        animation.setDuration( duration );
        animation.setRepeatCount( 0 );
        animation.setFillAfter( true );

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if( count >= maxCount ) {
                    animateTranslation( imageView, -1, runnableAfterAnimation );
                } else {
                    animateRotation( imageView, -dir, count + 1, runnableAfterAnimation );
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        imageView.startAnimation( animation );
    } // -- animateLogoRotate

    private void animateTranslation(ImageView imageView, final int dir, Runnable runnable ) {
        //imageView.clearAnimation();

        // logo animation
        int relative = Animation.RELATIVE_TO_SELF ;
        TranslateAnimation animation = new TranslateAnimation(
                relative,  dir < 0 ? 0.0f : -0.25f,
                relative, dir < 0 ? -0.25f : 0.0f,
                relative,  0.0f,
                relative,  0.0f);

        long duration = 1_000;

        animation.setDuration(duration);
        animation.setRepeatCount( 0 );
        //animation.setRepeatMode(Animation.RESTART);

        animation.setAnimationListener( new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.v( tag, "animationTranslation dir "  + dir );

                if( dir < 0 ) {
                    animateTranslation( imageView, - dir, runnable );
                } else if( dir >= 0 ) {
                    postDelayed( runnable );
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        imageView.startAnimation(animation);
    } // -- animateLogoTranslate
}