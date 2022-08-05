package com.lyncseapp.lyncse.viewCreatedGigs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lyncseapp.lyncse.R;
import com.lyncseapp.lyncse.User;
import com.lyncseapp.lyncse.stripe.StripeWebView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

public class RequireStripeActivity extends AppCompatActivity {
    private FirebaseUser user;
    private String userId;
    private TextView tvUser;
    private FrameLayout flLoad;
    private LinearLayout llMain;
    private boolean allowClicks;
    private Button stripe;
    private ImageButton bb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_require_stripe);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId= user.getUid();
        allowClicks=true;
        getUser();



    }
    private void startBegin(){
        getElements();
        setListeners();
        llMain.setVisibility(View.VISIBLE);
        flLoad.setVisibility(View.GONE);
    }
    private void getElements(){
        tvUser=findViewById(R.id.textViewUserRequireStripeActivity);
        flLoad=findViewById(R.id.flLoadRequireStripeActivity);
        llMain=findViewById(R.id.llMainRequireStripeActivity);
        stripe=findViewById(R.id.buttonRequireStripe);
        bb=findViewById(R.id.imageButtonRequireStripeActivity);
    }
    private void setListeners(){
        stripe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(allowClicks==true) {
                    allowClicks=false;
                    flLoad.setVisibility(View.VISIBLE);
                    stripeAccount();
                    System.out.println("Clicked");
                }
            }
        });
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void stripeAccount(){
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("text", "123");
        data.put("push", true);

        FirebaseFunctions.getInstance("us-central1") // Optional region: .getInstance("europe-west1")
                .getHttpsCallable( "createStripeConnectedAccount")
                .call().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e);
            }
        }).addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
            @Override
            public void onSuccess(HttpsCallableResult httpsCallableResult) {
                System.out.println("Success");
            }
        }).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                flLoad.setVisibility(View.GONE);
                allowClicks=true;
                System.out.println("done");
                if(task.isSuccessful()){
                    if(task.getResult().getData().toString().equals("noActionNeeded")) {
                        System.out.println("NoActionNeeded");
                        Intent intent = new Intent(RequireStripeActivity.this, StripeWebView.class);
                        intent.putExtra("STRIPE_URL", "NONE");
                        startActivity(intent);
                        finish();

                    } else if(task.getResult().getData().toString().equals("createdAccount")){
                        System.out.println("createdAccount");
                        Intent intent = new Intent(RequireStripeActivity.this, StripeWebView.class);
                        intent.putExtra("STRIPE_URL", "NONE");
                        startActivity(intent);
                        finish();
                    }else {
                        Intent intent = new Intent(RequireStripeActivity.this, StripeWebView.class);
                        intent.putExtra("STRIPE_URL", task.getResult().getData().toString());
                        startActivity(intent);
                        finish();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Task is NOT Successful", Toast.LENGTH_LONG).show();
                }


            }
        });
    }

    private void getUser(){


        FirebaseDatabase.getInstance().getReference("Users/"+userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile !=null){
                    startBegin();
                    String fullName = userProfile.firstName;
                    tvUser.setText("Hey "+fullName+", ");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RequireStripeActivity.this,"Something wrong happened!", Toast.LENGTH_LONG).show();
            }
        });
    }
}