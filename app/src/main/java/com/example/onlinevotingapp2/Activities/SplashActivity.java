package com.example.onlinevotingapp2.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.onlinevotingapp2.MainActivity;
import com.example.onlinevotingapp2.R;

public class SplashActivity extends AppCompatActivity {

    public static final String PREFERENCES="prefKey";
    SharedPreferences sharedPreferences;
    public static final String IsLogIn="isLogin";

    TextView appname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();

        sharedPreferences=getApplicationContext().getSharedPreferences(PREFERENCES,MODE_PRIVATE);
        boolean bol=sharedPreferences.getBoolean(IsLogIn,false);

        new Handler().postDelayed(() -> {
            if(bol){
                Intent i=new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }else{
                Intent i=new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }

        },3000);

    }
}