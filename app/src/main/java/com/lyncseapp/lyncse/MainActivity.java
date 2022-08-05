package com.lyncseapp.lyncse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView register, forgotPassword;
    private EditText editTextEmail, editTextPassword;
    private Button signIn;
    private FrameLayout flMain;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        flMain = findViewById(R.id.frameLayout_Main);
        register = (TextView) findViewById(R.id.Register);
        register.setOnClickListener(this);


        signIn = (Button) findViewById(R.id.signInBtn);
        signIn.setOnClickListener(this);



        editTextEmail = (EditText) findViewById(R.id.emailId);
        editTextPassword = (EditText) findViewById(R.id.password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(this);

    }










    @Override
    public void onClick (View v){
        switch(v.getId()) {

            case R.id.Register:
                startActivity(new Intent(this, RegisterUser.class));
                break;

            case R.id.signInBtn:
                userLogin();
                break;

            case R.id.forgotPassword:
                 startActivity(new Intent(this,ForgotPassword.class));
                 break;

            case R.id.buttonTest:

                //

                break;
        }
    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password= editTextPassword.getText().toString().trim();
        if(email.isEmpty()){
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            editTextPassword.setError("Password is required");
            editTextEmail.requestFocus();
            return;
        }

        if(password.length() < 6){
            editTextPassword.setError("invalid Password");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        flMain.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d("loginTag", "User record found in FireBase");
                    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

                    if(user.isEmailVerified()){

                        //redirect to user profile
                        Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
                        intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        progressBar.setVisibility(View.GONE);
                        flMain.setVisibility(View.GONE);
                        finish();
                    }else{
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this,"Email verification required. Check email your email inbox", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        flMain.setVisibility(View.GONE);
                    }

                }else{
                    Toast.makeText(MainActivity.this,"Failed to log in",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    flMain.setVisibility(View.GONE);
                }
            }
        });

    }
}