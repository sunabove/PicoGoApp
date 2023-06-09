package com.pico.ui.autoDrive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.pico.ComFragment;
import com.pico.databinding.FragmentAutoDriveBinding;

public class AutoDriveFragment extends ComFragment {

    private FragmentAutoDriveBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AutoDriveViewModel autoDriveViewModel = new ViewModelProvider(this).get(AutoDriveViewModel.class);

        binding = FragmentAutoDriveBinding.inflate(inflater, container, false);
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