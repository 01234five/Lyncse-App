package com.lyncseapp.lyncse.findRequests;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.lyncseapp.lyncse.ChatGig;
import com.lyncseapp.lyncse.GigOptions;
import com.lyncseapp.lyncse.Gigs;
import com.lyncseapp.lyncse.R;
import com.lyncseapp.lyncse.User;
import com.lyncseapp.lyncse.constructors.LocationConstr;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class SelectedGigFindActivity extends AppCompatActivity {
    private String gigId;
    private String creatorId;
    private DatabaseReference ref;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private String userId_seller;
    private String authUserName;
    private String authUserId;

    private TextView titleTextView;
    private TextView gigInfoTextView;
    private TextView distanceAway;

    private ImageView gigBanner;

    private TextView nameTextView;

    private ImageView profilePic;

    private TextView price1TextView;
    private TextView duration1TextView;
    private TextView time1TextView;
    private ToggleButton toggleButtonGig1;
    private LinearLayout clMessage;
    private ImageButton ibChat;

    private TextView price2TextView;
    private TextView duration2TextView;
    private TextView time2TextView;
    private ToggleButton toggleButtonGig2;

    private TextView price3TextView;
    private TextView duration3TextView;
    private TextView time3TextView;
    private ToggleButton toggleButtonGig3;


    private String userName;
    private String imageUri;
    ActivityResultLauncher<String> mGetContent;
    private Button request;
    private String optionSelected;
    private String optionSelectedPrice;
    private String optionSelectedDuration;
    private String optionSelectedTime;
    private String optionSelectedStatus;
    private String uriCreator;
    private String type;
    private String price1;
    private String price2;
    private String price3;
    private ImageButton bb;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private int LOCATION_REQUEST_CODE = 1001;
    ActivityResultLauncher<Intent> activityResultLaunch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityResultLauncher();
        setContentView(R.layout.activity_selected_gig_find);

        gigId = getIntent().getStringExtra("GIG_ID");
        creatorId = getIntent().getStringExtra("GIG_CREATORID");
        type = getIntent().getStringExtra("GIG_TYPE");
        System.out.println(type + " TYPE TYPE TYPE");
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        imageUri = null;


        getEUInfo();
        getViewElements();
        System.out.println(creatorId);
        System.out.println(authUserId);
        if (creatorId.equals(authUserId)) {
            View v = findViewById(R.id.btnRequestGigSelectedFindRequest);
            v.setVisibility(View.GONE);
            ibChat.setVisibility(View.GONE);
            distanceAway.setVisibility(View.GONE);

        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        } else {
            askLocationPermission();
        }

        getUserId();
        //getProfilePic();
        getGigBody();
        getUri();
        getGigOptions1();
        getGigOptions2();
        getGigOptions3();
        removeToggleButtonShadows();
        setListeners();


        System.out.println(creatorId + " " + authUserId);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("destroy");
        activityResultLaunch.unregister();
    }
    private void activityResultLauncher(){
        activityResultLaunch = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == 200) {
                            Intent data = result.getData();
                            String myStr = data.getStringExtra("MyData");
                            if(myStr.equals("ok")){
                                System.out.println("Status code okay");
                                finish();
                            }
                        }
                    }
                });
    }
    private void uiDistanceSet(double distance) {
        if (distance < 10) {
            distanceAway.setText("Less than 10 miles");
        } else {
            int value = (int) distance;
            distanceAway.setText(String.valueOf(value) + " miles away");
        }

    }

    private double getMiles(double meters) {
        return meters / 1609.344;
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            database.getReference("Distance/" + creatorId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    LocationConstr l = snapshot.getValue(LocationConstr.class);
                                    if (l != null) {
                                        Location loc2 = new Location("");
                                        loc2.setLatitude(l.latitude);
                                        loc2.setLongitude(l.longitude);
                                        double distance = getMiles(location.distanceTo(loc2));
                                        System.out.println(distance);
                                        uiDistanceSet(distance);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    }
                });

    }


    private void askLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                System.out.println("askLocationPermission: you should show an alert dialog...");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                System.out.println("Permission PRECISE Granted");
                getLastLocation();

            } else {
                //Permission not granted
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Permission COARSE Granted");
                    getLastLocation();
                } else {
                    System.out.println("Permission Not Granted");
                }


            }
        }
    }

    private void getEUInfo() {
        FirebaseUser authUser;
        DatabaseReference reference;


        authUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        authUserId = authUser.getUid();

        reference.child(authUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null) {
                    authUserName = userProfile.firstName;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SelectedGigFindActivity.this, "Something wrong happened!", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void getViewElements() {
        titleTextView = (TextView) findViewById(R.id.titleSelectedFindRequest);
        gigInfoTextView = (TextView) findViewById(R.id.gigInfoSelectedFindRequest);
        gigBanner = findViewById(R.id.imageViewSelectedFindRequest);
        nameTextView = (TextView) findViewById(R.id.nameSelectedFindRequest);
        profilePic = findViewById(R.id.profilePicSelectedFindRequest);
        distanceAway = findViewById(R.id.textView7LocationSelectedFindRequest);

        price1TextView = (TextView) findViewById(R.id.editTextLengthInfo1PriceSelectedFindRequest);
        duration1TextView = (TextView) findViewById(R.id.editTextLengthInfo1LengthSelectedFindRequest);
        time1TextView = (TextView) findViewById(R.id.textViewTime1SelectedFindRequest);
        toggleButtonGig1 = (ToggleButton) findViewById((R.id.toggleButtonGig1SelectedFindRequest));

        price2TextView = (TextView) findViewById(R.id.editTextLengthInfo2PriceSelectedFindRequest);
        duration2TextView = (TextView) findViewById(R.id.editTextLengthInfo2LengthSelectedFindRequest);
        time2TextView = (TextView) findViewById(R.id.textViewTime2SelectedFindRequest);
        toggleButtonGig2 = (ToggleButton) findViewById((R.id.toggleButtonGig2SelectedFindRequest));

        price3TextView = (TextView) findViewById(R.id.editTextLengthInfo3PriceSelectedFindRequest);
        duration3TextView = (TextView) findViewById(R.id.editTextLengthInfo3LengthSelectedFindRequest);
        time3TextView = (TextView) findViewById(R.id.textViewTime3SelectedFindRequest);
        toggleButtonGig3 = (ToggleButton) findViewById((R.id.toggleButtonGig3SelectedFindRequest));

        request = (Button) findViewById(R.id.btnRequestGigSelectedFindRequest);

        clMessage = findViewById(R.id.linearLayOutProfileSelectedFindRequest);
        ibChat = (ImageButton) findViewById(R.id.imageButton2ChatSelectedFindRequest);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        bb=findViewById(R.id.imageButtonSelectedGigFindRequest);
    }

    private void getUserId() {
        if (type.equals("Outside")) {
            ref = database.getReference("Gigs/Outside");
        } else {
            ref = database.getReference("Gigs/FindRequests");
        }

        ref.child(gigId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Gigs selectedGig = snapshot.getValue(Gigs.class);

                if (selectedGig != null) {
                    getUserNameUriProfile(selectedGig.userID);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    private void getUserNameUriProfile(String id) {
        ref = database.getReference("Users");
        ref.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null) {

                    userName = userProfile.firstName;
                    nameTextView.setText(userProfile.firstName);
                    if (userProfile.uriProfile.equals("Default")) {
                        Glide.with(SelectedGigFindActivity.this).load(R.drawable.ic_baseline_android_200).into(profilePic);
                    } else {
                        Glide.with(SelectedGigFindActivity.this)
                                .load(userProfile.uriProfile)
                                .into(profilePic);
                    }
                    uriCreator = userProfile.uriProfile;


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    private void getGigBody() {


        if (type.equals("Outside")) {
            ref = database.getReference("Gigs/Outside");
        } else {
            ref = database.getReference("Gigs/FindRequests");
        }
        ref.child(gigId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Gigs selectedGig = snapshot.getValue(Gigs.class);

                if (selectedGig != null) {
                    userId_seller = selectedGig.userID;
                    titleTextView.setText(selectedGig.title);
                    gigInfoTextView.setText(selectedGig.info);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    private void getGigOptions1() {

        if (type.equals("Outside")) {
            ref = database.getReference("Gigs/Outside");
        } else {
            ref = database.getReference("Gigs/FindRequests");
        }
        ref.child(gigId).child("option1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GigOptions selectedGig = snapshot.getValue(GigOptions.class);

                if (selectedGig != null) {
                    price1= selectedGig.price;
                    optionSelectedPrice = selectedGig.price;
                    optionSelected = "option1";
                    optionSelectedDuration = selectedGig.duration;
                    optionSelectedTime=selectedGig.time;
                    price1TextView.setText("$" + selectedGig.price);
                    //System.out.println(gig1Status);
                    duration1TextView.setText(selectedGig.duration);
                    time1TextView.setText(selectedGig.time);

                    if (selectedGig.activated.equals("true")) {
                        toggleButtonGig1.setText("$" + selectedGig.price);
                        toggleButtonGig1.setTextOn("$" + selectedGig.price);
                        toggleButtonGig1.setTextOff("$" + selectedGig.price);
                        optionSelectedStatus = "ACTIVE";
                    } else {
                        toggleButtonGig1.setText("INACTIVE");
                        toggleButtonGig1.setTextOn("INACTIVE");
                        toggleButtonGig1.setTextOff("INACTIVE");
                        optionSelectedStatus = "INACTIVE";
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    private void getGigOptions2() {
        if (type.equals("Outside")) {
            ref = database.getReference("Gigs/Outside");
        } else {
            ref = database.getReference("Gigs/FindRequests");
        }
        ref.child(gigId).child("option2").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GigOptions selectedGig = snapshot.getValue(GigOptions.class);

                if (selectedGig != null) {
                    price2= selectedGig.price;
                    price2TextView.setText("$" + selectedGig.price);
                    //System.out.println(gig1Status);
                    duration2TextView.setText(selectedGig.duration);
                    time2TextView.setText(selectedGig.time);

                    if (selectedGig.activated.equals("true")) {
                        toggleButtonGig2.setText("$" + selectedGig.price);
                        toggleButtonGig2.setTextOn("$" + selectedGig.price);
                        toggleButtonGig2.setTextOff("$" + selectedGig.price);
                    } else {
                        toggleButtonGig2.setText("INACTIVE");
                        toggleButtonGig2.setTextOn("INACTIVE");
                        toggleButtonGig2.setTextOff("INACTIVE");
                        toggleButtonGig2.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    private void getGigOptions3() {
        if (type.equals("Outside")) {
            ref = database.getReference("Gigs/Outside");
        } else {
            ref = database.getReference("Gigs/FindRequests");
        }
        ref.child(gigId).child("option3").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GigOptions selectedGig = snapshot.getValue(GigOptions.class);

                if (selectedGig != null) {
                    price3= selectedGig.price;
                    price3TextView.setText("$" + selectedGig.price);
                    //System.out.println(gig1Status);
                    duration3TextView.setText(selectedGig.duration);
                    time3TextView.setText(selectedGig.time);

                    if (selectedGig.activated.equals("true")) {
                        toggleButtonGig3.setText("$" + selectedGig.price);
                        toggleButtonGig3.setTextOn("$" + selectedGig.price);
                        toggleButtonGig3.setTextOff("$" + selectedGig.price);
                    } else {
                        toggleButtonGig3.setText("INACTIVE");
                        toggleButtonGig3.setTextOn("INACTIVE");
                        toggleButtonGig3.setTextOff("INACTIVE");
                        toggleButtonGig3.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    public void getUri() {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase;
        if (type.equals("Outside")) {
            mDatabase = database.getReference("Gigs/Outside");
        } else {
            mDatabase = database.getReference("Gigs/FindRequests");
        }


        mDatabase.child(gigId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Gigs selectedGig = snapshot.getValue(Gigs.class);
                //System.out.println(userGigs.uriBanner);
                if (selectedGig != null) {
                    imageUri = selectedGig.uriBanner;
                    if (imageUri.equals("Default")) {
                        Glide.with(SelectedGigFindActivity.this)
                                .load(R.drawable.ic_baseline_android_200)
                                .into(gigBanner);
                    } else {
                        Glide.with(SelectedGigFindActivity.this)
                                .load(selectedGig.uriBanner)
                                .into(gigBanner);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    private void removeToggleButtonShadows() {
        //remove shadows from toggle buttons
        toggleButtonGig1.setStateListAnimator(null);
        toggleButtonGig2.setStateListAnimator(null);
        toggleButtonGig3.setStateListAnimator(null);
        toggleButtonGig1.setChecked(true);

    }

    private void startActivity() {
        if(optionSelectedStatus.equals("INACTIVE")){
            Toast.makeText(SelectedGigFindActivity.this,"Status inactive",Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(SelectedGigFindActivity.this, SendInquiryActivity.class);
        intent.putExtra("GIG_EUID", authUserId);
        intent.putExtra("GIG_EUNAME", authUserName);
        intent.putExtra("GIG_URI", imageUri);
        intent.putExtra("GIG_TITLE", titleTextView.getText().toString());
        intent.putExtra("GIG_SNAME", nameTextView.getText().toString());
        intent.putExtra("GIG_SID", userId_seller);
        intent.putExtra("GIG_PRICE", optionSelectedPrice);
        intent.putExtra("GIG_OPTION",optionSelected);
        intent.putExtra("GIG_GIGID", gigId);
        intent.putExtra("GIG_URICREATOR", uriCreator);
        intent.putExtra("GIG_TYPE", type);
        intent.putExtra("GIG_DURATION",optionSelectedDuration);
        intent.putExtra("GIG_TIME",optionSelectedTime);
        intent.putExtra("GIG_STATUS",optionSelectedStatus);
        activityResultLaunch.launch(intent);}

    private void setListeners(){
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0,0);
            }
        });
        ibChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Message");
                if(creatorId.equals(authUserId)){
                    System.out.println("You created it");
                }
                else
                {

                    Intent intent = new Intent(view.getContext(), ChatGig.class);
                    //intent.putExtra("REQ_ID", textViewListIds.getText().toString());
                    intent.putExtra("THEIR_NAME", nameTextView.getText().toString());
                    intent.putExtra("THEIR_URI", uriCreator);

                    intent.putExtra("RECEIVER_ID", creatorId);

                    view.getContext().startActivity(intent);
                }

            }
        });

        clMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Message");
                if(creatorId.equals(authUserId)){
                    System.out.println("You created it");
                }
                else
                {

                    Intent intent = new Intent(view.getContext(), ChatGig.class);
                    //intent.putExtra("REQ_ID", textViewListIds.getText().toString());
                    //intent.putExtra("THEIR_NAME", textViewCreatorName.getText().toString());
                    //intent.putExtra("THEIR_URI", theirUri.getText().toString());
                    //intent.putExtra("GIG_CREATORID", textViewCreatorIds.getText().toString());
                    intent.putExtra("RECEIVER_ID", creatorId);
                    intent.putExtra("THEIR_NAME", nameTextView.getText().toString());
                    intent.putExtra("THEIR_URI", uriCreator);

                    view.getContext().startActivity(intent);
                }
            }
        });

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                startActivity();


            }
        });


        toggleButtonGig1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Is the toggle on?
                boolean on = ((ToggleButton) view).isChecked();

                if (on) {
                    View v = findViewById(R.id.gigPriceInfo1GigSelectedSearch);
                    v.setVisibility(View.VISIBLE);

                    toggleButtonGig2.setChecked(false);
                    toggleButtonGig3.setChecked(false);
                    v = findViewById(R.id.gigPriceInfo2GigSelectedSearch);
                    v.setVisibility(View.GONE);
                    v = findViewById(R.id.gigPriceInfo3GigSelectedSearch);
                    v.setVisibility(View.GONE);

                    optionSelected = "option1";
                    optionSelectedPrice=price1;
                    optionSelectedDuration=duration1TextView.getText().toString();
                    optionSelectedTime=time1TextView.getText().toString();
                    if(toggleButtonGig1.getText().toString().equals("INACTIVE")){
                        optionSelectedStatus="INACTIVE";
                    }else{
                        optionSelectedStatus="ACTIVE";
                    }

                } else {
                    toggleButtonGig1.setChecked(true);
                    //View v = findViewById(R.id.gigPriceInfo1);
                    //v.setVisibility(View.GONE);
                }



            }
        });


        toggleButtonGig2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Is the toggle on?
                boolean on = ((ToggleButton) view).isChecked();

                if (on) {
                    View v = findViewById(R.id.gigPriceInfo2GigSelectedSearch);
                    v.setVisibility(View.VISIBLE);

                    toggleButtonGig1.setChecked(false);
                    toggleButtonGig3.setChecked(false);
                    v = findViewById(R.id.gigPriceInfo1GigSelectedSearch);
                    v.setVisibility(View.GONE);
                    v = findViewById(R.id.gigPriceInfo3GigSelectedSearch);
                    v.setVisibility(View.GONE);
                    optionSelectedPrice=price2;
                    optionSelected = "option2";
                    optionSelectedDuration=duration2TextView.getText().toString();
                    optionSelectedTime=time2TextView.getText().toString();
                    if(toggleButtonGig2.getText().toString().equals("INACTIVE")){
                        optionSelectedStatus="INACTIVE";
                    }else{
                        optionSelectedStatus="ACTIVE";
                    }

                } else {
                    toggleButtonGig2.setChecked(true);
                    //View v = findViewById(R.id.gigPriceInfo2);
                    //v.setVisibility(View.GONE);
                }



            }
        });

        toggleButtonGig3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Is the toggle on?
                boolean on = ((ToggleButton) view).isChecked();

                if (on) {
                    View v = findViewById(R.id.gigPriceInfo3GigSelectedSearch);
                    v.setVisibility(View.VISIBLE);

                    toggleButtonGig1.setChecked(false);
                    toggleButtonGig2.setChecked(false);
                    v = findViewById(R.id.gigPriceInfo1GigSelectedSearch);
                    v.setVisibility(View.GONE);
                    v = findViewById(R.id.gigPriceInfo2GigSelectedSearch);
                    v.setVisibility(View.GONE);
                    optionSelectedPrice=price3;
                    optionSelected = "option3";
                    optionSelectedDuration=duration3TextView.getText().toString();
                    optionSelectedTime=time3TextView.getText().toString();
                    if(toggleButtonGig3.getText().toString().equals("INACTIVE")){
                        optionSelectedStatus="INACTIVE";
                    }else{
                        optionSelectedStatus="ACTIVE";
                    }

                } else {
                    toggleButtonGig3.setChecked(true);
                    //View v = findViewById(R.id.gigPriceInfo3);
                    //v.setVisibility(View.GONE);
                }



            }
        });


    }
}