package com.lyncseapp.lyncse.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lyncseapp.lyncse.R;
import com.lyncseapp.lyncse.webViewer.WebViewLyncseActivity;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestInfoFragment extends Fragment {
    private String theirName;
    private String theirUri;
    private String status;
    private String createdOn;
    private String title;
    private String price;
    private String reqId;
    private String duration;
    private String durationTime;
    private TextView theirNameTv;
    private TextView theirUriTv;
    private TextView statusTv;
    private TextView createdOnTv;
    private TextView titleTv;
    private TextView priceTv;
    private TextView reqIdTv;
    private TextView paymentCompletedTv;
    private TextView chargeIdTv;
    private TextView paymentIntentIdTv;
    private TextView tvDuration;
    private LinearLayout llStripe;
    private LinearLayout llInquiriesInfo;
    private FrameLayout flLoadRequestInfo;
    private Button bReceipt;
    private FirebaseUser user;
    private String userId;
    private String creatorId;
    private Button btnCancel;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RequestInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RequestInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RequestInfoFragment newInstance(String param1, String param2) {
        RequestInfoFragment fragment = new RequestInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request_info, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId= user.getUid();
        getBungleArgs();
        getViewElements(view);
        getPaymentInfo(view);
    }

    private void setListenersWithContext(View v){
        bReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flLoadRequestInfo.setVisibility(View.VISIBLE);
                FirebaseDatabase.getInstance().getReference("/Requests/"+
                        reqId+"/Stripe/Info/Charge/receipt_url").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        System.out.println(snapshot.getValue());
                        if(snapshot.exists()) {
                            flLoadRequestInfo.setVisibility(View.GONE);
                            Intent intent = new Intent(getActivity(), WebViewLyncseActivity.class);
                            intent.putExtra("STRIPE_RECEIPT_URL", snapshot.getValue().toString());
                            startActivity(intent);
                        }else{
                            flLoadRequestInfo.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        flLoadRequestInfo.setVisibility(View.GONE);
                    }
                });
            }
        });
    }
    private void setListeners(){
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(requireContext()).setTitle("Confirm cancel activity?")
                        .setMessage("Are you sure?")
                        .setPositiveButton("YES",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        serverRequestCancel();
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
        });
    }
    private void serverRequestCancel(){
        flLoadRequestInfo.setVisibility(View.VISIBLE);
        // Perform Action & Dismiss dialog
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("id",reqId);
        data.put("type","Request");
        FirebaseFunctions.getInstance("us-central1") // Optional region: .getInstance("europe-west1")
                .getHttpsCallable( "requestCancel")
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
                flLoadRequestInfo.setVisibility(View.GONE);
                System.out.println("done");
                if(task.isSuccessful()){
                    if(task.getResult().getData().toString().equals("success")) {
                        Toast.makeText(requireContext(),"Activity cancelled.", Toast.LENGTH_LONG).show();
                        getActivity().finishAffinity();
                    }
                }else{
                    Toast.makeText(requireContext(), "Task is NOT Successful", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void getViewElements(View v){
        reqIdTv=v.findViewById(R.id.textViewReqIdRequestInfo);
        theirNameTv=v.findViewById(R.id.textViewCreatorNameRequestInfo);
        titleTv=v.findViewById(R.id.textViewTitleRequestInfo);
        priceTv=v.findViewById(R.id.textViewPriceRequestInfo);
        statusTv=v.findViewById(R.id.textViewStatusRequestInfo);
        paymentCompletedTv=v.findViewById(R.id.textViewStripePaymentCompletedRequestInfo);
        chargeIdTv=v.findViewById(R.id.textViewStripeChargeIdRequestInfo);
        paymentIntentIdTv=v.findViewById(R.id.textViewStripePaymentIntentIdRequestInfo);
        llStripe=v.findViewById(R.id.linearLayoutStripeIdsRequestInfo);
        llInquiriesInfo=v.findViewById(R.id.llRequestInfo);
        flLoadRequestInfo=v.findViewById(R.id.flLoadRequestInfo);
        bReceipt=v.findViewById(R.id.buttonReceiptRequestInfo);
        tvDuration=v.findViewById(R.id.textViewDurationRequestInfo);
        btnCancel=v.findViewById(R.id.buttonCancelRequestInfo);
    }
    private void setElements(){
        reqIdTv.setText(reqId);
        titleTv.setText(title);
        priceTv.setText(price);
        statusTv.setText(status);
        tvDuration.setText(duration + " " + durationTime);

        if(userId.equals(creatorId)){
            //Me
            theirNameTv.setText("Me");
        }else {
            theirNameTv.setText(theirName);
        }
    }
    private void getBungleArgs(){
        reqId = this.getArguments().getString("REQ_ID");
        theirName=this.getArguments().getString("THEIR_NAME");
        theirUri=this.getArguments().getString("THEIR_URI");
        price=this.getArguments().getString("PRICE");
        title=this.getArguments().getString("TITLE");
        status=this.getArguments().getString("STATUS");
        creatorId=this.getArguments().getString("CREATOR_ID");
        duration=this.getArguments().getString("REQ_DURATION");
        durationTime=this.getArguments().getString("REQ_DURATIONTIME");
    }
    private void getPaymentInfo(View view){
        FirebaseDatabase.getInstance().getReference("/Requests/"+
                reqId+"/Stripe/Info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!status.equals("Completed")){
                    if(!status.equals("Cancelled")) {
                        setListeners();
                        btnCancel.setVisibility(View.VISIBLE);
                    }
                }
                if(snapshot.getValue() == null) {
                    System.out.println("Does not exist.");
                    setElements();
                    paymentCompletedTv.setText("No");
                    flLoadRequestInfo.setVisibility(View.GONE);
                    llInquiriesInfo.setVisibility(View.VISIBLE);
                    return;
                }else{
                    setElements();
                    paymentCompletedTv.setText("Yes");
                    chargeIdTv.setText(snapshot.child("Charge").child("id").getValue().toString());
                    paymentIntentIdTv.setText(snapshot.child("PaymentIntent").child("id").getValue().toString());
                    llStripe.setVisibility(View.VISIBLE);
                    flLoadRequestInfo.setVisibility(View.GONE);
                    llInquiriesInfo.setVisibility(View.VISIBLE);
                    if(userId.equals(creatorId)){
                        System.out.println("dont show receipt");
                    }else {
                        bReceipt.setVisibility(View.VISIBLE);
                        setListenersWithContext(view);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}