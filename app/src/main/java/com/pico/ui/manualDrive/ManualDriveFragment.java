package com.pico.ui.manualDrive;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.pico.ComFragment;
import com.pico.R;
import com.pico.databinding.FragmentManualDriveBinding;

public class ManualDriveFragment extends ComFragment {

    private FragmentManualDriveBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ManualDriveViewModel manualDriveViewModel = new ViewModelProvider(this).get(ManualDriveViewModel.class);

        this.binding = FragmentManualDriveBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if( true ) {
            ImageButton forward = binding.forward;
            ImageButton backward = binding.backward;
            ImageButton left = binding.left;
            ImageButton right = binding.right;
            ImageButton stop = binding.stop;

            forward.setOnTouchListener(dirListener);
            backward.setOnTouchListener(dirListener);
            left.setOnTouchListener(dirListener);
            right.setOnTouchListener(dirListener);
            stop.setOnTouchListener(dirListener);
        }

        if( true ) {

            RadioButton speedLow = binding.speedLow;
            RadioButton speedMedium = binding.speedMedium;
            RadioButton speedHigh = binding.speedHigh;

            speedLow.setOnClickListener(speedListener);
            speedMedium.setOnClickListener(speedListener);
            speedHigh.setOnClickListener(speedListener);
        }

        if( true ) {
            Switch buzzerToggleButton = binding.buzzerToggleButton;
            Switch ledToggleButton = binding.ledToggleButton;

            buzzerToggleButton.setOnCheckedChangeListener(toggleButtonListener);
            ledToggleButton.setOnCheckedChangeListener(toggleButtonListener);
        }

        if( true ) {
            SeekBar colorSeekBar = binding.colorSeekBar;
            colorSeekBar.setOnSeekBarChangeListener( colorSeekBarListner );
        }

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        this.sendWelcomeBeep();

