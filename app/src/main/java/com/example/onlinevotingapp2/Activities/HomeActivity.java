package com.example.onlinevotingapp2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
    private Button createBtn,createElectionBtn, manageElectionBtn,showAllElections;

    private CardView cardViewadmin_btn,cardViewcreate_election_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        sharedPreferences = getApplicationContext().getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean(IsLogIn, false);

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
//        voteBtn = findViewById(R.id.give_vote);


        createElectionBtn = findViewById(R.id.create_election_btn);
//        manageElectionBtn = findViewById(R.id.manage_election_btn);
        showAllElections = findViewById(R.id.showallelections);
        cardViewadmin_btn=findViewById(R.id.cardviewadmin_btn);
        cardViewcreate_election_btn=findViewById(R.id.cardviewcreate_election_btn);

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
                                createElectionBtn.setVisibility(View.VISIBLE);
//                                manageElectionBtn.setVisibility(View.VISIBLE);
//                                voteBtn.setVisibility(View.GONE);
                                showAllElections.setVisibility(View.VISIBLE);
                                cardViewadmin_btn.setVisibility(View.VISIBLE);
                                cardViewcreate_election_btn.setVisibility(View.VISIBLE);
                            } else {
//                                createBtn.setVisibility(View.GONE);
                                createBtn.setVisibility(View.GONE);
//                                voteBtn.setVisibility(View.GONE);
                                createElectionBtn.setVisibility(View.GONE);
//                                manageElectionBtn.setVisibility(View.GONE);
                                showAllElections.setVisibility(View.VISIBLE);
                                cardViewcreate_election_btn.setVisibility(View.GONE);
                                cardViewadmin_btn.setVisibility(View.GONE);

                                Log.d("UserRole", "User is not admin");
                            }
                            nameTxt.setText(name);
                            aadharNoTxt.setText(aadharno);

                            Log.d("ImageURL", "Image URL: " + image);
                            Glide.with(HomeActivity.this).load(image).into(circleImg);

                        } else {
                            Toast.makeText(HomeActivity.this, "User not Found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

//        createBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                startActivity(new Intent(HomeActivity.this, Create_Candidate_Activity.class));
//            }
//        });
//        createBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(HomeActivity.this,AllCandidateActivity.class));
//            }
//        });

//        voteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(HomeActivity.this,AllCandidateActivity.class));
//            }
//        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, Create_Candidate_Activity.class));
            }
        });

        createElectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, Create_Election_Activity.class));
            }
        });

//        manageElectionBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(HomeActivity.this, ManageElectionActivity.class));
//            }
//        });
        showAllElections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,ElectionActivity.class));
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
            startActivity(new Intent(HomeActivity.this,ResultActivityElection.class));
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
