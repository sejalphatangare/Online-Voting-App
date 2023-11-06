package com.example.onlinevotingapp2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private CircleImageView image;
    private TextView nameTxt, emailTxt,aadharNoTxt;
    private String uid;
    private FirebaseFirestore firebaseFirestore;
    private Button updateBtn,logout,backBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        image = findViewById(R.id.profile_image);
        nameTxt = findViewById(R.id.user_Name);
        emailTxt= findViewById(R.id.user_Email);
        logout=findViewById(R.id.logout);
        aadharNoTxt = findViewById(R.id.user_aadharno);
        firebaseFirestore = FirebaseFirestore.getInstance();
        backBtn=findViewById(R.id.backbtn);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        uid=user.getUid();

        updateBtn=findViewById(R.id.update_btn);

        loadProfileDetails();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,HomeActivity.class));
                finish();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,UpdateProfileActivity.class));
                finish();
            }
        });


    }

    private void loadProfileDetails() {
        firebaseFirestore.collection("Users")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            String img=documentSnapshot.getString("image");
                            String nm=documentSnapshot.getString("name");
                            String em=documentSnapshot.getString("email");
                            String aadhar_no=documentSnapshot.getString("aadharno");

                            nameTxt.setText(nm);
                            emailTxt.setText(em);
                            aadharNoTxt.setText(aadhar_no);

                            Log.d("ImageURL", "Image URL: " + image);
                            Glide.with(ProfileActivity.this).load(img).into(image);


                        }else{
                            Toast.makeText(ProfileActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                            finish();
                        }
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
//        SharedPreferences.Editor pref = sharedPreferences.edit();
        if(id == R.id.home_activity){
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            finish();
        }
        else if (id == R.id.show_result) {
            startActivity(new Intent(getApplicationContext(),ResultActivityElection.class));
            finish();
            return true;
        } else if (id == R.id.log_out) {
            FirebaseAuth.getInstance().signOut();
//            pref.putBoolean(IsLogIn, false);
//            pref.apply();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }


}