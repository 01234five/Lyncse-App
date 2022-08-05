package com.lyncseapp.lyncse.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lyncseapp.lyncse.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ConfirmationAcceptedActivity extends AppCompatActivity {
    private ImageButton bb;
    FrameLayout flLoad;
    TextView tvMessage;
    TextView tvId;
    String reqId;
    private Button bReturn;
    private FirebaseUser user;
    private String userId;
    private String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getExtras();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId= user.getUid();
        getUser();

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private void getExtras(){
        reqId=getIntent().getStringExtra("REQ_ID");
    }
    private void initializeLayout(){
        setContentView(R.layout.activity_confirmation_accepted);
        getElements();
        setElements();
        setListeners();
        flLoad.setVisibility(View.GONE);
    }
    private void getElements(){
        bb=findViewById(R.id.imageButtonConfirmationAcceptedActivity);
        flLoad = findViewById(R.id.flLoad_ConfirmationAcceptedActivity);
        tvMessage= findViewById(R.id.textView24ConfirmationAcceptedActivity);
        bReturn= findViewById(R.id.buttonReturnConfirmationAcceptedActivity);
        tvId= findViewById(R.id.textViewIdConfirmationAcceptedActivity);
    }
    private void setElements() {
        tvMessage.setText(message);
        tvId.setText(reqId);
    }
    private void setListeners(){
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });
        bReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });
    }

    private void getUser() {
        FirebaseDatabase.getInstance().getReference("Users/" + userId + "/firstName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                message = snapshot.getValue().toString() + ", task completed successfully. Request to accept has been received.";
                initializeLayout();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ConfirmationAcceptedActivity.this, "Something wrong happened!", Toast.LENGTH_LONG).show();
            }
        });
    }
}