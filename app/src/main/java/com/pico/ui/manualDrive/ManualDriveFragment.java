package com.pico.ui.manualDrive;

import android.graphics.Color;
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

        binding = FragmentManualDriveBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if( true ) {
            ImageButton forward = binding.forward;
            ImageButton backward = binding.backward;
            ImageButton left = binding.left;
            ImageButton right = binding.right;

            forward.setOnTouchListener(dirListener);
            backward.setOnTouchListener(dirListener);
            left.setOnTouchListener(dirListener);
            right.setOnTouchListener(dirListener);
        }

        if( true ) {

            RadioButton speedLow = binding.speedLow;
            RadioButton speedMedium = binding.speedMedium;
            RadioButton speedHigh = binding.speedHigh;

            speedMedium.setChecked(true);
            this.whenSpeedRadioButtonClicked(speedMedium);

            speedLow.setOnClickListener(speedListener);
            speedMedium.setOnClickListener(speedListener);
            speedHigh.setOnClickListener(speedListener);
        }

        if( true ) {
            CheckBox buzzerToggleButton = binding.buzzerToggleButton;
            CheckBox ledToggleButton = binding.ledToggleButton;

            buzzerToggleButton.setOnCheckedChangeListener(toggleButtonListener);
            ledToggleButton.setOnCheckedChangeListener(toggleButtonListener);
        }

        if( true ) {
            SeekBar colorSeekBar = binding.colorSeekBar;
            colorSeekBar.setOnSeekBarChangeListener( colorSeekBarListner );
        }

        return root;
    }

    private SeekBar.OnSeekBarChangeListener colorSeekBarListner = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            onColorSeekBarProgressChanged(seekBar, i, b);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            onColorSeekBarStopTrackingTouch( seekBar );
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // do nothing!
        }

    };

    private void onColorSeekBarProgressChanged(SeekBar seekBar, int i, boolean b) {
    }

    public void onColorSeekBarStopTrackingTouch(SeekBar seekBar) {
        Log.i(tag, "onColorSeekBarStopTrackingTouch()");

        String message = "{\"RGB\": \"%d,%d,%d\"}";

        Color color = Color.valueOf( getResources().getColor( R.color.red, getActivity().getTheme() ) );
        int c = color.toArgb();

        int red = Color.red( c );
        int blue = Color.blue( c );
        int green = Color.green( c );

        message = String.format( message, red, blue, green );

        sys.sendMessage(message);

    }

    private CompoundButton.OnCheckedChangeListener toggleButtonListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
            whenToggleButtonClicked( toggleButton, isChecked );
        }
    };

    private void whenToggleButtonClicked(CompoundButton toggleButton, boolean isChecked) {
        Log.i(tag, "whenToggleButtonClicked()");

        FragmentManualDriveBinding binding = this.binding;

        String message = "{\"%s\":\"%s\"}" ;

        String command = "" ;
        String onOff = "" ;

        if( isChecked ) {
            onOff = "on";

            toggleButton.setText( " 켜 짐 " );
            toggleButton.setTextColor( red );
        } else {
            onOff = "off";

            toggleButton.setText( " 꺼 짐 " );
            toggleButton.setTextColor( black );
        }

        if( toggleButton == binding.buzzerToggleButton ) {
            command = "BZ";
        } else if( toggleButton == binding.ledToggleButton ) {
            command = "LED";
        }

        if( command.length() > 0 && onOff.length() > 0 ) {
            message = String.format(message, command, onOff );

            sys.sendMessage(message);
        }
    }

    View.OnClickListener speedListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            whenSpeedRadioButtonClicked( view );
        }
    };

    private void whenSpeedRadioButtonClicked(View view) {
        Log.i( tag, "whenSpeedRadioButtonClicked" );

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

            sys.sendMessage(message);
        }

    }

    private View.OnTouchListener dirListener = new View.OnTouchListener() {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            whenDirButtonTouched(view, motionEvent);
            return false;
        }
    };

    private void whenDirButtonTouched(View view, MotionEvent motionEvent ) {
        Log.i( tag, "whenDirButtonTouched()" );

        FragmentManualDriveBinding binding = this.binding;
        ImageButton imageButton = (ImageButton) view ;
        int action = motionEvent.getAction();

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
            imageId = action == MotionEvent.ACTION_DOWN ? R.drawable.dir_forward_pressed : action == MotionEvent.ACTION_UP ? R.drawable.dir_forward : -1 ;
        } else if( view == binding.backward ) {
            command = "Backward" ;
            imageId = action == MotionEvent.ACTION_DOWN ? R.drawable.dir_backward_pressed : action == MotionEvent.ACTION_UP ? R.drawable.dir_backward : -1 ;
        } else if( view == binding.left ) {
            command = "Left" ;
            imageId = action == MotionEvent.ACTION_DOWN ? R.drawable.dir_left_pressed : action == MotionEvent.ACTION_UP ? R.drawable.dir_left : -1 ;
        } else if( view == binding.right ) {
            command = "Right" ;
            imageId = action == MotionEvent.ACTION_DOWN ? R.drawable.dir_right_pressed : action == MotionEvent.ACTION_UP ? R.drawable.dir_right : -1 ;
        }

        if( imageId != -1 ) {
            imageButton.setImageResource(imageId);
        }

        if( command.length() > 0 && upDown.length() > 0 ) {
            message = String.format(message, command, upDown);

            sys.sendMessage(message);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.v( tag, "onResume()" );

        if( false ) {
            String message = "{\"Forward\":\"Down\"}";
            sys.sendMessage(message);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        sys.sendMessage( "{\"Forward\":\"Up\"}" );

        binding = null;
    }
}