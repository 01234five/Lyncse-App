package com.lyncseapp.lyncse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.lyncseapp.lyncse.findRequests.CreatePostActivity;
import com.lyncseapp.lyncse.outside.CreateOutsideActivity;
import com.lyncseapp.lyncse.search.ActivitySearch;
import com.lyncseapp.lyncse.viewCreatedGigs.CreatedViewActivity;
import com.lyncseapp.lyncse.viewCreatedGigs.RequireStripeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class myGigs extends AppCompatActivity {


    private Button createGig;
    private Button viewMyGigs;
    private Button createOutsideGig;
    private Button createFindRequest;
    private FrameLayout flLoad;
    private LinearLayout llMain;
    private DatabaseReference reference;
    private ImageButton bb;

    private List<String> mList;



    private String userId;
    private FirebaseUser user;
    private BottomNavigationView bottomNavigationView;
    private boolean verificationPassed;
    private ValueEventListener mListener;
    private boolean changedActivities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gigs);
        mList = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId= user.getUid();
        verificationPassed=false;
        changedActivities=false;
        bottomNav();
        getStripeAccount();

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("destroy");

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        overridePendingTransition(0,0);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        bottomNavigationView.setSelectedItemId(R.id.myGigsBottomBar);

    }

    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

/*private void uncheckMenuItems(){
    int size = bottomNavigationView.getMenu().size();
    for (int i = 0; i < size; i++) {
        bottomNavigationView.getMenu().getItem(i).setCheckable(false);
    }
}

private void allowMenuItemsCheck(){
    int size = bottomNavigationView.getMenu().size();
    for (int i = 0; i < size; i++) {
        bottomNavigationView.getMenu().getItem(i).setCheckable(true);
    }
}*/

    private void getStripeAccount(){
        reference=FirebaseDatabase.getInstance().getReference("StripeUsers/"+userId+"/Stripe/charges_enabled");
        mListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot==null){
                    System.out.println("charges_enabled=false");
                }else{
                    if(changedActivities==false) {
                        if (snapshot.getValue() == null) {
                            System.out.println("need to finish Stripe account requirements");
                            startActivity(new Intent(myGigs.this, RequireStripeActivity.class));
                            finishAffinity();//so they cant go back to this screen with back button
                        } else if (snapshot.getValue().toString().equals("false")) {
                            System.out.println("need to finish Stripe account requirements");
                            startActivity(new Intent(myGigs.this, RequireStripeActivity.class));
                            finishAffinity();//so they cant go back to this screen with back button

                        } else if (snapshot.getValue().toString().equals("true")) {
                            verificationPassed = true;
                            System.out.println("Stripe account charges_enables= " + snapshot.getValue());
                            getElements();
                            setListeners();
                            llMain.setVisibility(View.VISIBLE);
                            flLoad.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        reference.addListenerForSingleValueEvent(mListener);

    }
    private void getElements(){
        createGig = (Button) findViewById(R.id.createGigVirtualActivity);

        createOutsideGig =(Button) findViewById(R.id.createGigOutsideActivity);
        viewMyGigs = (Button) findViewById(R.id.buttonViewGigs);
        createFindRequest = findViewById(R.id.createFindPost);
        flLoad=findViewById(R.id.flLoad_myGigs);
        llMain=findViewById(R.id.llMainLayoutMyGigs);
        bb=findViewById(R.id.imageButtonMyGigs);
    }
    private void setListeners(){
        createOutsideGig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                startActivity(new Intent(myGigs.this, CreateOutsideActivity.class));

            }
        });
        createGig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                startActivity(new Intent(myGigs.this,MyVirtualActivity.class));

            }
        });
        viewMyGigs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(myGigs.this, CreatedViewActivity.class));
            }
        });

        createFindRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(myGigs.this, CreatePostActivity.class));
            }
        });
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
                overridePendingTransition(0,0);
            }
        });
    }
    private void bottomNav(){
        bottomNavigationView = findViewById(R.id.bottomNavigationViewMyGigs);
        bottomNavigationView.setSelectedItemId(R.id.myGigsBottomBar);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.searchBottomBar:
                        changedActivities=true;
                        Intent intent = new Intent(myGigs.this, ActivitySearch.class);
                        startActivity(intent);
                        if(verificationPassed==false){
                            finishAffinity();
                        }
                        overridePendingTransition(0,0);
                        return true;


                    case R.id.homeBottomBar:
                        changedActivities=true;
                        startActivity(new Intent(myGigs.this, ProfileActivity.class));
                        if(verificationPassed==false){
                            finishAffinity();
                        }
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.myGigsBottomBar:
                        return true;
                    case R.id.requestsBottomBar:
                        changedActivities=true;
                        startActivity(new Intent(myGigs.this, RequestsView.class));
                        if(verificationPassed==false){
                            finishAffinity();
                        }
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.chatBottomBar:
                        changedActivities=true;
                        startActivity(new Intent(myGigs.this, Messenger.class));
                        if(verificationPassed==false){
                            finishAffinity();
                        }
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });


    }
}