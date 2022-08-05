package com.lyncseapp.lyncse.stripe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lyncseapp.lyncse.R;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StripeWebView extends AppCompatActivity {
    private static final String TAG = StripeWebView.class.getSimpleName();
    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private final static int FCR=1;

    private WebView wv;
    private String url;
    private TextView ta;
    private TextView te;
    private TextView tr;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private FirebaseUser user;
    private String userId;
    private Button bd;
    private Button bc;
    private LinearLayout llwv;
    private LinearLayout llsi;
    private LinearLayout llMain;
    private ImageButton ibb;
    private FrameLayout flpbs;
    private FrameLayout flmpb;
    private PermissionRequest pr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId= user.getUid();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("StripeUsers/"+userId+"/Stripe");
        url = getIntent().getStringExtra("STRIPE_URL");
        getElements();
        startWebView();

        if(url.equals("NONE")){
            llwv.setVisibility(View.GONE);
            getAccountInfoFireBase();
        }else{
            wv.loadUrl(url);
            llwv.setVisibility(View.VISIBLE);
        }

        setListeners();
    }
    private void startWebView(){
        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        llMain.setVisibility(View.VISIBLE);
        flmpb.setVisibility(View.GONE);
        wv.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                // Here you can check your new URL.
                System.out.println(url);
                flpbs.setVisibility(View.VISIBLE);
                if(url.toString().equals("https://example.com/reauth")){
                    System.out.println("reAuth");
                    llwv.setVisibility(View.GONE);
                    updateAccountFireBase();

                } else if(url.toString().equals("https://example.com/return")){
                    System.out.println("return");
                    llwv.setVisibility(View.GONE);
                    updateAccountFireBase();
                    //Update acct info in database
                    //update acct info in app
                }else{

                }

                super.onPageStarted(view, url,favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                flpbs.setVisibility(View.GONE);
            }

        });

        wv.setWebChromeClient(new WebChromeClient() {
            //For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg){
                mUM = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                StripeWebView.this.startActivityForResult(Intent.createChooser(i,"File Chooser"), FCR);
            }
            // For Android 3.0+, above method not supported in some android 3+ versions, in such case we use this
            public void openFileChooser(ValueCallback uploadMsg, String acceptType){
                mUM = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                StripeWebView.this.startActivityForResult(
                        Intent.createChooser(i, "File Browser"),
                        FCR);
            }
            //For Android 4.1+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
                mUM = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                StripeWebView.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), StripeWebView.FCR);
            }
            //For Android 5.0+
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    WebChromeClient.FileChooserParams fileChooserParams){
                if(mUMA != null){
                    mUMA.onReceiveValue(null);
                }
                mUMA = filePathCallback;

/*                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(takePictureIntent.resolveActivity(StripeWebView.this.getPackageManager()) != null){
                    File photoFile = null;
                    try{
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCM);
                    }catch(IOException ex){
                        Log.e(TAG, "Image file creation failed", ex);
                    }
                    if(photoFile != null){
                        mCM = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    }else{
                        takePictureIntent = null;
                    }
                }*/

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("*/*");
                Intent[] intentArray;

                    intentArray = new Intent[0];


                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooserIntent, FCR);
                return true;
            }
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                System.out.println("requesting permission camera" + request);

                pr=request;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    String permission = Manifest.permission.CAMERA;
                    int grant = ContextCompat.checkSelfPermission(StripeWebView.this, permission);
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        String[] permission_list = new String[1];
                        permission_list[0] = permission;
                        ActivityCompat.requestPermissions(StripeWebView.this, permission_list, 1);

                    }else{
                        request.grant(request.getResources());
                    }
                }else{
                    request.grant(request.getResources());
                }
            }

        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(StripeWebView.this,"permission granted", Toast.LENGTH_SHORT).show();
                // perform your action here
                pr.grant(pr.getResources());

            } else {
                Toast.makeText(StripeWebView.this,"permission not granted", Toast.LENGTH_SHORT).show();
            }
        }

    }
    public class Callback extends WebViewClient{
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl){
            Toast.makeText(getApplicationContext(), "Failed loading app!", Toast.LENGTH_SHORT).show();
        }
    }
    // Create an image file
    private File createImageFile() throws IOException{
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_"+timeStamp+"_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName,".jpg",storageDir);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if(Build.VERSION.SDK_INT >= 21){
            Uri[] results = null;
            //Check if response is positive
            if(resultCode== Activity.RESULT_OK){
                if(requestCode == FCR){
                    if(null == mUMA){
                        return;
                    }
                    if(intent == null || intent.getData() == null){
                        //Capture Photo if no image available
                        if(mCM != null){
                            results = new Uri[]{Uri.parse(mCM)};
                        }
                    }else{
                        String dataString = intent.getDataString();
                        if(dataString != null){
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }
            }
            mUMA.onReceiveValue(results);
            mUMA = null;
        }else{
            if(requestCode == FCR){
                if(null == mUM) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUM.onReceiveValue(result);
                mUM = null;
            }
        }
    }

    private void getElements(){
        wv= findViewById(R.id.webViewStripe);
        ta= findViewById(R.id.stripeAccount);
        tr= findViewById(R.id.stripeRequirements);
        te= findViewById(R.id.stripeEnabled);
        bc= findViewById(R.id.connect_with_stripe);
        bd= findViewById(R.id.stripe_dashboard);
        llwv= findViewById(R.id.llWebViewStripe);
        llsi= findViewById(R.id.llStripeInfo);
        ibb=findViewById(R.id.imageButtonBackStripe);
        flpbs=findViewById(R.id.flWebViewStripe);
        llMain = findViewById(R.id.llMainStripe);
        flmpb = findViewById(R.id.flMainProgressBar);
    }

    private void getAccountInfoFireBase(){
        llMain.setVisibility(View.GONE);
        flmpb.setVisibility(View.VISIBLE);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot != null) {
                    System.out.println(snapshot.child("id").getValue() + " " + snapshot.child("charges_enabled").getValue());
                    if(snapshot.hasChild("id")) {
                        ta.setText(snapshot.child("id").getValue().toString());
                    }

                    if(snapshot.hasChild("charges_enabled")) {
                        if (snapshot.child("charges_enabled").getValue().toString().equals("false")) {
                            te.setText(snapshot.child("charges_enabled").getValue().toString());
                            bc.setVisibility(View.VISIBLE);
                            bd.setVisibility(View.GONE);
                        } else {
                            te.setText(snapshot.child("charges_enabled").getValue().toString());
                            bc.setVisibility(View.GONE);
                            bd.setVisibility(View.VISIBLE);
                        }
                    }
                    if (snapshot.child("requirements").hasChild("currently_due")) {
                        System.out.println(snapshot.child("currently_due").getValue());
                        StringBuilder result = new StringBuilder();
                        for (DataSnapshot u : snapshot.child("requirements").child("currently_due").getChildren()) {
                            result.append(u.getValue().toString());
                            result.append("\n");
                        }
                        System.out.println();
                        tr.setText(result.toString());
                    } else {
                        tr.setText("None");
                    }
                    llMain.setVisibility(View.VISIBLE);
                    flmpb.setVisibility(View.GONE);
                    llsi.setVisibility(View.VISIBLE);
                }else{
                    llMain.setVisibility(View.VISIBLE);
                    flmpb.setVisibility(View.GONE);
                    llsi.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void setListeners(){
        bc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flmpb.setVisibility(View.VISIBLE);callCreateStripeConnectedAccount();
            }
        });
        ibb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        bd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { flmpb.setVisibility(View.VISIBLE);callCreateLogInLink(); }
        });
    }
    private void updateAccountFireBase(){
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
        }).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                System.out.println("done");
                if(task.isSuccessful()){
                    if(task.getResult().getData().toString().equals("noActionNeeded")) {
                        System.out.println("NoActionNeeded");

                    } else if(task.getResult().getData().toString().equals("createdAccount")){
                        System.out.println("createdAccount");
                    }else {

                    }
                    getAccountInfoFireBase();
                }else{
                    Toast.makeText(getApplicationContext(), "Task is NOT Successful", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void callCreateLogInLink(){
        FirebaseFunctions.getInstance("us-central1") // Optional region: .getInstance("europe-west1")
                .getHttpsCallable( "createStripeLogInLink")
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
        }).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                System.out.println("done");
                flmpb.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    if(task.getResult().getData().toString().equals("noActionNeeded")) {
                        System.out.println("NoActionNeeded");
                    }
                    else {
                        System.out.println("link "+ task.getResult().getData());
                        wv.loadUrl(task.getResult().getData().toString());
                        llsi.setVisibility(View.GONE);
                        flpbs.setVisibility(View.VISIBLE);
                        llwv.setVisibility(View.VISIBLE);
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Task is NOT Successful", Toast.LENGTH_LONG).show();
                }


            }
        });
    }
    private void callCreateStripeConnectedAccount(){
        //Function works by
        //-Verify account created
        //-if created UPDATE account on firebase database
        //-verify if account is enabled
        //-if not enabled send link to finish onboard/ if enabled returns "Account no immediate action required"

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
        }).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                System.out.println("done");
                flmpb.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    if(task.getResult().getData().toString().equals("noActionNeeded")) {
                        System.out.println("NoActionNeeded");

                    } else if(task.getResult().getData().toString().equals("createdAccount")){
                        System.out.println("createdAccount");
                    }else {
                        wv.loadUrl(task.getResult().getData().toString());
                        llsi.setVisibility(View.GONE);
                        flpbs.setVisibility(View.VISIBLE);
                        llwv.setVisibility(View.VISIBLE);

                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Task is NOT Successful", Toast.LENGTH_LONG).show();
                }


            }
        });
    }
}