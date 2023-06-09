package com.pico.ui.manualDrive;

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

        ImageButton forward = binding.forward;
        ImageButton backward = binding.backward;
        ImageButton left = binding.left;
        ImageButton right = binding.right;

        RadioButton speedLow = binding.speedLow;
        RadioButton speedMedium = binding.speedMedium;
        RadioButton speedHigh = binding.speedHigh;

        speedLow.setOnClickListener( speedListener );
        speedMedium.setOnClickListener( speedListener );
        speedHigh.setOnClickListener( speedListener );

        speedMedium.setChecked( true );

        forward.setOnTouchListener( dirListener );
        backward.setOnTouchListener( dirListener );
        left.setOnTouchListener( dirListener );
        right.setOnTouchListener( dirListener );

        return root;
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

            imageId = action == 0 ? R.drawable.dir_forward_pressed : action == 1 ? R.drawable.dir_forward : -1 ;
        } else if( view == binding.backward ) {
            command = "Backward" ;
            imageId = action == 0 ? R.drawable.dir_backward_pressed : action == 1 ? R.drawable.dir_backward : -1 ;
        } else if( view == binding.left ) {
            command = "Left" ;
            imageId = action == 0 ? R.drawable.dir_left_pressed : action == 1 ? R.drawable.dir_left : -1 ;
        } else if( view == binding.right ) {
            command = "Right" ;
            imageId = action == 0 ? R.drawable.dir_right_pressed : action == 1 ? R.drawable.dir_right : -1 ;
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