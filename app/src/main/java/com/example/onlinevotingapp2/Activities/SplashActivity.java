package com.example.onlinevotingapp2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.onlinevotingapp2.MainActivity;
import com.example.onlinevotingapp2.R;

public class SplashActivity extends AppCompatActivity {

    TextView appname;
    LottieAnimationView lottie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        lottie=findViewById(R.id.lottie);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    Intent i=new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                    finish();
            }
        },3000);

    }
}