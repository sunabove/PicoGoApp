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

        speedLow.setSelected( true );

        speedLow.setOnClickListener( speedListener );
        speedMedium.setOnClickListener( speedListener );
        speedHigh.setOnClickListener( speedListener );

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

        String dir ="Forward" ;
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
            dir = "Forward" ;

            imageId = action == 0 ? R.drawable.dir_forward_pressed : action == 1 ? R.drawable.dir_forward : -1 ;
        } else if( view == binding.backward ) {
            dir = "Backward" ;
            imageId = action == 0 ? R.drawable.dir_backward_pressed : action == 1 ? R.drawable.dir_backward : -1 ;
        } else if( view == binding.left ) {
            dir = "Left" ;
            imageId = action == 0 ? R.drawable.dir_left_pressed : action == 1 ? R.drawable.dir_left : -1 ;
        } else if( view == binding.right ) {
            dir = "Right" ;
            imageId = action == 0 ? R.drawable.dir_right_pressed : action == 1 ? R.drawable.dir_right : -1 ;
        }

        if( imageId != -1 ) {
            imageButton.setImageResource(imageId);
        }

        if( dir.length() > 0 && upDown.length() > 0 ) {
            message = String.format(message, dir, upDown);

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