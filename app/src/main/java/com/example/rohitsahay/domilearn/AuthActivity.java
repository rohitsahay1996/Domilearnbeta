package com.example.rohitsahay.domilearn;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class AuthActivity extends AppCompatActivity {
    private EditText mNumber,mCode;
    private RelativeLayout mPhoneLayout;
    private RelativeLayout mCodeLayout;
    private ProgressBar mNumberBar,mCodeBar;
    private Button mVerify;
    private EditText mName;
    private TextView mErrorText;
    private int btnTyp=0;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);


        mNumber = (EditText) findViewById(R.id.number);
        mName = (EditText) findViewById(R.id.userName);
        mCode = (EditText) findViewById(R.id.code);
        mVerify = (Button) findViewById(R.id.verifybtn);
        mErrorText = (TextView) findViewById(R.id.errortext);
        mPhoneLayout = (RelativeLayout) findViewById(R.id.rl1);
        mCodeLayout = (RelativeLayout) findViewById(R.id.rl2);
        mNumberBar = (ProgressBar)findViewById(R.id.phonebar);
        mCodeBar = (ProgressBar)findViewById(R.id.codebar);

        mAuth=FirebaseAuth.getInstance();



        mVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String phoneNumber0 = mNumber.getText().toString();



                if(btnTyp == 0) {
                    mNumber.setEnabled(false);
                    mVerify.setEnabled(false);
                    String nationalcode = "+91";

                  //  final String phoneNumber0 = mNumber.getText().toString();
                    String phoneNumber = nationalcode+phoneNumber0;


                   // Toast.makeText(AuthActivity.this, "" + phoneNumber, Toast.LENGTH_SHORT).show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,
                            60,
                            TimeUnit.SECONDS,
                            AuthActivity.this,
                            mCallBacks


                    );
                }else {


                    mVerify.setEnabled(false);
                    mCodeBar.setVisibility(View.VISIBLE);
                    String verificationCode = mCode.getText().toString();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }

            }
        });


        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential);
            }




            @Override
            public void onVerificationFailed(FirebaseException e) {
                mErrorText.setVisibility(View.VISIBLE);
                mErrorText.setText("Error in verification");
                mNumber.setEnabled(true);
                mVerify.setEnabled(true);
                btnTyp=0;


            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {


                mVerificationId = verificationId;
                mResendToken = token;
                btnTyp = 1;
                mVerify.setEnabled(true);

                mNumberBar.setVisibility(View.INVISIBLE);
                mCodeLayout.setVisibility(View.VISIBLE);

                mVerify.setText("Verify Code");

                // ...
            }
        };

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            String phoneNumber0 = mNumber.getText().toString();
                            String username = mName.getText().toString();

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference().child("Users").child(phoneNumber0);
                            HashMap<String,String> userMap = new HashMap<String, String>();
                            userMap.put("name",username);
                            userMap.put("mobile","+91"+phoneNumber0);
                            myRef.setValue(userMap);

                           // Toast.makeText(AuthActivity.this, ""+phoneNumber0+"/"+username, Toast.LENGTH_SHORT).show();



                            // Sign in success, update UI with the signed-in user's information
                            Intent mainIntent = new Intent(AuthActivity.this,MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                            Toast.makeText(AuthActivity.this, "Login Successfull", Toast.LENGTH_SHORT).show();

                            FirebaseUser user = task.getResult().getUser();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                           mErrorText.setVisibility(View.VISIBLE);
                            mErrorText.setText("Error detected");
                            mCodeBar.setVisibility(View.INVISIBLE);
                            mVerify.setEnabled(true);
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }


}
