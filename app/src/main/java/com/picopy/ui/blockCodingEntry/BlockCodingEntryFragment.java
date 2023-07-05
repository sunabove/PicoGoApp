package com.picopy.ui.blockCodingEntry;

import androidx.activity.OnBackPressedCallback;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Context;
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

    private final boolean readDirectly = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.binding = FragmentBlockCodingEntryBinding.inflate(inflater, container, false);

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

    private OnBackPressedCallback backPressedCallback = new OnBackPressedCallback( true ) {
        @Override
        public void handleOnBackPressed() {
            whenBackPressed();
        }
    } ;

    @Override
    public void onAttach(Context context) {
        super.onAttach( context );

        this.getActivity().getOnBackPressedDispatcher().addCallback( backPressedCallback );
    }

    @Override
    public void onDetach() {
        super.onDetach();

        backPressedCallback.remove();
    }

    public void whenBackPressed() {
        Log.d( tag, "whenBackPressed() " + this.getClass().getSimpleName() );

        Runnable okRunnable = new Runnable() {
            @Override
            public void run() {
                backPressedCallback.remove();

                getActivity().onBackPressed();
            }
        } ;

        Runnable cancelRunnable = new Runnable() {
            @Override
            public void run() {
                // do nothing.
            }
        };

        String title = "엔트리 종료" ;
        String message = "엔트리를 종료하시겠습니까?" ;

        this.showMessageDialog( title, message, okRunnable, cancelRunnable );
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.v(tag, "onStart() " + this.getClass().getSimpleName());
    }

    @JavascriptInterface
    public void whenStartEntry() {
        String msg = "whenStartEntry" ;

        boolean readDirectly = false                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          ;

        this.sendMessageUsingHandler( msg, readDirectly ) ;

        Log.v( tag, msg ) ;
    }

    @JavascriptInterface
    public void toggleStop() {
        String msg = "toggleStop" ;

        boolean readDirectly = false ;

        this.sendMessageUsingHandler( msg, readDirectly ) ;

        Log.v( tag, msg ) ;
    }

    @JavascriptInterface
    public void togglePause(float b) {
        String msg = "togglePause( b = %f )" ;
        msg = String.format( msg, b );

        boolean readDirectly = false ;

        this.sendMessageUsingHandler( msg, readDirectly ) ;

        Log.v( tag, msg ) ;
    }

    @JavascriptInterface
    public void addRotation(float ang_deg) {
        String msg = "addRotation( ang_deg = %f )" ;
        msg = String.format( msg, ang_deg );

        boolean readDirectly = false ;

        this.sendMessageUsingHandler( msg, readDirectly ) ;

        Log.v( tag, msg ) ;
    }

    @JavascriptInterface
    public void addDirection(float ang_deg) {
        String msg = "addDirection( ang_deg = %f )" ;
        msg = String.format( msg, ang_deg );

        boolean readDirectly = false ;

        this.sendMessageUsingHandler( msg , readDirectly ) ;

        Log.v( tag, msg ) ;
    }

    @JavascriptInterface
    public void moveToDirection( float dist ) {
        String msg = "moveToDirection( dist = %f )" ;
        msg = String.format( msg, dist );

        boolean readDirectly = false ;

        this.sendMessageUsingHandler( msg, readDirectly );

        Log.v( tag, msg );
    }

    @JavascriptInterface
    public void moveX( float x ) {
        this.moveXYImpl( x, null ) ;
    }

    @JavascriptInterface
    public void moveY( float y ) {
        this.moveXYImpl( null, y ) ;
    }

    public void moveXYImpl( Float x, Float y ) {
        String msg = "moveXY( x = %s , y = %s )" ;
        msg = String.format( msg, "" + x,  "" + y );

        boolean readDirectly = false ;

        this.sendMessageUsingHandler( msg, readDirectly ) ;

        Log.v( tag, msg ) ;
    }

    @JavascriptInterface
    public void locateX( float x ) {
        this.locateXYImpl( x, null) ;
    }

    @JavascriptInterface
    public void locateY( float y ) {
        this.locateXYImpl( null, y) ;
    }

    @JavascriptInterface
    public void locateXY( float x, float y ) {
        this.locateXYImpl( x, y );
    }

    public void locateXYImpl( Float x, Float y ) {
        String msg = "locateXY( x = %s , y = %s )" ;
        msg = String.format( msg, "" + x,  "" + y );

        boolean readDirectly = false ;

        this.sendMessageUsingHandler( msg, readDirectly ) ;

        Log.v( tag, msg ) ;
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

    public void loadEntry() {
        WebView webView = this.webView ;

        String url = "file:///android_asset/entry_html/index.html" ;

        this.webView.loadUrl( url );
    } // -- loadEntry

    class MyWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            loadingStatus.setTextColor( greenLight );
            loadingStatus.setText( "엔트리를 로딩중입니다." );
            progressBar.setVisibility(View.VISIBLE);
            loadingPanel.setVisibility( View.VISIBLE );
        }

        @Override
        public void onPageFinished(WebView view, String url ) {
            super.onPageFinished(view, url );

            loadingStatus.setTextColor( orangeLight );
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