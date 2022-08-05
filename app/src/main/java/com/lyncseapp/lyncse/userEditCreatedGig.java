package com.lyncseapp.lyncse;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class userEditCreatedGig extends AppCompatActivity {

    private String gigId;
    private DatabaseReference ref;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private String userId;
    private FirebaseUser user;

    private TextView titleTextView;
    private TextView gigInfoTextView;

    private ImageView gigBanner;

    private TextView nameTextView;

    private ImageView profilePic;

    private TextView price1TextView;
    private TextView duration1TextView;
    private TextView time1TextView;
    private ToggleButton toggleButtonGig1;

    private TextView price2TextView;
    private TextView duration2TextView;
    private TextView time2TextView;
    private ToggleButton toggleButtonGig2;

    private TextView price3TextView;
    private TextView duration3TextView;
    private TextView time3TextView;
    private ToggleButton toggleButtonGig3;

    private Button saveGig;
    private Button deleteGig;

    private Uri imageUri;
    ActivityResultLauncher<String> mGetContent;



    private TextView time1;
    private TextView time2;
    private TextView time3;
    private int timeToggleNumber;
    private int timeToggleNumber2;
    private int timeToggleNumber3;

    private String type;
    private ImageButton bb;
    FrameLayout flLoad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_created_gig);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userId= user.getUid();
        gigId = getIntent().getStringExtra("GIG_ID");
        type = getIntent().getStringExtra("GIG_TYPE");
        System.out.println(gigId);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        imageUri=null;

        getViewElements();
        getUserName();

        getGigBody();
        getUri();
        getGigOptions1();
        getGigOptions2();
        getGigOptions3();
        removeToggleButtonShadows();
        setToggleButtonListeners();


        //for banner image
        mGetContent= registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                imageUri=result;
                gigBanner.setImageURI(result);

            }
        });

        //

        //Get click events
        gigBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });

        saveGig = (Button) findViewById(R.id.btnSaveGigUserEditCreatedGig);
        saveGig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                saveGig();



            }
        });


        //---------------------
setListeners();

    }


