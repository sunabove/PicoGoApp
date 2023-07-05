package com.picopy.ui.blockCodingEntry;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.picopy.ComFragment;
import com.picopy.R;
import com.picopy.databinding.FragmentBlockCodingEntryBinding;
import com.picopy.databinding.FragmentBlockCodingScratchBinding;

public class BlockCodingEntryFragment extends ComFragment {

    private FragmentBlockCodingEntryBinding binding;

    private WebView webView ;
    private ProgressBar progressBar ;
    private LinearLayout loadingPanel ;
    private TextView loadingStatus ;
    private Button reloadBtn ;

    public static BlockCodingEntryFragment newInstance() {
        return new BlockCodingEntryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBlockCodingEntryBinding.inflate(inflater, container, false);

        this.webView = binding.webView ;
        this.progressBar = binding.progressBar ;
        this.loadingPanel = binding.loadingPanel ;
        this.loadingStatus = binding.loadingStatus ;
        this.reloadBtn = binding.reloadBtn ;

        this.loadingPanel.setVisibility(View.GONE );

        this.reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadEntry();
            }
        });

        this.initWebView();

        this.loadEntry();

        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.v(tag, "onStart() " + this.getClass().getSimpleName());

    }

    @SuppressLint({ "SetJavaScriptEnabled" })
    private void initWebView() {
        WebView webView = this.webView ;

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setBuiltInZoomControls(true);

        webView.setWebViewClient( new MyWebClient() );
        webView.setWebChromeClient(new WebChromeClient());

        webView.addJavascriptInterface( this, "Android");
    } // -- initWebView

    @JavascriptInterface
    public void javaMehod(String val) {
        Log.v( tag, "javaMehod() " + val);
    }

    @JavascriptInterface
    public void moveToDirection( float fx, float fy, float tx, float ty, float ang_deg ) {
        String msg = "moveToDirection( fx = %f, fy = %f, tx = %f, ty = %f, ang_deg = %f )" ;
        msg = String.format( msg, fx, fy, tx, ty, ang_deg );

        this.sendMessage( msg );

        Log.v( tag, msg );
    }

    public void loadEntry() {
        WebView webView = this.webView ;

        String url = "file:///android_asset/entry_html/index.html" ;

        this.webView.loadUrl( url );
    } // -- loadEntry

    class MyWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            loadingStatus.setText( "엔트리를 로딩중입니다." );
            progressBar.setVisibility(View.VISIBLE);
            loadingPanel.setVisibility( View.VISIBLE );
        }

        @Override
        public void onPageFinished(WebView view, String url ) {
            super.onPageFinished(view, url );

            loadingStatus.setText( "엔트리 로딩이 완료되었습니다." );
            progressBar.setVisibility(View.GONE);

            postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadingPanel.setVisibility( View.GONE );
                }
            }, 1000 );
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);

            return true;
        }
    } // MyWebClient classs

}