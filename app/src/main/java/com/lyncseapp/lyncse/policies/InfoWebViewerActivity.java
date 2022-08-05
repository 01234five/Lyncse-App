package com.lyncseapp.lyncse.policies;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.lyncseapp.lyncse.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class InfoWebViewerActivity extends AppCompatActivity {
    private WebView webView;
    private String option;
    private String customHtml;
    private ImageButton bb;
    private FrameLayout flLoad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_web_viewer);

        option = getIntent().getStringExtra("OPTION");
        getElements();
        setCustomHtml();
        createWebView();
        setListeners();
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
        webView= findViewById(R.id.webViewInfoWebViewerActivity);
        bb= findViewById(R.id.imageButtonInfoWebViewereActivity);
        flLoad = findViewById(R.id.flLoad_InfoWebViewer);
    }

    private void setCustomHtml(){
        System.out.println(option);
        if(option.equals("privacy_policy")){

        }else if (option.equals("terms_of_service")){
            customHtml = "<a href=\"https://www.iubenda.com/terms-and-conditions/30155994\" class=\"iubenda-white no-brand iubenda-noiframe iubenda-embed iubenda-noiframe iub-body-embed\" title=\"Terms and Conditions\">Terms and Conditions</a><script type=\"text/javascript\">(function (w,d) {var loader = function () {var s = d.createElement(\"script\"), tag = d.getElementsByTagName(\"script\")[0]; s.src=\"https://cdn.iubenda.com/iubenda.js\"; tag.parentNode.insertBefore(s,tag);}; if(w.addEventListener){w.addEventListener(\"load\", loader, false);}else if(w.attachEvent){w.attachEvent(\"onload\", loader);}else{w.onload = loader;}})(window, document);</script>";
        }else if(option.equals("contact")){

        }else{
            option = "none";
        }
    }

    private void createWebView(){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                flLoad.setVisibility(View.VISIBLE);
                // Here you can check your new URL.

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
        if(option.equals("terms_of_service")) {
            webView.loadData(customHtml, "text/html", "UTF-8");
        } else if(option.equals("privacy_policy")){
            webView.loadUrl("https://lyncse.wixsite.com/home/privacy-policy");
        } else if(option.equals("contact")){
            webView.loadUrl("https://lyncse.wixsite.com/home/contact");
        } else{
            webView.loadUrl("https://lyncse.wixsite.com/home");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(webView!=null){
            webView.destroy();
        }
    }
}