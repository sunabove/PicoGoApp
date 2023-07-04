package com.picopy.ui.avoidance;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.picopy.ComFragment;
import com.picopy.databinding.FragmentObstacleAvoidanceBinding;

public class ObstacleAvoidanceFragment extends ComFragment {

    private FragmentObstacleAvoidanceBinding binding;

    private Switch startStopBtn ;
    private SeekBar speedSeekBar ;
    private TextView speedTv ;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ObstacleAvoidanceViewModel obstacleAvoidanceViewModel = new ViewModelProvider(this).get(ObstacleAvoidanceViewModel.class);

        binding = FragmentObstacleAvoidanceBinding.inflate(inflater, container, false);

        this.startStopBtn = binding.startStopBtn ;
        this.speedSeekBar = binding.speedSeekBar ;
        this.speedTv = binding.speedTv ;

        this.startStopBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                whenStartStopBtnClicked( checked );
            }
        });

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
            this.sendStartObstacleAvoidanceMessage();
        } else {
            this.sendStopMessage();
        }
    }

}