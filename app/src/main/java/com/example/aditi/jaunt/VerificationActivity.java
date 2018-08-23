package com.example.aditi.jaunt;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity
{
    String number;
    EditText codeText;
    String phoneVerificationId;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks
            verificationCallbacks;
    PhoneAuthProvider.ForceResendingToken resendToken;
    FirebaseAuth fbAuth;

    ViewFlipper v_flipper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_verification);

      //  FirebaseApp.initializeApp(getApplicationContext());
        fbAuth = FirebaseAuth.getInstance();

        v_flipper = (ViewFlipper) findViewById(R.id.view_flipper);
        int images[] = {R.drawable.splash3, R.drawable.splash8, R.drawable.splash2};

        for (int image : images)
        {
            flipperImages(image);
        }

        codeText = (EditText) findViewById(R.id.codeText);

        Intent intent = getIntent();
        number = intent.getExtras().getString("num");

        setUpVerificatonCallbacks();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                verificationCallbacks);
    }


    public void flipperImages(int image)
    {
        ImageView imageView=new ImageView(this);
        imageView.setBackgroundResource(image);

        v_flipper.addView(imageView);
        v_flipper.setFlipInterval(2000);
        v_flipper.setAutoStart(true);
        //  v_flipper.setInAnimation(this,android.R.anim.slide_in_left);
        v_flipper.setOutAnimation(this,android.R.anim.slide_out_right);
    }

    private void setUpVerificatonCallbacks() {

        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(
                            PhoneAuthCredential credential)
                    {

                        codeText.setText("");
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e)
                    {
                        if (e instanceof FirebaseAuthInvalidCredentialsException)
                        {
                            // Invalid request
                            Toast.makeText(getApplicationContext(),"Invalid credential: ",Toast.LENGTH_SHORT ).show();
                        }

                        else if (e instanceof FirebaseTooManyRequestsException)
                        {
                            // SMS quota exceeded
                            Toast.makeText(getApplicationContext(),"SMS Quota exceeded. ",Toast.LENGTH_SHORT ).show();
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {

                        phoneVerificationId = verificationId;
                        resendToken = token;

                    }
                };
    }

    public void verifyCode(View view)
    {
        String code = codeText.getText().toString();

        PhoneAuthCredential credential =
                PhoneAuthProvider.getCredential(phoneVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            codeText.setText("");
                            FirebaseUser user = task.getResult().getUser();
                            String phoneNumber = user.getPhoneNumber();

                            Intent intent = new Intent(VerificationActivity.this, MainActivity.class);
                            intent.putExtra("phone", phoneNumber);
                            startActivity(intent);
                            finish();

                        } else {
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    public void resendcode(View view)
    {

        setUpVerificatonCallbacks();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                this,
                verificationCallbacks,
                resendToken);
    }
}
