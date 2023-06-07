package com.pico.ui.lineFollow;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pico.R;
import com.pico.databinding.FragmentLineFollowBinding;
import com.pico.databinding.FragmentManualDriveBinding;
import com.pico.ui.manualDrive.ManualDriveViewModel;

public class LineFollowFragment extends Fragment {

    private FragmentLineFollowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LineFollowViewModel lineFollowViewModel = new ViewModelProvider(this).get(LineFollowViewModel.class);

        binding = FragmentLineFollowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotifications;

        lineFollowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}