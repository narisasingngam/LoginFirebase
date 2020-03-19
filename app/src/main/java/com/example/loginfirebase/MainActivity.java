package com.example.loginfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText email,password;
    private Button signin, signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        signin = (Button) findViewById(R.id.signin);
        signup = (Button) findViewById(R.id.signup);
        email = (EditText) findViewById(R.id.emailName);
        password = (EditText) findViewById(R.id.password);

//        check if the user is login
        if(mAuth.getCurrentUser() != null){
//            if user not login
            finish();
            startActivity(new Intent(getApplicationContext(),SignIn.class));

        }
        signin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String getemail = email.getText().toString();
                String getpassword = password.getText().toString();
                callsignin(getemail,getpassword);
            }
        });

        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String getemail = email.getText().toString();
                String getpassword = password.getText().toString();
                callsignup(getemail,getpassword);
                callsignin(getemail,getpassword);
            }
        });

    }

    private void callsignup(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "createUserWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                            userProfile();
                            Toast.makeText(MainActivity.this, "Account create",
                                    Toast.LENGTH_SHORT).show();
                            Log.d("Test","Account Created");
                        } else {
                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Sign up fail"+ task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void userProfile()
    {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(email.getText().toString()).build();
            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Log.d("Test","User profile update");
                    }
                }
            });

        }
    }

    private void callsignin(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("Test","Sign in is successful" + task.isSuccessful());
                if(!task.isSuccessful()){
                    Log.d("Test","Sign in with email failed", task.getException());
                    Toast.makeText(MainActivity.this,"Login Faild", Toast.LENGTH_SHORT).show();
                }else{
                    Intent i = new Intent(MainActivity.this, SignIn.class);
                    finish();
                    startActivity(i);
                }
            }
        });

    }
}