private void setListeners(){
    bb.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
            overridePendingTransition(0,0);
        }
    });
    deleteGig.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(type.equals("Virtual")) {
                database.getReference("Gigs/Virtual/"+gigId).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(userEditCreatedGig.this,"Post deleted Successful",Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });
            }else if(type.equals("Find")){
                database.getReference("Gigs/FindRequests/"+gigId).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(userEditCreatedGig.this,"Post deleted Successful",Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });
            }else
            {
                database.getReference("Gigs/Outside/"+gigId).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(userEditCreatedGig.this,"Post deleted Successful",Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });
            }
        }
    });
}
    private void getViewElements(){
        titleTextView=(TextView) findViewById(R.id.titleUserEditCreatedGig);
        gigInfoTextView=(TextView) findViewById(R.id.gigInfoUserEditCreatedGig);
        gigBanner=findViewById(R.id.imageViewUserEditCreatedGig);
        nameTextView=(TextView) findViewById(R.id.nameUserEditCreatedGig);
        profilePic=findViewById(R.id.profilePicUserEditCreatedGig);

        price1TextView = (TextView) findViewById(R.id.editTextLengthInfo1PriceUserEditCreatedGig);
        duration1TextView = (TextView) findViewById(R.id.editTextLengthInfo1LengthUserEditCreatedGig);
        time1TextView = (TextView) findViewById(R.id.textViewTime1UserEditCreatedGig);
        toggleButtonGig1 = (ToggleButton) findViewById((R.id.toggleButtonGig1UserEditCreatedGig));

        price2TextView = (TextView) findViewById(R.id.editTextLengthInfo2PriceUserEditCreatedGig);
        duration2TextView = (TextView) findViewById(R.id.editTextLengthInfo2LengthUserEditCreatedGig);
        time2TextView = (TextView) findViewById(R.id.textViewTime2UserEditCreatedGig);
        toggleButtonGig2 = (ToggleButton) findViewById((R.id.toggleButtonGig2UserEditCreatedGig));

        price3TextView = (TextView) findViewById(R.id.editTextLengthInfo3PriceUserEditCreatedGig);
        duration3TextView = (TextView) findViewById(R.id.editTextLengthInfo3LengthUserEditCreatedGig);
        time3TextView = (TextView) findViewById(R.id.textViewTime3UserEditCreatedGig);
        toggleButtonGig3 = (ToggleButton) findViewById((R.id.toggleButtonGig3UserEditCreatedGig));
        bb=findViewById(R.id.imageButtonEditCreatedGig);
        flLoad=findViewById(R.id.flLoad_EditCreatedGig);
        deleteGig=findViewById(R.id.btnDeleteGigUserEditCreatedGig);
    }

    private void getGigBody(){


        if(type.equals("Virtual")) {
            ref = database.getReference("Gigs/Virtual/"+gigId);
        }else if(type.equals("Find")){
            ref = database.getReference("Gigs/FindRequests/"+gigId);
        }else
        {
            ref = database.getReference("Gigs/Outside/"+gigId);
        }
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Gigs selectedGig = snapshot.getValue(Gigs.class);

                if(selectedGig !=null){

                    titleTextView.setText(selectedGig.title);
                    gigInfoTextView.setText(selectedGig.info);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }





    public void getUri(){

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase;
        if(type.equals("Virtual")) {
            mDatabase = database.getReference("Gigs/Virtual/"+gigId);
        }else if(type.equals("Find")){
            mDatabase = database.getReference("Gigs/FindRequests/"+gigId);
        }else{
            mDatabase = database.getReference("Gigs/Outside/"+gigId);
        }

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Gigs selectedGig= snapshot.getValue(Gigs.class);
                //System.out.println(userGigs.uriBanner);
                if (selectedGig != null) {
                    if(selectedGig.uriBanner.equals("Default")){
                        Glide.with(userEditCreatedGig.this)
                                .load(R.drawable.ic_baseline_android_200)
                                .into(gigBanner);
                    }
                    else {
                        Glide.with(userEditCreatedGig.this)
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



    private void getUserName(){

        ref = database.getReference("Users");

        ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile !=null){
                    String fullName = userProfile.firstName;
                    nameTextView.setText(fullName);
                    if (userProfile.uriProfile.equals("Default")) {
                        Glide.with(userEditCreatedGig.this)
                                .load(R.drawable.ic_baseline_android_200)
                                .into(profilePic);
                    } else {
                        Glide.with(userEditCreatedGig.this)
                                .load(userProfile.uriProfile)
                                .into(profilePic);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(userEditCreatedGig.this,"Something wrong happened!", Toast.LENGTH_LONG).show();
                Glide.with(userEditCreatedGig.this)
                        .load(R.drawable.ic_baseline_android_200)
                        .into(profilePic);
            }
        });
    }




    private void getGigOptions1(){


        if(type.equals("Virtual")) {
            ref = database.getReference("Gigs/Virtual/"+gigId);
        }else if(type.equals("Find")){
            ref = database.getReference("Gigs/FindRequests/"+gigId);
        }else{
            ref = database.getReference("Gigs/Outside/"+gigId);
        }
        ref.child("option1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GigOptions selectedGig = snapshot.getValue(GigOptions.class);

                if(selectedGig !=null){

                    price1TextView.setText(selectedGig.price);
                    //System.out.println(gig1Status);
                    duration1TextView.setText(selectedGig.duration);
                    time1TextView.setText(selectedGig.time);

                    if (selectedGig.activated.equals("true")) {
                        toggleButtonGig1.setText("$" + selectedGig.price);
                        toggleButtonGig1.setTextOn("$" + selectedGig.price);
                        toggleButtonGig1.setTextOff("$" + selectedGig.price);
                    } else {
                        toggleButtonGig1.setText("INACTIVE");
                        toggleButtonGig1.setTextOn("INACTIVE");
                        toggleButtonGig1.setTextOff("INACTIVE");
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    private void getGigOptions2(){
        if(type.equals("Virtual")) {
            ref = database.getReference("Gigs/Virtual/"+gigId);
        }else if(type.equals("Find")){
            ref = database.getReference("Gigs/FindRequests/"+gigId);
        }else{
            ref = database.getReference("Gigs/Outside/"+gigId);
        }
        ref.child("option2").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GigOptions selectedGig = snapshot.getValue(GigOptions.class);

                if(selectedGig !=null){

                    price2TextView.setText(selectedGig.price);
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
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    private void getGigOptions3(){

        if(type.equals("Virtual")) {
            ref = database.getReference("Gigs/Virtual/"+gigId);
        }else if(type.equals("Find")){
            ref = database.getReference("Gigs/FindRequests/"+gigId);
        }else{
            ref = database.getReference("Gigs/Outside/"+gigId);
        }
        ref.child("option3").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GigOptions selectedGig = snapshot.getValue(GigOptions.class);

                if(selectedGig !=null){

                    price3TextView.setText(selectedGig.price);
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
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    private void removeToggleButtonShadows(){
        //remove shadows from toggle buttons
        toggleButtonGig1.setStateListAnimator(null);
        toggleButtonGig2.setStateListAnimator(null);
        toggleButtonGig3.setStateListAnimator(null);
        toggleButtonGig1.setChecked(true);

    }

    private void setToggleButtonListeners(){
        //---------------------------------------------click listener textview------------------
        timeToggleNumber=0;
        timeToggleNumber2=0;
        timeToggleNumber3=0;
        time1= (TextView) findViewById(R.id.textViewTime1UserEditCreatedGig);
        time1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timeToggleNumber==0) {
                    time1.setText("Hours");
                    timeToggleNumber=1;
                }else
                {
                    time1.setText("Minutes");
                    timeToggleNumber=0;
                }
            }
        });


        time2= (TextView) findViewById(R.id.textViewTime2UserEditCreatedGig);
        time2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timeToggleNumber2==0) {
                    time2.setText("Hours");
                    timeToggleNumber2=1;
                }else
                {
                    time2.setText("Minutes");
                    timeToggleNumber2=0;
                }
            }
        });


        time3= (TextView) findViewById(R.id.textViewTime3UserEditCreatedGig);
        time3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timeToggleNumber3==0) {
                    time3.setText("Hours");
                    timeToggleNumber3=1;
                }else
                {
                    time3.setText("Minutes");
                    timeToggleNumber3=0;
                }
            }
        });




        //---------------------------------------------------------------


        toggleButtonGig1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Is the toggle on?
                boolean on = ((ToggleButton) view).isChecked();

                if (on) {
                    View v = findViewById(R.id.gigPriceInfo1UserEditCreatedGig);
                    v.setVisibility(View.VISIBLE);

                    toggleButtonGig2.setChecked(false);
                    toggleButtonGig3.setChecked(false);
                    v = findViewById(R.id.gigPriceInfo2UserEditCreatedGig);
                    v.setVisibility(View.GONE);
                    v = findViewById(R.id.gigPriceInfo3UserEditCreatedGig);
                    v.setVisibility(View.GONE);

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
                    View v = findViewById(R.id.gigPriceInfo2UserEditCreatedGig);
                    v.setVisibility(View.VISIBLE);

                    toggleButtonGig1.setChecked(false);
                    toggleButtonGig3.setChecked(false);
                    v = findViewById(R.id.gigPriceInfo1UserEditCreatedGig);
                    v.setVisibility(View.GONE);
                    v = findViewById(R.id.gigPriceInfo3UserEditCreatedGig);
                    v.setVisibility(View.GONE);

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
                    View v = findViewById(R.id.gigPriceInfo3UserEditCreatedGig);
                    v.setVisibility(View.VISIBLE);

                    toggleButtonGig1.setChecked(false);
                    toggleButtonGig2.setChecked(false);
                    v = findViewById(R.id.gigPriceInfo1UserEditCreatedGig);
                    v.setVisibility(View.GONE);
                    v = findViewById(R.id.gigPriceInfo2UserEditCreatedGig);
                    v.setVisibility(View.GONE);

                } else {
                    toggleButtonGig3.setChecked(true);
                    //View v = findViewById(R.id.gigPriceInfo3);
                    //v.setVisibility(View.GONE);
                }



            }
        });


    }

    private void saveGig() {
        flLoad.setVisibility(View.VISIBLE);
        System.out.println("Saving");
        String price;
        String duration;

        if(type.equals("Virtual")) {
            ref = database.getReference("Gigs/Virtual/"+gigId);
        }else if(type.equals("Find")){
            ref = database.getReference("Gigs/FindRequests/"+gigId);
        }else{
            ref = database.getReference("Gigs/Outside/"+gigId);
        }


        ref.child("esearch").child("title").setValue(titleTextView.getText().toString());
        ref.child("title").setValue(titleTextView.getText().toString());
        ref.child("info").setValue(gigInfoTextView.getText().toString());
        ref.child("updatedOn").setValue(ServerValue.TIMESTAMP);



        price = price1TextView.getText().toString();
        duration = duration1TextView.getText().toString();
        ref.child("option1").child("price").setValue(price);
        ref.child("option1").child("duration").setValue(duration);
        ref.child("option1").child("time").setValue(time1TextView.getText().toString());

        if (price.equals("0") || price.equals("") || duration.equals("0") || duration.equals("")) {
            ref.child("option1").child("activated").setValue("false");
        } else {

            ref.child("option1").child("activated").setValue("true");
        }



        price = price2TextView.getText().toString();
        duration = duration2TextView.getText().toString();
        ref.child("option2").child("price").setValue(price);
        ref.child("option2").child("duration").setValue(duration);
        ref.child("option2").child("time").setValue(time2TextView.getText().toString());

        if (price.equals("0") || price.equals("") || duration.equals("0") || duration.equals("")) {
            ref.child("option2").child("activated").setValue("false");
        } else {

            ref.child("option2").child("activated").setValue("true");
        }


        price = price3TextView.getText().toString();
        duration = duration3TextView.getText().toString();
        ref.child("option3").child("price").setValue(price);
        ref.child("option3").child("duration").setValue(duration);
        ref.child("option3").child("time").setValue(time3TextView.getText().toString());

        if (price.equals("0") || price.equals("") || duration.equals("0") || duration.equals("")) {
            ref.child("option3").child("activated").setValue("false");
        } else {

            ref.child("option3").child("activated").setValue("true");
        }

        uploadBanner();
        getGigOptions1();
        getGigOptions2();
        getGigOptions3();
    }


    private void uploadBanner() {

        if (imageUri != null) {
            //Log.d("debugTag", "In If Upload");
            StorageReference reference = storage.getReference().child("users/" + userId + "/images/banner/" + gigId);
            //we are creating a reference to store the image in firebase storage.
            //it will be stored inside images folder in firebase storage
            //you can use user auth id instead of uuid if your app has  firebase auth

            //now using the below code we will store the file


            reference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        //Image uploaded
                        Toast.makeText(userEditCreatedGig.this, "Image uploaded", Toast.LENGTH_LONG).show();
                        Log.d("debugTag", "Success");
                        saveUriDatabase();
                    } else {
                        flLoad.setVisibility(View.GONE);
                        Log.d("debugTag", "Fail");
                        //Failed
                        Toast.makeText(userEditCreatedGig.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

            imageUri=null;// To make the whole thing not launch if no image is selected again
        }else{
            flLoad.setVisibility(View.GONE);
        }
    }

    private void saveUriDatabase(){


        if(type.equals("Virtual")) {
            ref = database.getReference("Gigs/Virtual/"+gigId);
        }else if(type.equals("Find")){
            ref = database.getReference("Gigs/FindRequests/"+gigId);
        }else{
            ref = database.getReference("Gigs/Outside/"+gigId);
        }

        StorageReference storageRef1 =
                FirebaseStorage.getInstance().getReference();
        storageRef1.child("users/"+userId+"/images/banner/"+gigId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                flLoad.setVisibility(View.GONE);
                // Got the download URL for 'users/me/profile.png'
                System.out.println(uri.toString());
                ref.child("uriBanner").setValue(uri.toString());
                Glide.with(userEditCreatedGig.this)
                        .load(uri.toString())
                        .into(gigBanner);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                flLoad.setVisibility(View.GONE);
                // Handle any errors
                System.out.println("failed");
                Glide.with(userEditCreatedGig.this)
                        .load(R.drawable.ic_baseline_android_200)
                        .into(gigBanner);
            }
        });
    }

    private void choosePicture() {
        //Log.d("debugTag", "Choose Pic");
        mGetContent.launch("image/*");
    }

    }