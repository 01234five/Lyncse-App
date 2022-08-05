package com.lyncseapp.lyncse.splashScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.lyncseapp.lyncse.MainActivity;
import com.lyncseapp.lyncse.ProfileActivity;
import com.lyncseapp.lyncse.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {
    FirebaseAuth mFirebaseAuth;
    private Handler handler;
    private Runnable r;
    int SPLASH_DISPLAY_LENGTH = 500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mFirebaseAuth = FirebaseAuth.getInstance();
        startActivity();
        handler.postDelayed(r, SPLASH_DISPLAY_LENGTH);
    }

    private void startActivity(){
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                if(mFirebaseUser!=null){
                    //someone logged in
                    System.out.println("already logged in");
                    if(mFirebaseUser.isEmailVerified()) {
                        Intent intent = new Intent(SplashScreenActivity.this, ProfileActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();//so they cant go back to this screen with back button
                    }else{
                        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                }else {
                    //no one logged in
                    System.out.println("not logged in");
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    finish();//so they cant go back to this screen with back button
                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(r);
    }
}