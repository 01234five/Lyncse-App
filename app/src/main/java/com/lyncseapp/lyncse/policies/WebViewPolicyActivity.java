package com.lyncseapp.lyncse.policies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lyncseapp.lyncse.R;

public class WebViewPolicyActivity extends AppCompatActivity {
    private WebView webView;
    private String option;
    private String customHtml;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_policy);

        //option = getIntent().getStringExtra("OPTION");
        //setCustomHtml();
        //createWebView();
    }


    private void setCustomHtml(){
        System.out.println(option);
        if(option.equals("privacy_policy")){
            customHtml = "";
        }else if (option.equals("terms_of_service")){
            customHtml = "<a href=\"https://www.iubenda.com/terms-and-conditions/30155994\" class=\"iubenda-white no-brand iubenda-noiframe iubenda-embed iubenda-noiframe iub-body-embed\" title=\"Terms and Conditions\">Terms and Conditions</a><script type=\"text/javascript\">(function (w,d) {var loader = function () {var s = d.createElement(\"script\"), tag = d.getElementsByTagName(\"script\")[0]; s.src=\"https://cdn.iubenda.com/iubenda.js\"; tag.parentNode.insertBefore(s,tag);}; if(w.addEventListener){w.addEventListener(\"load\", loader, false);}else if(w.attachEvent){w.attachEvent(\"onload\", loader);}else{w.onload = loader;}})(window, document);</script>";
        }
    }

    private void createWebView(){
        webView= new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadData(customHtml,"text/html","UTF-8");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(webView!=null){
            webView.destroy();
        }
    }
}