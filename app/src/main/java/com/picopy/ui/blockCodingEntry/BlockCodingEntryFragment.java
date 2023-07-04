package com.picopy.ui.blockCodingEntry;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.picopy.ComFragment;
import com.picopy.R;
import com.picopy.databinding.FragmentBlockCodingEntryBinding;
import com.picopy.databinding.FragmentBlockCodingScratchBinding;

public class BlockCodingEntryFragment extends ComFragment {

    private FragmentBlockCodingEntryBinding binding;

    private WebView webView ;

    public static BlockCodingEntryFragment newInstance() {
        return new BlockCodingEntryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBlockCodingEntryBinding.inflate(inflater, container, false);

        this.webView = binding.webView ;

        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.v(tag, "onStart() " + this.getClass().getSimpleName());

        WebView webView = this.webView ;

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);

        String url = "" ;
        url = "https://playentry.org/ws/new" ;
        url = "https://www.google.com/";
        url = "https://scratch.mit.edu/projects/editor/" ;
        url = "file:///android_asset/entry_html/index.html" ;

        this.webView.loadUrl( url );
    }

}