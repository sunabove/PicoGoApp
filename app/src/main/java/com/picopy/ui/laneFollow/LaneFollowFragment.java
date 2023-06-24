package com.picopy.ui.laneFollow;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.picopy.ComFragment;
import com.picopy.databinding.FragmentLaneFollowBinding;

public class LaneFollowFragment extends ComFragment {

    private FragmentLaneFollowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LaneFollowViewModel laneFollowViewModel = new ViewModelProvider(this).get(LaneFollowViewModel.class);

        binding = FragmentLaneFollowBinding.inflate(inflater, container, false);
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

}