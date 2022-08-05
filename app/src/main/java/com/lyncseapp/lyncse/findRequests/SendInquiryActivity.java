package com.lyncseapp.lyncse.findRequests;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lyncseapp.lyncse.R;
import com.lyncseapp.lyncse.Requests;
import com.lyncseapp.lyncse.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.zone.ZoneRules;
import java.util.HashMap;
import java.util.Map;

public class SendInquiryActivity extends AppCompatActivity {
    private String creator;
    private String sId;
    private String uri;
    private String title;
    private String eu;
    private String price;
    private String euId;
    private String gigId;
    private String uriCreator;
    private String uriEu;
    private String gigType;
    private String duration;
    private String gigStatus;
    private String gigTime;
    private String gigOption;




    private TextView euTextView;
    private TextView creatorTextView;
    private TextView titleTextView;
    private TextView priceTextView;
    private ImageView uriImageView;
    private TextView instruction1TextView;
    private TextView instructions2TextView;
    private Button bSendRequest;

    ImageButton bb;
    FrameLayout flLoad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentExtra();
        if(gigStatus.equals("INACTIVE")){
            Toast.makeText(SendInquiryActivity.this,"Status inactive",Toast.LENGTH_LONG).show();
            finish();
        }
        setContentView(R.layout.activity_send_inquiry);
        getElements();
        setElements();
        setListeners();
    }
    private void setListeners(){
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        bSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUriProfile();
            }
        });
    }
    private void getElements(){


        bb=findViewById(R.id.imageButtonSendInquiryActivity);
        euTextView=(TextView) findViewById(R.id.textViewEUSendInquiryActivity);
        creatorTextView=(TextView) findViewById(R.id.textViewCreatorSendInquiryActivity);
        titleTextView =(TextView) findViewById(R.id.gigInfoRecyclerSendInquiryActivity);
        priceTextView = (TextView) findViewById(R.id.textViewPriceRecyclerSendInquiryActivity);
        uriImageView=(ImageView) findViewById(R.id.imageView2GigImageSendInquiryActivity);
        instruction1TextView=(TextView) findViewById(R.id.textViewInstructionsSendInquiryActivity);
        instructions2TextView=(TextView) findViewById(R.id.textViewInstrunction2SendInquiryActivity);
        bSendRequest=findViewById(R.id.buttonSendInquiryActivity);
        flLoad=findViewById(R.id.flLoad_SendInquiryActivity);

    }
    private void setElements() {
        if (uri.equals("Default")) {
            Glide.with(SendInquiryActivity.this)
                    .load(R.drawable.ic_baseline_android_200)
                    .into(uriImageView);
        } else {
            Glide.with(SendInquiryActivity.this)
                    .load(uri)
                    .into(uriImageView);
        }

        euTextView.setText("Hey " + eu+",");
        creatorTextView.setText("you will be sending a inquiry to "+creator+" for:");
        titleTextView.setText(title);
        priceTextView.setText("$"+price);
        instruction1TextView.setText("This inquiry will be reviewed by "+creator+".");
        instructions2TextView.setText("In the meantime feel free to contact "+creator+" and discuss the above.");



    }

        private void sendRequest(){
//        System.out.println("sendRequests");
//        mDatabase = FirebaseDatabase.getInstance().getReference("Inquiries/");
//
//        Requests r = new Requests(sId,euId,gigId,gigType,"Inquiring", ServerValue.TIMESTAMP,getUTC(),title,price,duration,uri,creator,uriCreator,eu,uriEu);
//
//        FirebaseDatabase.getInstance().getReference("Inquiries")
//                .child(mDatabase.push().getKey()).setValue(r).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful())
//                {
//                    Toast.makeText(SendInquiryActivity.this,"Inquiry sent", Toast.LENGTH_LONG).show();
//                }
//                else
//                {
//                    Toast.makeText(SendInquiryActivity.this,"Failed. Try again.", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
            serverRequest();
    }

    private void serverRequest(){
        flLoad.setVisibility(View.VISIBLE);
        System.out.println("gigType "+ gigType);
        Requests r = new Requests(sId,euId,gigId,gigType,"Requesting", ServerValue.TIMESTAMP,getUTC(),title,price,duration,gigTime,uri,creator,uriCreator,eu,uriEu);

        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("sId",sId);
        data.put("euId",euId);
        data.put("gigId", gigId);
        data.put("gigType",gigType);
        data.put("gigServerTime",ServerValue.TIMESTAMP);
        data.put("gigUTC",getUTC());
        data.put("gigCreatorName",creator);
        data.put("gigCreatorUri",uriCreator);
        data.put("gigEuName",eu);
        data.put("gigEuUri",uriEu);
        data.put("gigOption",gigOption);

        Gson gson = new Gson();
        String jsonString = gson.toJson(r);
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FirebaseFunctions.getInstance("us-central1") // Optional region: .getInstance("europe-west1")
                .getHttpsCallable( "createInquiry")
                .call(data).addOnFailureListener(new OnFailureListener() {
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
                System.out.println("done");
                if(task.isSuccessful()){
                    if(task.getResult().getData().toString().equals("success")) {
                        System.out.println("response success");
                        String data = "ok";
                        Intent intent = new Intent();
                        intent.putExtra("MyData", data);
                        setResult(200, intent);
                        finish();
                    }
                    Toast.makeText(SendInquiryActivity.this,"Inquiry sent", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Task is NOT Successful", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getIntentExtra(){
        euId=getIntent().getStringExtra("GIG_EUID");
        eu= getIntent().getStringExtra("GIG_EUNAME");
        uri = getIntent().getStringExtra("GIG_URI");
        title = getIntent().getStringExtra("GIG_TITLE");
        creator = getIntent().getStringExtra("GIG_SNAME");
        sId = getIntent().getStringExtra("GIG_SID");
        price = getIntent().getStringExtra("GIG_PRICE");
        gigId= getIntent().getStringExtra("GIG_GIGID");
        uriCreator=getIntent().getStringExtra("GIG_URICREATOR");
        gigType=getIntent().getStringExtra("GIG_TYPE");
        duration=getIntent().getStringExtra("GIG_DURATION");
        gigTime=getIntent().getStringExtra("GIG_TIME");
        gigStatus=getIntent().getStringExtra("GIG_STATUS");
        gigOption=getIntent().getStringExtra("GIG_OPTION");
        System.out.println(euId +" "+sId+" "+ gigId);


    }
    private void getUriProfile(){ //get the uri only when send request button is pressed. sendrequest() will be executed only when we get the uriProfile.

        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(euId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile !=null){

                    uriEu=userProfile.uriProfile;
                    sendRequest();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    private String getUTC(){
        //Get ZONE UTC time
        ZoneId zone = ZoneId.systemDefault() ;
        ZoneRules rules = zone.getRules() ;
        Instant instant = Instant.now() ;
        ZoneOffset offset = rules.getOffset( instant ) ;

        return offset.toString();
    }
}