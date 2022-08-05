package com.lyncseapp.lyncse.findRequests.InquiriesViewAll;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lyncseapp.lyncse.ConstructorRequestsRetrieve;
import com.lyncseapp.lyncse.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InquiriesSentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InquiriesSentFragment extends Fragment {
    RecyclerView recyclerView;
    private FirebaseUser user;
    private String userId;

    ImageView ivb;
    FrameLayout flLoad;
    FrameLayout llLoadBottom;

    private DatabaseReference refs;
    private FirebaseDatabase database;

    private List<String> requestIds;
    private List<String> sId;
    private List<String> euId;
    private List<String> gigId;
    private List<String> status;
    private List<Long> createdOn;
    private List<String> euTimeZoneUTC;
    private List<String> title;
    private List<String> price;
    private List<String> uriGig;
    private List<String> creatorName;
    private List<String> uriCreator;
    private List<String> timeStampFormatted;
    private List<String> gigType;
    private List<Integer> typeId;
    private List<String> duration;
    private List<String> durationTime;
    private boolean loadMore;
    private boolean initialLoad;
    RecyclerInquiriesView adapter;
    View vv;
    int pos;
    private int x;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public InquiriesSentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InquiriesSentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InquiriesSentFragment newInstance(String param1, String param2) {
        InquiriesSentFragment fragment = new InquiriesSentFragment();
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
        return inflater.inflate(R.layout.fragment_inquiries_sent, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId= user.getUid();
        requestIds=new ArrayList<String>();
        database = FirebaseDatabase.getInstance();

        initialLoad=true;
        x=0;
        sId=new ArrayList<String>();
        euId=new ArrayList<String>();
        gigId=new ArrayList<String>();
        status=new ArrayList<String>();
        createdOn=new ArrayList<Long>();
        euTimeZoneUTC=new ArrayList<String>();
        title=new ArrayList<String>();
        price=new ArrayList<String>();
        uriGig=new ArrayList<String>();
        creatorName=new ArrayList<String>();
        uriCreator=new ArrayList<String>();
        timeStampFormatted=new ArrayList<String>();
        gigType=new ArrayList<String>();
        typeId=new ArrayList<Integer>();
        duration=new ArrayList<String>();
        durationTime=new ArrayList<String>();
        getElements(view);
        getIds(view);
        vv=view;
        loadMore=false;
    }

    private void getElements(View v){
        recyclerView = v.findViewById(R.id.recyclerViewInquiriesSent);
        flLoad = v.findViewById(R.id.flLoad_InquiriesSent);
        llLoadBottom= v.findViewById(R.id.llLoadBottom_InquiriesSent);
        ivb =v.findViewById(R.id.imageViewDefault_InquiriesSent);
    }
    private void getIdsScroll(View v,String lastKey){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("OutgoingInquiries/"+userId);
        //GETUID OF GIG--------------------------------------------------------------------------------
        System.out.println(lastKey + " LAST KEY");
        ref.orderByKey().endBefore(lastKey).limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int reqSize=0;
                if(snapshot.getValue()==null) {
                    llLoadBottom.setVisibility(View.GONE);
                    return;
                }
                List<String> buffer= new ArrayList<String>();
                for (DataSnapshot userGigsSnapshot: snapshot.getChildren()) {
                    System.out.println(userGigsSnapshot.getKey());
                    buffer.add(userGigsSnapshot.getKey());
                    reqSize=reqSize+1;
                }
                //invertResults();
                Collections.reverse(buffer);
                requestIds.addAll(buffer);
                getRequestInfo(v,reqSize);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The read failed: " + error.getCode());
            }
        });
        //-----------------------------------------------------------------------------------------------
    }
    private void getIds(View v){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("OutgoingInquiries/"+userId);
        //GETUID OF GIG--------------------------------------------------------------------------------
        ref.limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println(snapshot);

                if(snapshot.getValue()==null){
                    flLoad.setVisibility(View.GONE);
                    ivb.setVisibility(View.VISIBLE);
                    return;}
                for (DataSnapshot userGigsSnapshot: snapshot.getChildren()) {
                    requestIds.add(userGigsSnapshot.getKey());
                }
                invertResults();
                getRequestInfo(v,requestIds.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The read failed: " + error.getCode());
            }
        });
        //-----------------------------------------------------------------------------------------------
    }
    private void invertResults(){
        Collections.reverse(requestIds);
    }

    public void getRequestInfo(View v,int reqSize){
        refs = database.getReference("Inquiries");
        //Get title of each gig on arrayList
        refs.child(requestIds.get(pos)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ConstructorRequestsRetrieve userRequests = snapshot.getValue(ConstructorRequestsRetrieve.class);


                if (userRequests != null) {
                    sId.add(userRequests.sId);
                    euId.add(userRequests.euId);
                    gigId.add(userRequests.gigId);
                    status.add(userRequests.status);
                    createdOn.add(userRequests.createdOn);
                    title.add(userRequests.title);
                    price.add(userRequests.price);
                    uriGig.add(userRequests.uriGig);
                    creatorName.add(userRequests.creatorName);
                    uriCreator.add(userRequests.uriCreator);
                    if(snapshot.child("durationTime").exists()){
                        durationTime.add(snapshot.child("durationTime").getValue().toString());
                    }else{
                        durationTime.add(" ");
                    }
                    if(snapshot.child("duration").exists()){
                        duration.add(snapshot.child("duration").getValue().toString());
                    }else{
                        duration.add("Unknown");
                    }
                    if(snapshot.child("paymentIntentCompletedNumber").exists()) {
                        //PaymentIntent Completed
                        System.out.println("snapshotchild " + snapshot.child("paymentIntentCompletedNumber").getValue().toString());
                        typeId.add(99);
                    }
                    else {
                        if (userRequests.status.equals("Inquiring")) {
                            typeId.add(0);
                        } else if (userRequests.status.equals("Accepted")) {
                            System.out.println(userRequests.status + " " + userRequests.title);
                            typeId.add(3);
                        } else {
                            typeId.add(0);
                        }
                    }

                    if(userRequests.gigType != null){
                        gigType.add(userRequests.gigType);
                    }else{
                        gigType.add("Unknown");
                    }
                    formatTimeStamp(x);
                    //-------



                }
                System.out.println("x"+x);
                x=x+1;
                if (x == requestIds.size()) {
                    pos = pos + 1;
                    rAdapter();
                    System.out.println("last value");
                }else {
                    pos = pos + 1;
                    getRequestInfo(v,reqSize);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void rAdapter(){
        loadMore=true;
        if(initialLoad==true) {
            flLoad.setVisibility(View.GONE);
            initialLoad=false;
            System.out.println("INVOKE INVOKE");
            recyclerAdapterInvoke();
        }else{
            llLoadBottom.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
    }

    private void formatTimeStamp(int x){
        Date myDate = new Date(createdOn.get(x));
        SimpleDateFormat formatter= new SimpleDateFormat("dd MMM, yyyy");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        //formatter.setTimeZone(TimeZone.getTimeZone("GMT-8"));
        String dateFormatted = formatter.format(myDate);
        timeStampFormatted.add(dateFormatted);
        System.out.println(dateFormatted);

    }
    public void recyclerAdapterInvoke() {

        System.out.println(typeId);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));


        adapter = new RecyclerInquiriesView(this.getContext(),  sId, euId, gigId,gigType, status, createdOn,title,price,uriGig,creatorName,requestIds,uriCreator,timeStampFormatted,typeId,duration,durationTime);
        recyclerView.setAdapter(adapter);


        //Scroll listener for end of list
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();

                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) { //1 for down
                    System.out.println("Scroll");
                    if (loadMore == true) {
                        loadMore=false;
                        llLoadBottom.setVisibility(View.VISIBLE);
                        getIdsScroll(vv,requestIds.get(requestIds.size()-1));
                    }
                }



            }
        });

    }
}