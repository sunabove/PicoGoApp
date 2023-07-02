package com.picopy.ui.blockCoding;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.picopy.ComFragment;
import com.picopy.R;
import com.picopy.databinding.FragmentBlockCodingBinding;
import com.picopy.databinding.FragmentObstacleAvoidanceBinding;

public class BlockCodingFragment extends ComFragment {

    private ImageButton scratchStartBtn ;

    private FragmentBlockCodingBinding binding;

    public static BlockCodingFragment newInstance() {
        return new BlockCodingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentBlockCodingBinding.inflate(inflater, container, false);

        this.scratchStartBtn = binding.scratchStartBtn ;

        this.scratchStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whenScratchStartBtnClicked();
            }
        });

        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.v(tag, "onStart() " + this.getClass().getSimpleName());

        if( this.startCount <= 1 ) {
            this.whenScratchStartBtnClicked();
        }
    }

    public void whenScratchStartBtnClicked() {

        FragmentActivity activity = this.getActivity() ;

        String appName = "Scratch";
        String packageName = "org.scratch" ;

        this.openApp( appName, packageName );
    }

    public void openApp(String appName, String packageName) {
        Context context = this.getContext() ;
        if (isAppInstalled(context, packageName)) {
            if (isAppEnabled(context, packageName)) {
                context.startActivity(context.getPackageManager().getLaunchIntentForPackage(packageName));
            } else {
                Log.v( tag, appName + " app is not enabled." ) ;
            }
        } else {
            Log.v( tag, appName + " app is not installed." ) ;
        }
    }

    private boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException ignored) {
            ignored.printStackTrace();
        }

        return false;
    }

    private boolean isAppEnabled(Context context, String packageName) {
        boolean appStatus = false;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(packageName, 0);
            if (ai != null) {
                appStatus = ai.enabled;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appStatus;
    }

}