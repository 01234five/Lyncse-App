package com.lyncseapp.lyncse.policies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lyncseapp.lyncse.MainActivity;
import com.lyncseapp.lyncse.ProfileActivity;
import com.lyncseapp.lyncse.R;

public class InfoActivity extends AppCompatActivity {

    private TextView tvPrivacyPolicy;
    private TextView tvTermsOfService;
    private TextView tvContact;
    private ImageButton bb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        getElements();
        setListeners();

    }

    private void getElements(){
        tvPrivacyPolicy = findViewById(R.id.textViewPrivacyPolicyInfo);
        tvTermsOfService= findViewById(R.id.textViewTermsOfServiceInfo);
        tvContact = findViewById(R.id.textViewInfoContactLyncse);
        bb=findViewById(R.id.imageButtonInfoActivity);
    }
    private void setListeners(){
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InfoActivity.this, InfoWebViewerActivity.class);
                intent.putExtra("OPTION", "contact");
                startActivity(intent);
            }
        });
        tvPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InfoActivity.this, InfoWebViewerActivity.class);
                intent.putExtra("OPTION", "privacy_policy");
                startActivity(intent);
            }
        });

        tvTermsOfService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InfoActivity.this, InfoWebViewerActivity.class);
                intent.putExtra("OPTION", "terms_of_service");
                startActivity(intent);
            }
        });
    }
}