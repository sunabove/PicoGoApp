package com.picopy.ui.avoidance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.picopy.ComFragment;
import com.picopy.databinding.FragmentObstacleAvoidanceBinding;

public class ObstacleAvoidanceFragment extends ComFragment {

    private FragmentObstacleAvoidanceBinding binding;

    private Switch startStopBtn ;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ObstacleAvoidanceViewModel obstacleAvoidanceViewModel = new ViewModelProvider(this).get(ObstacleAvoidanceViewModel.class);

        binding = FragmentObstacleAvoidanceBinding.inflate(inflater, container, false);

        this.startStopBtn = binding.startStopBtn ;

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
    public void onPause() {
        super.onPause();

        this.sendStopMessage();
    }

    public void whenStartStopBtnClicked( boolean checked) {
        if( checked ) {
            this.sendStartObstacleAvoidanceMessage();
        } else {
            this.sendStopMessage();
        }
    }

}