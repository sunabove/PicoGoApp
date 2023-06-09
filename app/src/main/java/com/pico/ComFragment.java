package com.pico;

import android.util.Log;

import androidx.fragment.app.Fragment;

public abstract class ComFragment extends Fragment implements ComInterface, SysListener {

    protected final Sys sys = Sys.getSys();

    public ComFragment() {

    }

    public boolean sendMessage( String message ) {
        boolean result = sys.sendMessage(message);

        if( result ) {
            this.whenSysSucceeded();
        } else {
            this.whenSysFailed();
        }

        return result;
    }

    @Override
    public void whenSysSucceeded() {
        Log.v( tag, "whenSysSucceeded()" );
    }

    @Override
    public void whenSysFailed() {
        Log.v( tag, "whenSysFailed()" );
    }
}
