package com.picopy;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.picopy.databinding.ActivityTabBinding;

public class TabActivity extends ComActivity {

    public static final int [] navigationIds = { R.id.navigation_bluetooth, R.id.navigation_manual_drive, R.id.navigation_obstacle_avoidance, R.id.navigation_line_follow, R.id.navigation_block_coding };

    private ActivityTabBinding binding;

    private int lastTabIndex = -1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTabBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder( navigationIds ).build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_tab);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean useOptionMenu = false;

        if( useOptionMenu ) {
            getMenuInflater().inflate(R.menu.option_menu, menu);
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();;

        Log.i( tag, "TabActivity onBackPressed()" );

        sys.disconnectBluetoothDevice();
    }

    public int getLastTabIndex() {
        return lastTabIndex;
    }

    public void setLastTabIndex(int lastTabIndex) {
        this.lastTabIndex = lastTabIndex;
    }
}