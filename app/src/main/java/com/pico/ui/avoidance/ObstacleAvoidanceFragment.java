package com.pico.ui.avoidance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.pico.ComFragment;
import com.pico.databinding.FragmentObstacleAvoidanceBinding;

public class ObstacleAvoidanceFragment extends ComFragment {

    private FragmentObstacleAvoidanceBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ObstacleAvoidanceViewModel obstacleAvoidanceViewModel = new ViewModelProvider(this).get(ObstacleAvoidanceViewModel.class);

        binding = FragmentObstacleAvoidanceBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.textNotifications;
        //autoDriveViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}