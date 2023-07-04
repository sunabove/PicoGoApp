package com.picopy.ui.blockCodingEntry;

import androidx.lifecycle.ViewModelProvider;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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

        this.loadingPanel.setVisibility(View.GONE );

        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.v(tag, "onStart() " + this.getClass().getSimpleName());

        WebView webView = this.webView ;

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setBuiltInZoomControls(true);

        class MyWebClient extends WebViewClient {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                loadingStatus.setText( "엔트리를 로딩중입니다." );
                loadingPanel.setVisibility( View.VISIBLE );
            }

            @Override
            public void onPageFinished(WebView view, String url ) {
                super.onPageFinished(view, url );

                loadingStatus.setText( "엔트리 로딩이 완료되었습니다." );
                loadingPanel.setVisibility( View.GONE );
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                return true;
            }
        }

        webView.setWebViewClient( new MyWebClient() );

        String url = "file:///android_asset/entry_html/index.html" ;

        this.webView.loadUrl( url );
    }

}