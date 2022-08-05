package com.lyncseapp.lyncse.requests;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lyncseapp.lyncse.R;
import com.lyncseapp.lyncse.activities.ConfirmationAcceptedActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RequestAccept extends AppCompatActivity {

    private String uri;
    private String title;
    private String price;
    private String theirName;
    private FirebaseUser user;
    private String userId;
    private String myName;
    private String requestId;
    private boolean done;

    TextView tvTitle;
    TextView tvPrice;
    TextView tvMe;
    TextView tv1;
    TextView tv2;
    ImageView ivPic;
    Button bAcceptRequest;
    ImageButton bb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uri = getIntent().getStringExtra("REQ_URI");
        title = getIntent().getStringExtra("REQ_TITLE");
        price = getIntent().getStringExtra("REQ_PRICE");
        theirName = getIntent().getStringExtra("REQ_THEIRNAME");
        requestId=getIntent().getStringExtra("REQ_ID");

        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        done=false;
        getUserName();

    }
    private void getElements(){
        tvTitle= findViewById(R.id.gigInfoRecyclerRequestGigReviewAcceptRequest);
        tvPrice=findViewById(R.id.textViewPriceRecyclerRequestGigReviewAcceptRequest);
        ivPic=findViewById(R.id.imageView2GigImageRequestGigReviewAcceptRequest);
        tv1=findViewById(R.id.textViewCreatorRequestGigReviewAcceptRequestAcceptRequest);
        tv2=findViewById(R.id.textViewInstrunction2RequestGigReviewAcceptRequest);
        tvMe=findViewById(R.id.textViewEURequestGigReviewAcceptRequestAcceptRequest);
        bAcceptRequest=findViewById(R.id.buttonSendRequestActivityRequestGigReviewAcceptRequest);
        bb=findViewById(R.id.imageButtonAcceptRequestAcceptRequest);
    }
private void getUserName(){
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("Users/"+userId);

    ref.child("firstName").addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            myName=snapshot.getValue().toString();
            //finish the page.
            setContentView(R.layout.activity_request_accept);
            getElements();
            setElements();
            setListeners();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });

}
private void setListeners(){
    bb.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    });
        bAcceptRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(done==false) {
                    done=true;
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference ref = database.getReference("Requests/" + requestId + "/uStatus");
                    ref.setValue("Accepted").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Accepted: "+ title, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(RequestAccept.this, ConfirmationAcceptedActivity.class);
                                intent.putExtra("REQ_ID", requestId);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });
}
    private void setElements(){
        tvTitle.setText(title);
        tvPrice.setText(price);
        tvMe.setText("Hey "+myName+",");
        tv1.setText("You will be accepting a request from " +theirName+" for:");
        tv2.setText("After accepting, "+theirName+" will be able to send payment. Its up to "+theirName+" and you to decide when the payment will be sent. A recommended approach would be:");

        if(uri.equals("Default")) {
            Glide.with(this).load(R.drawable.ic_baseline_android_200).into(ivPic);
        }else{
            Glide.with(this).load(uri).into(ivPic);
        }
    }
}