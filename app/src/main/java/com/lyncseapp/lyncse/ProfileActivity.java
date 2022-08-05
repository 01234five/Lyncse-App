package com.lyncseapp.lyncse;

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
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lyncseapp.lyncse.policies.InfoActivity;
import com.lyncseapp.lyncse.search.ActivitySearch;
import com.lyncseapp.lyncse.stripe.StripeWebView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stripe.android.paymentsheet.PaymentSheet;

import java.util.HashMap;
import java.util.Map;



public class ProfileActivity extends AppCompatActivity {

    private Button logout;
    private Button stripe;
    private Button gigs;
    private Button search;
    private Button bTest;
    private FirebaseUser user;
    private DatabaseReference reference;

    private String userId;


    private ImageView profilePic;
    public Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private BottomNavigationView bottomNavigationView;

    ActivityResultLauncher<String> mGetContent;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private int LOCATION_REQUEST_CODE = 1001;
    FrameLayout flLoad;

    private PaymentSheet paymentSheet;
    String IntentClientSecret;
    private boolean allowClicks;
    private TextView fullNameTextView;
    private String welcome;
    private LinearLayout llMain;
    private ImageButton bb;
    private OnCompleteListener mCListener;
    private ImageButton ibInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getElements();
        flLoad.setVisibility(View.VISIBLE);
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userId= user.getUid();
        allowClicks=true;
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ProfileActivity.this);
        mGetContent= registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                imageUri=result;
                profilePic.setImageURI(result);
                uploadPicture();
            }
        });
        getToken();
        getUser();
        bottomNav();
    }
    private void getToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            //Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            System.out.println("failed to get token");
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        System.out.println(token + " token");
                        FirebaseDatabase.getInstance().getReference("UsersToken/"+userId+"/token").setValue(token).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    System.out.println("Token retrieved and saved");
                                }
                            }
                        });

                        // Log and toast

                        //Log.d(TAG, msg);
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void setListeners(){
        stripe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(allowClicks==true) {
                    allowClicks=false;
                    flLoad.setVisibility(View.VISIBLE);
                    stripeAccount();
                    System.out.println("Clicked");
                }
            }
        });
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("EXIT", true);
                startActivity(intent);
            }
        });
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0,0);
            }
        });

        ibInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, InfoActivity.class);
                //intent.putExtra("EXIT", true);
                startActivity(intent);
            }
        });
    }
    private void getElements(){
        ibInfo= findViewById(R.id.imageButtonInfo);
        profilePic = findViewById(R.id.profilePic);
        flLoad = findViewById(R.id.flLoad_Profile);
        logout = (Button) findViewById(R.id.signOut);
        stripe=findViewById(R.id.button2);
        fullNameTextView = (TextView) findViewById(R.id.fullName);
        llMain= findViewById(R.id.linearLayoutProfile);
        bb=findViewById(R.id.imageButtonProfileActivity);
        bTest= findViewById(R.id.buttonTestProfile);
        bTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testDatabase();
            }
        });
    }
    private void testDatabase(){
        FirebaseDatabase.getInstance().getReference("Users/37PSFKRiaIcYvK8wNWcDNXLzTew2/age").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error);
            }
        });
        FirebaseDatabase.getInstance().getReference("Requests/-N0n8W4K2-1rdobBIp5s/uStatus").setValue("Processing1").addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failed to write");
            }
        });
    }
    private void getUser(){
        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile !=null){
                    String fullName = userProfile.firstName;
                    welcome="Nice to have you back, "+fullName;
                    getUriProfile();
                    //fullNameTextView.setText();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this,"Something wrong happened!", Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        bottomNavigationView.setSelectedItemId(R.id.homeBottomBar);

    }

    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        }else  {
            askLocationPermission();
        }


    }



    private void stripeAccount(){

        mCListener= new OnCompleteListener<HttpsCallableResult>() {
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                flLoad.setVisibility(View.GONE);
                allowClicks = true;
                System.out.println("done");
                if (task.isSuccessful()) {
                    if (task.getResult().getData().toString().equals("noActionNeeded")) {
                        System.out.println("NoActionNeeded");
                        Intent intent = new Intent(ProfileActivity.this, StripeWebView.class);
                        intent.putExtra("STRIPE_URL", "NONE");
                        startActivity(intent);

                    } else if (task.getResult().getData().toString().equals("createdAccount")) {
                        System.out.println("createdAccount");
                        Intent intent = new Intent(ProfileActivity.this, StripeWebView.class);
                        intent.putExtra("STRIPE_URL", "NONE");
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(ProfileActivity.this, StripeWebView.class);
                        intent.putExtra("STRIPE_URL", task.getResult().getData().toString());
                        startActivity(intent);
                        //Toast.makeText(getApplicationContext(), "Task is NOT Successful", Toast.LENGTH_LONG).show();
                        //System.out.println(task.getResult().getData().toString());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Task is NOT Successful", Toast.LENGTH_LONG).show();
                }


            }
        };
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("text", "123");
        data.put("push", true);

        FirebaseFunctions.getInstance("us-central1") // Optional region: .getInstance("europe-west1")
                .getHttpsCallable( "createStripeConnectedAccount")
                .call().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e);
            }
        }).addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
            @Override
            public void onSuccess(HttpsCallableResult httpsCallableResult) {
                System.out.println("Success");
            }
        }).addOnCompleteListener(mCListener);
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
                            System.out.println(location.getLatitude());
                            System.out.println(location.getLongitude());
                            HashMap<String, Object> loc =
                                    new HashMap<String, Object>();

                            double lat=location.getLatitude();
                            double lon=location.getLongitude();
                            // add elements dynamically
                            loc.put("latitude", lat);
                            loc.put("longitude", lon);
                            FirebaseDatabase.getInstance().getReference("Distance/"+userId).setValue(loc);
                        }
                    }
                });

    }

    private void askLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                System.out.println("askLocationPermission: you should show an alert dialog...");
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
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
                }else {
                    System.out.println("Permission Not Granted");
                }




            }
        }
    }




    private void bottomNav(){
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.homeBottomBar);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.searchBottomBar:
                        Intent intent = new Intent(ProfileActivity.this, ActivitySearch.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);

                        return true;
                    case R.id.homeBottomBar:
                        return true;

                    case R.id.myGigsBottomBar:
                        startActivity(new Intent(ProfileActivity.this,myGigs.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.requestsBottomBar:
                        startActivity(new Intent(ProfileActivity.this,RequestsView.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.chatBottomBar:
                        startActivity(new Intent(ProfileActivity.this, Messenger.class));
                        overridePendingTransition(0,0);
                        return true;

                }


                return false;
            }
        });


    }








    private void choosePicture() {
        Log.d("debugTag", "Choose Pic");
        mGetContent.launch("image/*");


    }




    private void getItemImageUrl(StorageReference referenceStorage) {

        StorageReference storageRef =
                FirebaseStorage.getInstance().getReference();
        storageRef.child("users/"+userId+"/images/profilePic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Log.i("URL", uri.toString());
                reference.child(userId).child("uriProfile").setValue(uri.toString());
                Glide.with(ProfileActivity.this)
                        .load(uri.toString())
                        .into(profilePic);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.i("URL", "failed");
                Glide.with(ProfileActivity.this)
                        .load("https://firebasestorage.googleapis.com/v0/b/lyncse-33410.appspot.com/o/users%2Fme%2Fprofile.png?alt=media&token=2490e504-d637-401b-8f66-5f96dabcb2d7")
                        .into(profilePic);
            }
        });


    }


    private void getUriProfile(){

        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile !=null){
                    if(userProfile.uriProfile.equals("Default")){
                        Glide.with(ProfileActivity.this)
                                .load(R.drawable.ic_baseline_android_200)
                                .into(profilePic);
                    }else {
                        Glide.with(ProfileActivity.this)
                                .load(userProfile.uriProfile)
                                .into(profilePic);
                    }
                    fullNameTextView.setText(welcome);
                    setListeners();
                    llMain.setVisibility(View.VISIBLE);
                    flLoad.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }



    private void uploadPicture(){
        Log.d("debugTag", "In Upload");
        if(imageUri!=null ){
            Log.d("debugTag", "In If Upload");
            StorageReference reference = storage.getReference().child("users/"+userId+"/images/profilePic");
            //we are creating a reference to store the image in firebase storage.
            //it will be stored inside images folder in firebase storage
            //you can use user auth id instead of uuid if your app has  firebase auth

            //now using the below code we will store the file


            reference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        //Image uploaded
                        Toast.makeText(ProfileActivity.this,"Image uploaded",Toast.LENGTH_LONG).show();
                        Log.d("debugTag", "Success");
                        getItemImageUrl(storageReference);
                    }else{
                        Log.d("debugTag", "Fail");
                        //Failed
                        Toast.makeText(ProfileActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });



        }



    }



}
