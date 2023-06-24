package com.picopy.ui.lineFollow;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.picopy.ComFragment;
import com.picopy.R;
import com.picopy.databinding.FragmentLaneFollowBinding;
import com.picopy.databinding.FragmentManualDriveBinding;

public class LineFollowFragment extends ComFragment {

    private FragmentLaneFollowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LineFollowViewModel lineFollowViewModel = new ViewModelProvider(this).get(LineFollowViewModel.class);

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