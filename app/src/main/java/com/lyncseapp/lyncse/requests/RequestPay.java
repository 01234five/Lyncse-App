package com.lyncseapp.lyncse.requests;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lyncseapp.lyncse.R;
import com.lyncseapp.lyncse.activities.PaymentCompletedActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RequestPay extends AppCompatActivity {
    Button payWithStripeButton;
    private String reqId;
    private PaymentSheet paymentSheet;
    String IntentClientSecret;

    private String uri;
    private String title;
    private String price;
    private String theirName;
    private String myName;

    TextView tvTitle;
    TextView tvPrice;
    TextView tvMe;
    TextView tv1;
    TextView tv2;
    ImageView ivPic;
    FrameLayout flLoad;
    LinearLayout llMain;
    ImageButton bb;
    private FirebaseUser user;
    private String userId;
    private boolean allowOtherClicks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_pay);
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        getUserName();
        allowOtherClicks=true;
    }
    private void getBundle(){
        reqId=getIntent().getStringExtra("REQ_ID");
        uri = getIntent().getStringExtra("REQ_URI");
        title = getIntent().getStringExtra("REQ_TITLE");
        price = getIntent().getStringExtra("REQ_PRICE");
        theirName = getIntent().getStringExtra("REQ_THEIRNAME");
    }
    private void getElements(){
        payWithStripeButton=findViewById(R.id.requestPay_Pay_with_stripe);
        llMain=findViewById(R.id.llMain_RequestPayActivity);
        flLoad=findViewById(R.id.flLoad_RequestPayActivity);
        tvTitle= findViewById(R.id.gigInfoRecyclerRequestGigReviewRequestPayActivity);
        tvPrice=findViewById(R.id.textViewPriceRecyclerRequestGigReviewRequestPayActivity);
        ivPic=findViewById(R.id.imageView2GigImageRequestGigReviewRequestPayActivity);
        tv1=findViewById(R.id.textViewCreatorRequestGigReviewRequestPayActivity);
        tv2=findViewById(R.id.textViewInstrunction2RequestGigReviewRequestPayActivity);
        tvMe=findViewById(R.id.textViewEURequestGigReviewRequestPayActivity);
        bb=findViewById(R.id.imageButtonRequestPayActivity);
    }
    private void setElements(){
        tvTitle.setText(title);
        tvPrice.setText(price);
        tvMe.setText("Hey "+myName+",");
        tv2.setText("When payment completed, it will be transferred to "+theirName+" account. You will also receive a receipt via email. You could also view the receipt in the info tab of the post.");

        if(uri.equals("Default")) {
            Glide.with(this).load(R.drawable.ic_baseline_android_200).into(ivPic);
        }else{
            Glide.with(this).load(uri).into(ivPic);
        }
    }
    private void getUserName(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users/"+userId);

        ref.child("firstName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myName=snapshot.getValue().toString();
                //finish the page.
                getBundle();
                getElements();
                setElements();
                setListeners();
                llMain.setVisibility(View.VISIBLE);
                flLoad.setVisibility(View.GONE);
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
                if(allowOtherClicks==true) {
                    allowOtherClicks=false;
                    finish();
                }
            }
        });
        payWithStripeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(allowOtherClicks==true) {
                    allowOtherClicks=false;
                    flLoad.setVisibility(View.VISIBLE);
                    stripeCreateIntent();
                }
            }
        });
    }

    private void stripeCreateIntent(){
        Map<String, Object> data = new HashMap<>();
        data.put("reqId", reqId);

        FirebaseFunctions.getInstance("us-central1") // Optional region: .getInstance("europe-west1")
                .getHttpsCallable( "stripePaymentIntent")
                .call(data).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                flLoad.setVisibility(View.GONE);
                allowOtherClicks=true;
                System.out.println(e.getMessage());
                Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
            @Override
            public void onSuccess(HttpsCallableResult httpsCallableResult) {
                flLoad.setVisibility(View.GONE);
                System.out.println("Success");
            }
        }).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                flLoad.setVisibility(View.GONE);
                System.out.println("done");
                if(task.isSuccessful()){
                    if(task.getResult().getData().toString().equals("noActionNeeded")) {
                    }else {
                        System.out.println(task.getResult().getData());
                        try {
                            final JSONObject result = new JSONObject(task.getResult().getData().toString());
                            IntentClientSecret = result.getString("paymentIntent");
                            PaymentConfiguration.init(getApplicationContext(), result.getString("publishableKey"));
                            System.out.println(IntentClientSecret);
                            presentPaymentSheet();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Unable to create payment.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void presentPaymentSheet() {
        final PaymentSheet.GooglePayConfiguration googlePayConfiguration =
                new PaymentSheet.GooglePayConfiguration(
                        PaymentSheet.GooglePayConfiguration.Environment.Production,
                        "US"
                );
        final PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("Lyncse")
                .googlePay(googlePayConfiguration).build();
        paymentSheet.presentWithPaymentIntent(
                IntentClientSecret,configuration
        );
    }
    private void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            System.out.println("Payment Canceled");
            allowOtherClicks=true;
            Toast.makeText(
                    this,
                    "Payment Cancelled.",
                    Toast.LENGTH_LONG
            ).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            System.out.println("Error" + ((PaymentSheetResult.Failed) paymentSheetResult).getError());
            allowOtherClicks=true;
            Toast.makeText(
                    this,
                    "Error. "+ ((PaymentSheetResult.Failed) paymentSheetResult).getError(),
                    Toast.LENGTH_LONG
            ).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            // Display for example, an order confirmation screen
            System.out.println("Completed Payment received");
            allowOtherClicks=true;
            DatabaseReference reference;
            reference = FirebaseDatabase.getInstance().getReference("Requests/"+reqId+"/uStatus");
            reference.setValue("Processing");
            Toast.makeText(
                    this,
                    "Completed Payment received.",
                    Toast.LENGTH_LONG
            ).show();
            Intent intent = new Intent(RequestPay.this, PaymentCompletedActivity.class);
            intent.putExtra("REQ_ID", reqId);
            startActivity(intent);
        }
    }
}