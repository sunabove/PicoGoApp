package com.picopy.ui.laneFollow;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.picopy.ComFragment;
import com.picopy.databinding.FragmentLaneFollowBinding;

public class LaneFollowFragment extends ComFragment {

    private FragmentLaneFollowBinding binding;
    private Switch startStopBtn ;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LaneFollowViewModel laneFollowViewModel = new ViewModelProvider(this).get(LaneFollowViewModel.class);

        binding = FragmentLaneFollowBinding.inflate(inflater, container, false);

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
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();

        this.sendStopMessage();
    }

    public void whenStartStopBtnClicked( boolean checked) {
        if( checked ) {
            this.sendStartLaneFollowingMessage();
        } else {
            this.sendStopMessage();
        }
    }

}