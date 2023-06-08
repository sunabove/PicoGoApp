package com.pico.ui.manualDrive;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.pico.ComFragment;
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

        View.OnTouchListener listener = new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                whenDirButtonTouched(view, motionEvent);
                return false;
            }
        };

        forward.setOnTouchListener( listener );
        backward.setOnTouchListener( listener );
        left.setOnTouchListener( listener );
        right.setOnTouchListener( listener );

        return root;
    }

    private void whenDirButtonTouched(View view, MotionEvent motionEvent ) {
        FragmentManualDriveBinding binding = this.binding;

        int action = motionEvent.getAction();

        //String message = "{\"Forward\":\"Down\"}" ;
        String message = "{\"%s\":\"%s\"}" ;

        String dir ="Forward" ;
        String upDown = "Up" ;

        if( action == 0 ) {
            upDown = "Down";

            view.setBackgroundColor( greenLight );
        } else if( action == 1 ) {
            upDown = "Up";
        } else {
            upDown = "";
        }

        if( view == binding.forward ) {
            dir = "Forward" ;
        } else if( view == binding.backward ) {
            dir = "Backward" ;
        } else if( view == binding.left ) {
            dir = "Left" ;
        } else if( view == binding.right ) {
            dir = "Right" ;
        }

        if( dir.length() > 0 && upDown.length() > 0 ) {
            message = String.format(message, dir, upDown);

            sys.sendMessage(message);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.v( "sunabove", "onResume()" );

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