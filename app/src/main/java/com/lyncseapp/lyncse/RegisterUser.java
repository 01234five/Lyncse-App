package com.lyncseapp.lyncse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener{


    private TextView registerUser;
    private LinearLayout banner;
    private EditText editTextFirstName, editTextLastName,editTextAge,editTextEmail,editTextPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FrameLayout flLoad;
    private ImageButton bb;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);


        mAuth = FirebaseAuth.getInstance();

        banner= (LinearLayout) findViewById (R.id.titleIdBannerRegisterUser);
        banner.setOnClickListener (this);
        registerUser =(Button) findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        editTextFirstName = (EditText) findViewById(R.id.firstNameRegisterUser);
        editTextLastName = (EditText) findViewById(R.id.lastNameRegisterUser);
        editTextAge= (EditText) findViewById(R.id.ageRegisterUser);
        editTextEmail=(EditText) findViewById(R.id.emailRegisterUser);
        editTextPassword = (EditText) findViewById(R.id.passwordRegisterUser);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2RegisterUser);
        flLoad = findViewById(R.id.frameLayout_RegisterUser);
        bb=findViewById(R.id.imageButtonRegisterUser);
        setListeners();
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.titleIdBannerRegisterUser:
                finish();
                break;
            case R.id.registerUser:
                registerUser();
        }

    }
    private void setListeners(){
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public static String capitalize(String str) {
        if(str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();
        String uriProfile = "Default";


        if (firstName.isEmpty()) {
            editTextFirstName.setError("First name is required");
            editTextFirstName.requestFocus();
            return;
        }
        if (lastName.isEmpty()) {
            editTextLastName.setError("Last name is required");
            editTextLastName.requestFocus();
            return;
        }
        if (age.isEmpty()) {
            editTextAge.setError("Age is required");
            editTextAge.requestFocus();
            return;
        }
        if (age.length() > 0) {
            int intAge = Integer.valueOf(editTextAge.getText().toString());
            if (intAge < 18) {
                editTextAge.setError("Must be at least 18 to register");
                editTextAge.requestFocus();
                return;
            }
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Valid email is required");
            editTextEmail.requestFocus();
            return;

        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters");
            editTextPassword.requestFocus();
            return;

        }
        progressBar.setVisibility(View.VISIBLE);
        flLoad.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(capitalize(firstName),capitalize(lastName), age, email,uriProfile);
                            FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterUser.this,"User has been registered successfully", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        flLoad.setVisibility(View.GONE);
                                        finish();
                                    }else{

                                        Toast.makeText(RegisterUser.this,"Failed. Try again.", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        flLoad.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(RegisterUser.this,"Failed. Try again.", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            flLoad.setVisibility(View.GONE);
                        }
                    }
                });





    }

}