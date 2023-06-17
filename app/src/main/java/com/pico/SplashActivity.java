package com.pico;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
                boolean aniRotation = false ;
                whenLogoImageClicked(view, aniRotation, 500);
            }
        });

        this.justifyLogoImageHeight( this.logoImage );
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.logoImage.clearAnimation();

        if( null == ComActivity.activityBefore ) {
            boolean aniRotation = true;
            whenLogoImageClicked( logoImage, aniRotation, 2500 );
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

        this.logoImage.clearAnimation();
    }

    private boolean logoImageClicked = false ;

    public void whenLogoImageClicked(View view, boolean aniRotation, int delay) {

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