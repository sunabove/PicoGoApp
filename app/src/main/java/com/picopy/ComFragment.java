package com.picopy;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class ComFragment extends Fragment implements ComInterface, SysListener {

    protected final Sys sys = Sys.getSys();

    protected TabActivity activity ;
    private ImageView commStatusImage;
    private ProgressBar reconnectProgressBar ;
    protected Button reconnectButton;
    private TextView commStatus;

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

        if( this.startCount <= 1 ) {
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

    @Override
    public void onDetach() {
        super.onDetach();

        Log.v(tag, "onDetach() " + this.getClass().getSimpleName());
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

        if( null == this.reconnectProgressBar ) {
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

    public void whenReconnectButtonClicked(View view) {
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

        if( null != reconnectProgressBar ) {
            reconnectProgressBar.setVisibility( View.GONE );
        }

        if( ! success && ! paused ) {
            // 블루투스 연결 탭으로 이동함
            this.moveToFragment(0);
        } else {
            this.sendHelloMessage();
        }
    }

    public void showMessageDialog( String title, String message ) {
        TabActivity activity = this.activity ;

        if( null != activity ) {
            activity.showMessageDialog( title, message );
        }
    }

    public void showMessageDialog( String title, String message, Runnable oKRunnable ) {
        TabActivity activity = this.activity ;

        if( null != activity ) {
            activity.showMessageDialog( title, message, oKRunnable );
        }
    }

    public void showMessageDialog( String title, String message, Runnable oKRunnable, Runnable cancelRunnable ) {
        TabActivity activity = this.activity ;

        if( null != activity ) {
            activity.showMessageDialog( title, message, oKRunnable, cancelRunnable );
        }
    }

    public void sendStartObstacleAvoidanceMessage() {
        this.sendMessage( "start obstacle avoidance" );
    }

    public void sendObtacleDistanceMessage( int maxDist ) {
        this.sendMessage( "ostacle distance = " + maxDist );
    }

    public void sendStartLaneFollowingMessage() {
        this.sendMessage( "start lane following" );
    }

    public void sendStopMessage() {
        this.sendMessage( "stop" );
    }

    public void sendSpeedMessage(int speed) {
        if( speed < 1 ) {
            speed = 10 ;
        }
        this.sendMessage( "speed = " + speed );
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

    public void sendWelcomeBeep( int beepCount ) {
        if( sendingBeep ) {
            return;
        }

        int delay = 500 ;

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            private int count = 0 ;
            private int maxCount = 2*beepCount ;
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

    public String sendSendMeParingCodeMessage() {
        String message = "send me pairing code" ;

        boolean directReply = true;

        String reply = sendMessage( message, directReply );

        String [] replies = reply.split( ":" );

        if( replies == null || replies.length < 1 ) {
            return "" ;
        } else {
            String paringCode = replies[replies.length - 1].trim();
            return paringCode ;
        }
    }

    public String sendMotorStopMessage() {
        String message = "{\"Forward\":\"Up\"}" ;

        return sendMessage( message );
    }

    public String sendBuzzerMessage( boolean on) {
        String message = "{\"BZ\":\"%s\"}";
        message = String.format( message, on ? "on" : "off" );

        return sendMessage( message );
    }

    public String sendParingCompletedMessage( ) {
        String message = "paring completed";

        return sendMessage( message );
    }

    public String sendMessage( String message ) {
        boolean directReply = false ;

        return this.sendMessage( message, directReply );
    }

    public void sendMessageUsingHandler( String message, boolean  directReply ) {
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendMessage( message, directReply );
            }
        });
    }

    public String sendMessage( String message, boolean  directReply ) {
        String reply = sys.sendMessage(message, directReply );

        if( reply == null ) {
            this.whenSysFailed();
        } else {
            this.whenSysSucceeded();
        }

        return reply;
    }

    @Override
    public void whenSysSucceeded() {
        Log.v( tag, "whenSysSucceeded()" );

        if( this.commStatus != null ) {
            this.commStatus.setText("블루투스 통신 성공");
            this.commStatus.setTextColor(greenLight);
        }

        if (this.reconnectButton != null) {
            this.reconnectButton.setEnabled( false );
        }

        if (this.commStatusImage != null) {
            // this.reconnectButton.setVisibility(View.GONE);
            this.commStatusImage.setImageResource(R.drawable.bluetooth_succ_64);
        }
    }

    @Override
    public void whenSysFailed() {
        Log.v(tag, "whenSysFailed() " + this.getClass().getSimpleName() );

        if( null != this.commStatus) {
            String message = "블루투스 통신 실패 : 재접속하여 주세요.";
            if( sys.getBluetoothDevice() == null ) {
                message = "블루투스를 먼저 연결하세요.";
            }

            this.commStatus.setText( message );
            this.commStatus.setTextColor( red );
        }

        if( null != this.reconnectButton ) {
            this.reconnectButton.setEnabled( true );
        }

        if( null != this.commStatusImage ) {
            this.commStatusImage.setImageResource(R.drawable.bluetooth_fail_64);
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
        this.activity.saveProperty(key, value);
    }

    public boolean isBluetoothPaired( String address ) {
        return this.activity.isBluetoothPaired( address );
    }

    public String getBluetoothAddressLastConnected() {
        return this.activity.getBluetoothAddressLastConnected();
    }

    public void saveBluetoothPairedCodeProperty(String address) {
        this.activity.saveBluetoothPairedCodeProperty( address );
    }

    public String getProperty( String key ) {
        return this.activity.getProperty( key );
    }

    public String getProperty( String key, String def ) {
        return this.activity.getProperty( key, def );
    }

    public final ComActivity getComActivity() {
        return (ComActivity) super.getActivity();
    }

    public final Application getApplication() {
        return this.getActivity().getApplication();
    }

    public final Context getContext() {
        Context context = null;

        try {
            context = super.getContext() ;
        } catch ( Exception e ) {
            Log.d( tag, "Cannot get context." ) ;
        }

        return context ;
    }

    public String getBluetoothName( BluetoothDevice device ) {
        String name = "";

        ComActivity activity = this.getComActivity() ;

        if( null != activity ) {
            name = activity.getBluetoothName( device );
        }

        return name ;
    }
}
