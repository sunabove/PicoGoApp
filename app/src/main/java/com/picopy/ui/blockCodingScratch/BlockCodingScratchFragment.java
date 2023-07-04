package com.picopy.ui.blockCodingScratch;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.picopy.ComActivity;
import com.picopy.ComFragment;
import com.picopy.databinding.FragmentBlockCodingScratchBinding;

public class BlockCodingScratchFragment extends ComFragment {

    private ImageButton scratchStartBtn ;
    private EditText status;

    private FragmentBlockCodingScratchBinding binding;

    public static BlockCodingScratchFragment newInstance() {
        return new BlockCodingScratchFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentBlockCodingScratchBinding.inflate(inflater, container, false);

        this.scratchStartBtn = binding.scratchStartBtn ;

        this.scratchStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whenScratchStartBtnClicked( 1000 );
            }
        });

        this.status = binding.status ;

        this.status.setText( "" );

        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.v(tag, "onStart() " + this.getClass().getSimpleName());

        if( this.startCount <= 1 ) {
            this.whenScratchStartBtnClicked( 2000 );
        }
    }

    public void whenScratchStartBtnClicked(int delayMilis) {
        String packageName = "org.scratch" ;
        
        if( ! isAppInstalled( packageName ) ) {
            this.status.setText("스크래치 앱을 설치합니다.");
        } else {
            this.status.setText("스크래치 앱을 실행합니다.");
        }

        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                whenScratchStartBtnClickedImpl();
            }
        }, delayMilis );
    }

    public void whenScratchStartBtnClickedImpl() {
        ComActivity activity = this.getComActivity() ;

        String appName = "Scratch";
        String packageName = "org.scratch" ;

        if( ! isAppInstalled( packageName ) ) {

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    installApp( appName, packageName );
                }
            };

            String title = "스크래치 앱 설치" ;
            String message = "스크래치 앱을 설치합니다." ;

            this.status.setText( message );

            activity.showMessageDialog( title, message, runnable );
        } else {
            this.openApp(appName, packageName);
        }

    }

    public void installApp( String appName, String packageName ) {
        Intent goToMarket = new Intent(Intent.ACTION_VIEW);
        goToMarket.setData(Uri.parse("market://details?id=" + packageName ));

        startActivity(goToMarket);
    }

    public void openApp(String appName, String packageName) {
        Context context = this.getContext() ;
        if ( isAppInstalled( packageName) ) {
            if (isAppEnabled( packageName)) {
                this.status.setText( appName + "앱을 실행합니다."  );

                context.startActivity(context.getPackageManager().getLaunchIntentForPackage(packageName));

                this.status.setText( appName + "앱이 실행되었습니다."  );
            } else {
                Log.v( tag, appName + " app is not enabled." ) ;

                this.status.setText( appName + " 앱이 비활성화되어 있습니다."  );
            }
        } else {
            Log.v( tag, appName + " app is not installed." ) ;

            this.status.setText( appName + " 앱이 설치되어 있지 않습니다."  );
        }
    }

    private boolean isAppInstalled( String packageName) {
        Context context = this.getContext();

        PackageManager pm = context.getPackageManager();

        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);

            return true;
        } catch (PackageManager.NameNotFoundException ignored) {
            ignored.printStackTrace();
        }

        return false;
    }

    private boolean isAppEnabled( String packageName) {
        boolean appStatus = false;
        try {
            Context context = this.getContext();
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