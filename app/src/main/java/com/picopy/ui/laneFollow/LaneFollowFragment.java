package com.picopy.ui.laneFollow;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.picopy.ComFragment;
import com.picopy.databinding.FragmentLaneFollowBinding;

public class LaneFollowFragment extends ComFragment {

    private FragmentLaneFollowBinding binding;

    private Switch startStopBtn ;

    private SeekBar speedSeekBar ;
    private TextView speedTv ;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LaneFollowViewModel laneFollowViewModel = new ViewModelProvider(this).get(LaneFollowViewModel.class);

        binding = FragmentLaneFollowBinding.inflate(inflater, container, false);

        this.startStopBtn = binding.startStopBtn ;

        this.speedSeekBar = binding.speedSeekBar ;
        this.speedTv = binding.speedTv ;

        this.speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                whenSpeedSeekBarChanged( seekBar, i );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        this.startStopBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                whenStartStopBtnClicked( checked );
            }
        });

        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        this.speedTv.setText( "" + this.speedSeekBar.getProgress() );

        this.sendSpeedMessage( this.speedSeekBar.getProgress() );
    }

    @Override
    public void onPause() {
        super.onPause();

        this.sendStopMessage();
    }

    public void whenSpeedSeekBarChanged(SeekBar seekBar, int i) {
        Log.d( tag, "whenSpeedSeekBarChanged() i = " + i );

        int speed = i ;

        this.speedTv.setText( "" + speed );

        this.sendSpeedMessage( speed );
    }

    public void whenStartStopBtnClicked( boolean checked) {
        if( checked ) {
            this.sendStartLaneFollowingMessage();
        } else {
            this.sendStopMessage();
        }
    }

}