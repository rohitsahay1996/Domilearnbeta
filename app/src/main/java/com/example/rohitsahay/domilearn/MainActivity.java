package com.example.rohitsahay.domilearn;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button mLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLogout=(Button)findViewById(R.id.logoutbtn);
       mAuth = FirebaseAuth.getInstance();

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                sendtoAuth();
            }
        });
    }

    private void sendtoAuth() {
        Intent authIntent = new Intent(MainActivity.this,AuthActivity.class);
        startActivity(authIntent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

    if(currentUser==null){

        sendtoAuth();

    }


    }
}
