package com.lyncseapp.lyncse.outside;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.lyncseapp.lyncse.GigOptions;
import com.lyncseapp.lyncse.R;
import com.lyncseapp.lyncse.User;
import com.lyncseapp.lyncse.constructors.GigCreate;
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

import java.util.Map;

public class CreateOutsideActivity extends AppCompatActivity {

    private String uGigID;
    private String currencyNum;
    private EditText gigPrice1;
    private EditText gigLength1;
    private Button saveGig;
    private ToggleButton tglBtnO1;
    private ToggleButton tglBtnO2;
    private ToggleButton tglBtnO3;
    private ImageView profilePic;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference mDatabase;
    private String userId;
    private String userName;
    private FirebaseUser user;
    private TextView time1;
    private TextView time2;
    private TextView time3;
    private int timeToggleNumber;
    private int timeToggleNumber2;
    private int timeToggleNumber3;
    private ImageView gigBanner;
    ActivityResultLauncher<String> mGetContent;
    public Uri imageUri;
    private boolean loadBannerInitial;
    private boolean initialCreateLoad;
    private boolean initialCreateLoadEventsForGig;

    private TextView nameTextView;


    DatabaseReference ref;
    FirebaseDatabase database;

    private String imgUriString;
    private ImageButton bb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_outside);

        timeToggleNumber=0;
        timeToggleNumber2=0;
        timeToggleNumber3=0;


        user = FirebaseAuth.getInstance().getCurrentUser();
        userId= user.getUid();
        userName= user.getDisplayName();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();



        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Gigs/Outside");




        getUser();
        getElements();
        try {
            Glide.with(CreateOutsideActivity.this)
                    .load(R.drawable.ic_baseline_android_200)
                    .into(gigBanner);

        } catch (Exception e) {
            e.printStackTrace();
        }

        setListeners();
        removeShadowsToggleBtn();


    }


    private void getElements() {
        nameTextView = (TextView) findViewById(R.id.nameCreateOutside);
        saveGig = (Button) findViewById(R.id.btnSaveGigCreateOutside);
        time1= (TextView) findViewById(R.id.textViewTime1CreateOutside);
        time2= (TextView) findViewById(R.id.textViewTime2CreateOutside);
        time3= (TextView) findViewById(R.id.textViewTime3CreateOutside);
        tglBtnO1= (ToggleButton) findViewById(R.id.toggleButtonGig1CreateOutside);
        tglBtnO2= (ToggleButton) findViewById(R.id.toggleButtonGig2CreateOutside);
        tglBtnO3= (ToggleButton) findViewById(R.id.toggleButtonGig3CreateOutside);
        gigPrice1 = (EditText) findViewById(R.id.editTextLengthInfo1PriceCreateOutside);
        gigLength1 = (EditText) findViewById(R.id.editTextLengthInfo1LengthCreateOutside);
        profilePic = findViewById(R.id.profilePicCreateOutside);
        gigBanner = findViewById(R.id.imageViewCreateOutside);
        bb=findViewById(R.id.imageButtonCreateOutside);
    }
    private void getUser(){
        // GET USER DATABASE INFO
        // Get a reference to our posts
        ref = database.getReference("Users");

        ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile !=null){
                    nameTextView.setText(userProfile.firstName);
                    if (userProfile.uriProfile.equals("Default")) {
                        Glide.with(CreateOutsideActivity.this)
                                .load(R.drawable.ic_baseline_android_200)
                                .into(profilePic);
                    } else {
                        Glide.with(CreateOutsideActivity.this)
                                .load(userProfile.uriProfile)
                                .into(profilePic);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CreateOutsideActivity.this,"Something wrong happened!", Toast.LENGTH_LONG).show();
                Glide.with(CreateOutsideActivity.this)
                        .load(R.drawable.ic_baseline_android_200)
                        .into(profilePic);
            }
        });
        //-----------------------------------------------------------------------------------
    }
    private void removeShadowsToggleBtn(){
        tglBtnO1.setStateListAnimator(null);
        tglBtnO2.setStateListAnimator(null);
        tglBtnO3.setStateListAnimator(null);
        tglBtnO1.setChecked(true);
    }
    private void setListeners(){

        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0,0);
            }
        });
        mGetContent= registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                imageUri=result;
                gigBanner.setImageURI(result);

            }
        });

        tglBtnO1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Is the toggle on?
                boolean on = ((ToggleButton) view).isChecked();

                if (on) {
                    View v = findViewById(R.id.gigPriceInfo1CreateOutside);
                    v.setVisibility(View.VISIBLE);

                    tglBtnO2.setChecked(false);
                    tglBtnO3.setChecked(false);
                    v = findViewById(R.id.gigPriceInfo2CreateOutside);
                    v.setVisibility(View.GONE);
                    v = findViewById(R.id.gigPriceInfo3CreateOutside);
                    v.setVisibility(View.GONE);

                } else {
                    tglBtnO1.setChecked(true);
                    //View v = findViewById(R.id.gigPriceInfo1);
                    //v.setVisibility(View.GONE);
                }



            }
        });


        tglBtnO2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Is the toggle on?
                boolean on = ((ToggleButton) view).isChecked();

                if (on) {
                    View v = findViewById(R.id.gigPriceInfo2CreateOutside);
                    v.setVisibility(View.VISIBLE);

                    tglBtnO1.setChecked(false);
                    tglBtnO3.setChecked(false);
                    v = findViewById(R.id.gigPriceInfo1CreateOutside);
                    v.setVisibility(View.GONE);
                    v = findViewById(R.id.gigPriceInfo3CreateOutside);
                    v.setVisibility(View.GONE);

                } else {
                    tglBtnO2.setChecked(true);
                    //View v = findViewById(R.id.gigPriceInfo2);
                    //v.setVisibility(View.GONE);
                }



            }
        });

        tglBtnO3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Is the toggle on?
                boolean on = ((ToggleButton) view).isChecked();

                if (on) {
                    View v = findViewById(R.id.gigPriceInfo3CreateOutside);
                    v.setVisibility(View.VISIBLE);

                    tglBtnO1.setChecked(false);
                    tglBtnO2.setChecked(false);
                    v = findViewById(R.id.gigPriceInfo1CreateOutside);
                    v.setVisibility(View.GONE);
                    v = findViewById(R.id.gigPriceInfo2CreateOutside);
                    v.setVisibility(View.GONE);

                } else {
                    tglBtnO3.setChecked(true);
                    //View v = findViewById(R.id.gigPriceInfo3);
                    //v.setVisibility(View.GONE);
                }



            }
        });

        //---------------------------------------------click listener textview------------------


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




        //

        //Get click events

        saveGig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushKeyUser();

            }
        });


        gigBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });

        gigPrice1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = charSequence.toString();
                if (text.contains(".") && text.substring(text.indexOf(".") + 1).length() > 2) {
                    gigPrice1.setText(text.substring(0, text.length() - 1));
                    gigPrice1.setSelection(gigPrice1.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //currencyNum = gigPrice1.toString();
                //gigPrice1.setText(form.format(currencyNum));
            }
        });

        gigLength1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = charSequence.toString();



                if(text.length()>2){
                    gigLength1.setText(text.substring(0, text.length() - 1));
                    gigLength1.setSelection(gigLength1.getText().length());
                }
                if (text.contains(".")) {
                    gigLength1.setText(text.substring(0, text.length() - 1));
                    gigLength1.setSelection(gigLength1.getText().length());
                }


                if(text.startsWith("0")) {
                    gigLength1.setText(null);
                    gigLength1.setSelection(gigLength1.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void choosePicture() {
        //Log.d("debugTag", "Choose Pic");
        mGetContent.launch("image/*");


    }
    private void pushKeyUser(){
        mDatabase = database.getReference("Gigs/Outside/");
        if (uGigID == null) {
            uGigID = mDatabase.push().getKey();

        }
        mDatabase = mDatabase.child(uGigID);

        uploadBanner();
    }
    private void uploadBanner() {

        if(imageUri==null){

                saveUriDatabaseDefault();


        }
        else{
            //Log.d("debugTag", "In If Upload");
            StorageReference reference = storage.getReference().child("users/" + userId + "/images/banner/" + uGigID);
            //we are creating a reference to store the image in firebase storage.
            //it will be stored inside images folder in firebase storage
            //you can use user auth id instead of uuid if your app has  firebase auth

            //now using the below code we will store the file


            reference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        //Image uploaded
                        Toast.makeText(CreateOutsideActivity.this, "Image uploaded", Toast.LENGTH_LONG).show();
                        Log.d("debugTag", "Success");
                        saveUriDatabase();
                        imageUri=null;
                    } else {
                        Log.d("debugTag", "Fail");
                        //Failed
                        Toast.makeText(CreateOutsideActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });


        }
    }


    private void saveUriDatabaseDefault(){
        mDatabase = database.getReference("Gigs/Outside/");
        mDatabase = mDatabase.child(uGigID);
        System.out.println("savedefault");
        String x= "https://firebasestorage.googleapis.com/v0/b/lyncse-33410.appspot.com/o/default%2FbannerGigs%2FOutside%2FdefaultBanner_O.jpg?alt=media&token=e1a8f7d5-9b74-4127-8265-2c8a95565c9b";
        imgUriString="Default";
        saveGig();
    }
    private void saveUriDatabase(){
        mDatabase = database.getReference("Gigs/Outside/");
        mDatabase = mDatabase.child(uGigID);

        StorageReference storageRef1 =
                FirebaseStorage.getInstance().getReference();
        storageRef1.child("users/"+userId+"/images/banner/"+uGigID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                System.out.println(uri.toString());
                imgUriString=uri.toString();
                Glide.with(CreateOutsideActivity.this)
                        .load(uri.toString())
                        .into(gigBanner);

                saveGig();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                System.out.println("failed");
                Glide.with(CreateOutsideActivity.this)
                        .load(R.drawable.ic_baseline_android_200)
                        .into(gigBanner);
                String x= "https://firebasestorage.googleapis.com/v0/b/lyncse-33410.appspot.com/o/default%2FbannerGigs%2FOutside%2FdefaultBanner_O.jpg?alt=media&token=e1a8f7d5-9b74-4127-8265-2c8a95565c9b";
                imgUriString=x;
                saveGig();

            }
        });
    }



    private void saveGig(){
        EditText mEdit;
        TextView tView;
        String price;
        String duration;
        String title;
        String esearch;
        String info;
        String userID;
        String uriBanner;
        Map createdOn;

        mEdit = (EditText) findViewById(R.id.titleCreateOutside);
        title=mEdit.getText().toString();
        esearch=mEdit.getText().toString();
        userID=userId;
        mEdit = (EditText) findViewById(R.id.gigInfoCreateOutside);
        info=mEdit.getText().toString();
        createdOn=ServerValue.TIMESTAMP;
        uriBanner=imgUriString;

        //eventListenerGigUID();


        mEdit   = (EditText)findViewById(R.id.editTextLengthInfo1PriceCreateOutside);
        price=mEdit.getText().toString();
        mEdit   = (EditText)findViewById(R.id.editTextLengthInfo1LengthCreateOutside);
        duration=mEdit.getText().toString();

        if(price.equals("0") || price.equals(null) || price.equals("")  ){
            System.out.println("price 0 or null");
        }else if(duration.equals("0") || duration.equals(null) || duration.equals("") ) {
            System.out.println("duration 0 or null");
        }else if(title.equals("")){
            System.out.println("title null");
        }else if(info.equals("")){
            System.out.println("info null");
        }
        else

        {


            String price1;
            mEdit = (EditText) findViewById(R.id.editTextLengthInfo1PriceCreateOutside);
            price1 = mEdit.getText().toString();
            String duration1;
            mEdit = (EditText) findViewById(R.id.editTextLengthInfo1LengthCreateOutside);
            duration1 = mEdit.getText().toString();
            tView = (TextView) findViewById(R.id.textViewTime1CreateOutside);
            String time1;
            time1=tView.getText().toString();
            String activated1;




            if (price1.equals("0") || price1.equals("") || duration1.equals("0") || duration1.equals("")) {
                activated1="false";
            } else {
                activated1="true";
            }


            String price2;
            mEdit = (EditText) findViewById(R.id.editTextLengthInfo2PriceCreateOutside);
            price2 = mEdit.getText().toString();
            String duration2;
            mEdit = (EditText) findViewById(R.id.editTextLengthInfo2LengthCreateOutside);
            duration2 = mEdit.getText().toString();
            tView = (TextView) findViewById(R.id.textViewTime2CreateOutside);
            String time2;
            time2=tView.getText().toString();
            String activated2;
            if (price2.equals("0") || price2.equals("") || duration2.equals("0") || duration2.equals("")) {
                activated2="false";
            } else {

                activated2="true";
            }


            String price3;
            mEdit = (EditText) findViewById(R.id.editTextLengthInfo3PriceCreateOutside);
            price3 = mEdit.getText().toString();
            String duration3;
            mEdit = (EditText) findViewById(R.id.editTextLengthInfo3LengthCreateOutside);
            duration3 = mEdit.getText().toString();
            tView = (TextView) findViewById(R.id.textViewTime3CreateOutside);
            String time3;
            time3=tView.getText().toString();
            String activated3;
            if (price3.equals("0") || price3.equals("") || duration3.equals("0") || duration3.equals("")) {
                activated3="false";
            } else {

                activated3="true";
            }




            GigCreate gig = new GigCreate(title,esearch,info,userID,uriBanner,createdOn);
            mDatabase.setValue(gig).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    System.out.println("Finished");
                    GigOptions g1 = new GigOptions(activated1,duration1,price1,time1);
                    GigOptions g2 = new GigOptions(activated2,duration2,price2,time2);
                    GigOptions g3 = new GigOptions(activated3,duration3,price3,time3);
                    mDatabase.child("option1").setValue(g1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mDatabase.child("option2").setValue(g2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mDatabase.child("option3").setValue(g3).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(CreateOutsideActivity.this, "Post Created.", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });

        }

    }


}