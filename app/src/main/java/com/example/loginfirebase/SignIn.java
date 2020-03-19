package com.example.loginfirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button signout, newPost, viewPost, sent;
    private TextView emailName,data;
    private EditText input;
    private FirebaseDatabase database;
    private DatabaseReference inputMsg;
    private String dataRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();

        signout = (Button) findViewById(R.id.signout);
        emailName = (TextView) findViewById(R.id.emailName);
        data = (TextView) findViewById(R.id.data);
        newPost = (Button) findViewById(R.id.newPost);
        viewPost = (Button) findViewById(R.id.viewPost);

        database = FirebaseDatabase.getInstance();

        inputMsg = database.getReference(mAuth.getCurrentUser().getUid());
        inputMsg.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                dataRead = dataSnapshot.getValue(String.class);
                data.setText(dataRead);
            }


            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Test", "Failed to read value.", error.toException());
            }
        });

        if(mAuth.getCurrentUser() != null){
            emailName.setText(mAuth.getCurrentUser().getEmail());
        }


        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignIn.this, MainActivity.class);
                mAuth.signOut();
                finish();
                startActivity(i);
            }
        });

        newPost.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NewPost.class));

            }
        });

        viewPost.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ViewPost.class));
            }
        });

    }

    public void onSentButtonClick(View view) {
        input = (EditText) findViewById(R.id.input);
        sent = (Button) findViewById(R.id.sent);

        String msg = input.getText().toString();
        inputMsg.setValue(msg);
    }
}