        this.initRobot();
    }

    private void initRobot() {
        Log.v( tag, "initRobot()" );

        FragmentManualDriveBinding binding = this.binding;

        binding.speedLow.setChecked( true );
        this.whenSpeedRadioButtonClicked(binding.speedLow);
        this.whenDirButtonTouched( binding.forward, MotionEvent.ACTION_UP );
        this.whenColorSeekBarStopTrackingTouch( binding.colorSeekBar );

        this.whenToggleButtonClicked(binding.buzzerToggleButton, false);
        this.whenToggleButtonClicked(binding.ledToggleButton, false);
    }

    private SeekBar.OnSeekBarChangeListener colorSeekBarListner = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            whenColorSeekBarProgressChanged(seekBar, i, b);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            whenColorSeekBarStopTrackingTouch( seekBar );
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // do nothing!
        }

    };

    private void whenColorSeekBarProgressChanged(SeekBar seekBar, int i, boolean b) {
        // Do nothing!
    }

    public void whenColorSeekBarStopTrackingTouch(SeekBar seekBar) {
        Log.v(tag, "onColorSeekBarStopTrackingTouch()");

        int i = seekBar.getProgress();

        Log.v( tag, "colorSeekBar progress = " + i );

        BitmapDrawable bitmapDrawable = (BitmapDrawable) seekBar.getBackground();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        final int w = bitmap.getWidth();
        final int h = bitmap.getHeight();

        int x = w*i/100;
        int y = h/5;

        if( x >= w ) {
            x = w - 1;
        } else if( x < 0 ) {
            x = 0 ;
        }

        int color = bitmap.getPixel( x, y );

        this.sendRgbLedMessage( color );
    }

    private CompoundButton.OnCheckedChangeListener toggleButtonListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
            whenToggleButtonClicked( toggleButton, isChecked );
        }
    };

    private void whenToggleButtonClicked(CompoundButton toggleButton, boolean isChecked) {
        Log.v(tag, "whenToggleButtonClicked()");

        FragmentManualDriveBinding binding = this.binding;

        String message = "{\"%s\":\"%s\"}" ;

        String command = "" ;
        String onOff = "" ;

        if( isChecked ) {
            onOff = "on";

            if( toggleButton instanceof ToggleButton ) {
                toggleButton.setText(" 켜 짐 ");
                toggleButton.setTextColor(red);
            }
        } else {
            onOff = "off";

            if( toggleButton instanceof ToggleButton ) {
                toggleButton.setText(" 꺼 짐 ");
                toggleButton.setTextColor(black);
            }
        }

        if( toggleButton == binding.buzzerToggleButton ) {
            command = "BZ";
        } else if( toggleButton == binding.ledToggleButton ) {
            command = "LED";
        }

        if( command.length() > 0 && onOff.length() > 0 ) {
            message = String.format(message, command, onOff );

            this.sendMessage(message );
        }
    }

    View.OnClickListener speedListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            whenSpeedRadioButtonClicked( view );
        }
    };

    private void whenSpeedRadioButtonClicked(View view) {
        Log.v( tag, "whenSpeedRadioButtonClicked" );

        FragmentManualDriveBinding binding = this.binding;

        binding.speedHigh.setTextColor( black );
        binding.speedMedium.setTextColor( black );
        binding.speedLow.setTextColor( black );

        String message = "{\"%s\":\"%s\"}" ;

        String command = "" ;
        String upDown = "Down" ;

        if( view == binding.speedHigh ) {
            command = "High";
        } else if( view == binding.speedMedium ) {
            command = "Medium";
        } if( view == binding.speedLow ) {
            command = "Low";
        }

        RadioButton button = (RadioButton) view ;
        button.setTextColor( red );

        if( command.length() > 0 && upDown.length() > 0 ) {
            message = String.format(message, command, upDown);

            this.sendMessage( message );
        }

    }

    private View.OnTouchListener dirListener = new View.OnTouchListener() {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            whenDirButtonTouched(view, motionEvent.getAction());
            return false;
        }
    };

    private void whenDirButtonTouched(View view, int action ) {
        Log.v( tag, "whenDirButtonTouched()" );

        FragmentManualDriveBinding binding = this.binding;
        ImageButton currDirButton = (ImageButton) view ;

        boolean disableButtons = false ;

        if( disableButtons ) {
            if (action == MotionEvent.ACTION_DOWN) {
                binding.forward.setEnabled(false);
                binding.backward.setEnabled(false);
                binding.left.setEnabled(false);
                binding.right.setEnabled(false);

                binding.stop.setEnabled(true);

                currDirButton.setEnabled(true);
            } else if (action == MotionEvent.ACTION_UP) {
                binding.forward.setEnabled(true);
                binding.backward.setEnabled(true);
                binding.left.setEnabled(true);
                binding.right.setEnabled(true);
                binding.stop.setEnabled(true);
            }
        }

        //String message = "{\"Forward\":\"Down\"}" ;
        String message = "{\"%s\":\"%s\"}" ;

        String command ="Forward" ;
        String upDown = "Up" ;

        if( action == 0 ) {
            upDown = "Down";
        } else if( action == 1 ) {
            upDown = "Up";
        } else {
            upDown = "";
        }

        int imageId = -1 ;

        if( view == binding.forward ) {
            command = "Forward" ;
            imageId = action == MotionEvent.ACTION_DOWN ? R.drawable.dir_forward_pressed_128 : action == MotionEvent.ACTION_UP ? R.drawable.dir_forward_128 : -1 ;
        } else if( view == binding.backward ) {
            command = "Backward" ;
            imageId = action == MotionEvent.ACTION_DOWN ? R.drawable.dir_backward_pressed_128 : action == MotionEvent.ACTION_UP ? R.drawable.dir_backward_128 : -1 ;
        } else if( view == binding.left ) {
            command = "Left" ;
            imageId = action == MotionEvent.ACTION_DOWN ? R.drawable.dir_left_pressed_128 : action == MotionEvent.ACTION_UP ? R.drawable.dir_left_128 : -1 ;
        } else if( view == binding.right ) {
            command = "Right" ;
            imageId = action == MotionEvent.ACTION_DOWN ? R.drawable.dir_right_pressed_128 : action == MotionEvent.ACTION_UP ? R.drawable.dir_right_128 : -1 ;
        } else if( view == binding.stop ) {
            command = "Forward" ;
            upDown = "Up";
            imageId = action == MotionEvent.ACTION_DOWN ? R.drawable.dir_stop_pressed_128 : action == MotionEvent.ACTION_UP ? R.drawable.dir_stop_128 : -1 ;
        }

        if( imageId != -1 ) {
            binding.forward.setImageResource( R.drawable.dir_forward_128);
            binding.backward.setImageResource( R.drawable.dir_backward_128);
            binding.left.setImageResource( R.drawable.dir_left_128);
            binding.right.setImageResource( R.drawable.dir_right_128);
            binding.stop.setImageResource( R.drawable.dir_stop_128);
            
            currDirButton.setImageResource(imageId);
        }

        if( command.length() > 0 && upDown.length() > 0 ) {
            message = String.format(message, command, upDown);

            this.sendMessage(message);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.v( tag, "onResume()" );
    }

    @Override
    public void onPause() {
        super.onPause();

        this.sendMotorStopMessage( );
        this.sendBuzzerMessage( false );

        int color = 0;
        this.sendRgbLedMessage( color );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }
}