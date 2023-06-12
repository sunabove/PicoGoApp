package com.pico;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class ComFragment extends Fragment implements ComInterface, SysListener {

    protected final Sys sys = Sys.getSys();

    protected TabActivity activity ;
    private ImageView commStatusImage;
    private ProgressBar reconnectProgressBar ;
    private Button reconnectButton;
    private EditText commStatus;

    protected boolean paused = false ;
    protected int startCount = 0 ;
    protected int resumeCount = 0 ;

    public ComFragment() {
        // do nothing!
    }

    @Override
    public void onStart() {
        super.onStart();

        this.paused = false ;

        Log.v( tag, "onStart() startCount = " + this.startCount );

        if( this.startCount < 1 ) {
            this.initFragment();
        }

        this.startCount += 1;

        this.sendHelloMessage( );
    }

    @Override
    public void onPause() {
        super.onPause();

        this.paused = true;
    }

    @Override
    public void onResume() {
        super.onResume();

        this.paused = false;
        this.resumeCount += 1;

        ProgressBar reconnectProgressBar = this.reconnectProgressBar;

        if( reconnectProgressBar != null ) {
            reconnectProgressBar.setVisibility( View.GONE );
        }
    }

    public void initFragment() {

        if( null == this.activity ) {
            this.activity = (TabActivity) this.getActivity();
        }

        if( null == this.commStatus) {
            this.commStatus = this.findViewById(R.id.commStatus);
        }

        if( null == this.commStatusImage) {
            this.commStatusImage = this.findViewById(R.id.commStatusImage);
        }

        if( null== this.reconnectProgressBar ) {
            this.reconnectProgressBar = this.findViewById( R.id.reconnectProgressBar );
        }

        if( null == this.reconnectButton ) {
            this.reconnectButton = this.findViewById(R.id.reconnectButton);

            if( null != this.reconnectButton ) {
                this.reconnectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        whenReconnectButtonClicked(view);
                    }
                });
            }
        }

    }

    private void whenReconnectButtonClicked(View view) {
        ImageView commStatusImage = this.commStatusImage;
        ProgressBar reconnectProgressBar = this.reconnectProgressBar;

        if( null != reconnectProgressBar ) {
            reconnectProgressBar.setVisibility( View.VISIBLE );
        }

        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                whenReconnectButtonClickedImpl(view);
            }
        }, 500);
    }

    private void whenReconnectButtonClickedImpl(View view) {
        Sys sys = this.sys;

        ImageView commStatusImage = this.commStatusImage;
        ProgressBar reconnectProgressBar = this.reconnectProgressBar;

        boolean success = false ;

        if( null != reconnectProgressBar ) {
            reconnectProgressBar.setVisibility( View.VISIBLE );
        }

        if( ! success && ! paused ) {
            success = sys.reConnectBluetoothDevice();
        }

        if( ! success && ! paused ) {
            success = sys.reConnectBluetoothDevice();
        }

        if( ! success && ! paused ) {
            success = sys.reConnectBluetoothDevice();
        }

        if( null != commStatusImage ) {
            commStatusImage.setVisibility( View.VISIBLE );
        }
        if( reconnectProgressBar != null ) {
            reconnectProgressBar.setVisibility( View.GONE );
        }

        if( ! success && ! paused ) {
            this.moveToFragment(0);
        } else {
            this.sendHelloMessage();
        }
    }

    public void sendHelloMessage() {
        this.sendMessage( "hello" );
    }

    public void sendRgbLedMessage(int color) {

        String message = "{\"RGB\": \"%d,%d,%d\"}";

        int red = Color.red( color );
        int blue = Color.blue( color );
        int green = Color.green( color );

        message = String.format( message, red, green, blue );

        this.sendMessage(message);
    }

    private boolean sendingBeep = false ;

    public void sendWelcomeBeep( ) {
        if( sendingBeep ) {
            return;
        }

        int delay = 500 ;

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            private int count = 0 ;
            private int maxCount = 6;
            @Override
            public void run() {
                if( count < maxCount ) {
                    sendingBeep = true;

                    if (count % 2 == 0) {
                        sendBuzzerMessage( true );
                    } else {
                        sendBuzzerMessage( false );
                    }

                    handler.postDelayed(this, delay);

                    count += 1 ;
                } else {
                    sendingBeep = false;
                }
            }
        };

        handler.postDelayed( runnable, delay);
    }

    public boolean sendMotorStopMessage() {
        String message = "{\"Forward\":\"Up\"}" ;

        return sendMessage( message );
    }

    public boolean sendBuzzerMessage( boolean on) {
        String message = "{\"BZ\":\"%s\"}";
        message = String.format( message, on ? "on" : "off" );

        return sendMessage( message );
    }

    public boolean sendMessage( String message ) {
        boolean result = sys.sendMessage(message);

        if( result ) {
            this.whenSysSucceeded();
        } else {
            this.whenSysFailed();
        }

        return result;
    }

    @Override
    public void whenSysSucceeded() {
        Log.v( tag, "whenSysSucceeded()" );

        if( this.commStatus != null ) {
            this.commStatus.setText("블루투스 통신 성공");
            this.commStatus.setTextColor( greenLight );

            this.reconnectButton.setVisibility(View.GONE);
            this.commStatusImage.setImageResource( R.drawable.bluetooth_succ_64);
        }
    }

    @Override
    public void whenSysFailed() {
        Log.v( tag, "whenSysFailed()" );

        if( null != this.commStatus) {
            String message = "블루투스 통신 실패: 재접속하여 주세요.";
            if( sys.getBluetoothDevice() == null ) {
                message = "블루투스를 먼저 연결하세요.";
            }

            this.commStatus.setText( message );
            this.commStatus.setTextColor( red );

            this.reconnectButton.setVisibility(View.VISIBLE);
            this.commStatusImage.setImageResource( R.drawable.bluetooth_fail_64);
        }
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return (T) this.getView().findViewById(id);
    }

    protected void moveToFragment(int navIdx ) {
        TabActivity activity = (TabActivity) this.getActivity();

        if( null != activity && ! paused ) {
            BottomNavigationView navView = activity.findViewById(R.id.nav_view);

            navView.setSelectedItemId(activity.navigationIds[navIdx]);
        }
    }

    public void postDelayed( Runnable runnable ) {
        this.postDelayed(runnable, 0 );
    }

    public void postDelayed( Runnable runnable, int delayMillis ) {
        new Handler(Looper.getMainLooper()).postDelayed(runnable, delayMillis);
    }

    public void saveProperty( String key, String value ) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString( key, value );
        editor.apply();

        Log.i( tag, "Property save: key = " + key + " value = " + value );
    }

    public String getProperty( String key ) {
        return this.getProperty( key, "" );
    }

    public String getProperty( String key, String def ) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        String value = sharedPref.getString( key, def );

        Log.i( tag, "Property read : key = " + key + " value = " + value );

        return value;
    }

}
