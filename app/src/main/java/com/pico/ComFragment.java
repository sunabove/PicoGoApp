package com.pico;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class ComFragment extends Fragment implements ComInterface, SysListener {

    protected final Sys sys = Sys.getSys();

    private ImageView commStatusImage;
    private Button reconnectButton;
    private EditText commStatus;

    protected int startCount = 0 ;

    public ComFragment() {

    }

    public void initFragment() {

        if( null == this.commStatus) {
            this.commStatus = this.findViewById(R.id.commStatus);
        }

        if( null == this.commStatusImage) {
            this.commStatusImage = this.findViewById(R.id.commStatusImage);
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
        this.moveToFragment( 0 );
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.v( tag, "onStart() startCount = " + this.startCount );

        if( this.startCount < 1 ) {
            this.initFragment();
        }

        this.startCount += 1;

        this.sendHelloMessage( );
    }

    public void sendHelloMessage() {
        this.sendMessage( "hello" );
    }

    public void sendWelcomeBeep( ) {
        int delay = 500 ;

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            private int count = 0 ;
            private int maxCount = 8;
            @Override
            public void run() {
                if( count < maxCount ) {
                    if (count % 2 == 0) {
                        sendMessage("{\"BZ\":\"on\"}");
                    } else {
                        sendMessage("{\"BZ\":\"off\"}");
                    }

                    handler.postDelayed(this, delay);

                    count += 1 ;
                }
            }
        };

        handler.postDelayed( runnable, delay);
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
        BottomNavigationView navView = activity.findViewById(R.id.nav_view);

        navView.setSelectedItemId( activity.navigationIds[ navIdx ] );
    }

}
