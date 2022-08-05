package com.lyncseapp.lyncse.webViewer;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.lyncseapp.lyncse.R;

public class WebViewLyncseActivity extends AppCompatActivity {
    private WebView wv;
    private String url;
    private FrameLayout flLoad;
    private ImageButton bb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_lyncse);
        getElements();
        url = getIntent().getStringExtra("STRIPE_RECEIPT_URL");
        startWebView();
        setListeners();
        wv.loadUrl(url);
    }
    private void setListeners(){
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void getElements(){
        wv= findViewById(R.id.wvWebViewLyncseActivity);
        flLoad=findViewById(R.id.flLoad_WebViewLyncseActivity);
        bb=findViewById(R.id.imageButtonWebViewLyncseActivity);
    }

    private void startWebView(){
        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setDomStorageEnabled(true);
        //llMain.setVisibility(View.VISIBLE);
        //flmpb.setVisibility(View.GONE);
        wv.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                // Here you can check your new URL.
                System.out.println(url);
                //flpbs.setVisibility(View.VISIBLE);
                if(url.toString().equals("https://example.com/reauth")){
                    System.out.println("reAuth");
                    //llwv.setVisibility(View.GONE);
                    //updateAccountFireBase();

                } else if(url.toString().equals("https://example.com/return")){
                    System.out.println("return");
                    //llwv.setVisibility(View.GONE);
                    //updateAccountFireBase();
                    //Update acct info in database
                    //update acct info in app
                }else{

                }

                super.onPageStarted(view, url,favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                flLoad.setVisibility(View.GONE);
            }

        });
    }
}