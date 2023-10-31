package com.example.onlinevotingapp2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.onlinevotingapp2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    public static final String PREFERENCES = "prefKey";
    SharedPreferences sharedPreferences;
    public static final String IsLogIn = "islogin";
    private CircleImageView circleImg;
    private TextView nameTxt, aadharNoTxt;
    private String uid;
    private FirebaseFirestore firebaseFirestore;
    private Button createBtn, voteBtn,startBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        sharedPreferences = getApplicationContext().getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean(IsLogIn, false);

//        if (!isLoggedIn) {
//            // The user is not logged in; navigate back to the login activity
//            startActivity(new Intent(this, LoginActivity.class));
//            finish(); // Close the current activity
//        }
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user == null) {
//            // The user is not logged in; navigate back to the login activity
//            startActivity(new Intent(this, LoginActivity.class));
//            finish(); // Close the current activity
//        }

        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            uid=user.getUid();
        }else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        circleImg = findViewById(R.id.circle_image);
        nameTxt = findViewById(R.id.name);
        aadharNoTxt = findViewById(R.id.aadhar_no);
        createBtn = findViewById(R.id.admin_btn);
        voteBtn = findViewById(R.id.give_vote);
        startBtn=findViewById(R.id.candidate_create_voting);

        sharedPreferences = getApplicationContext().getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor pref = sharedPreferences.edit();
        pref.putBoolean(IsLogIn, true);
        pref.apply();

//        findViewById(R.id.log_out).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                pref.putBoolean(IsLogIn,false);
//                pref.commit();
//                startActivity(new Intent(HomeActivity.this,LoginActivity.class));
//                finish();
//            }
//        });

        firebaseFirestore.collection("Users")
                .document(uid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String name = task.getResult().getString("name");
                            String aadharno = task.getResult().getString("aadharno");
                            String image = task.getResult().getString("image");

                            Log.d("UserRole", "User name: " + name); // Add this line for debugging
                            assert name != null;
                            if (name.equals("admin")) {
                                createBtn.setVisibility(View.VISIBLE);
                                startBtn.setVisibility(View.GONE);
                                voteBtn.setVisibility(View.GONE);
                            } else {
                                createBtn.setVisibility(View.GONE);
                                startBtn.setVisibility(View.VISIBLE);
                                voteBtn.setVisibility(View.VISIBLE);
                                Log.d("UserRole", "User is not admin");
                            }
                            nameTxt.setText(name);
                            aadharNoTxt.setText(aadharno);

                            Glide.with(HomeActivity.this).load(image).into(circleImg);

                        } else {
                            Toast.makeText(HomeActivity.this, "User not Found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(HomeActivity.this, Create_Candidate_Activity.class));
            }
        });
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,AllCandidateActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        SharedPreferences.Editor pref = sharedPreferences.edit();
        if (id == R.id.show_result) {
            return true;
        } else if (id == R.id.log_out) {
            FirebaseAuth.getInstance().signOut();
            pref.putBoolean(IsLogIn, false);
            pref.apply();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
