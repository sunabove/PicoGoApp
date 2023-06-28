package com.picopy.ui.blockCoding;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.picopy.R;
import com.picopy.databinding.FragmentBlockCodingBinding;
import com.picopy.databinding.FragmentObstacleAvoidanceBinding;

public class BlockCodingFragment extends Fragment {

    private FragmentBlockCodingBinding binding;

    public static BlockCodingFragment newInstance() {
        return new BlockCodingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentBlockCodingBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        return root;
    }

}