package com.pico;

import androidx.fragment.app.Fragment;

public abstract class ComFragment extends Fragment implements ComInterface {

    protected final Sys sys = Sys.getSys();

    public ComFragment() {

    }
}
