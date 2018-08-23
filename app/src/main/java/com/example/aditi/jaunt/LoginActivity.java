package com.example.aditi.jaunt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.hbb20.CountryCodePicker;

public class LoginActivity extends AppCompatActivity {

    ViewFlipper v_flipper;
    CountryCodePicker ccp;
    EditText phoneText;
    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        v_flipper = (ViewFlipper) findViewById(R.id.view_flipper);
        int images[] = {R.drawable.splash3, R.drawable.splash8, R.drawable.splash2};

        for (int image : images)
        {
            flipperImages(image);
        }
        phoneText = (EditText) findViewById(R.id.phoneText);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(phoneText);

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


    public void next(View view)
    {
       number= ccp.getFullNumberWithPlus();
        Intent i = new Intent(LoginActivity.this,VerificationActivity.class);
        i.putExtra("num",number);
        startActivity(i);
    }
}
