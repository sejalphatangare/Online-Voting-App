package com.example.onlinevotingapp2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.onlinevotingapp2.Model.Election;
import com.example.onlinevotingapp2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Create_Election_Activity extends AppCompatActivity {
    private EditText electionName,startDate,endDate;
    private Button makeElectionBtn,backToMainPage;
    private DatePicker startElectionDate,endElectionDate;
    private FirebaseFirestore firestore;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_election);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        electionName = findViewById(R.id.electionName);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        makeElectionBtn = findViewById(R.id.makeElectionBtn);
        startElectionDate = findViewById(R.id.startDatePicker);
        endElectionDate = findViewById(R.id.endDatePicker);
        backToMainPage = findViewById(R.id.backToInfoPageFromCreateElectionECA);

        backToMainPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        });

        makeElectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day = startElectionDate.getDayOfMonth();
                int month = startElectionDate.getMonth();
                int year = startElectionDate.getYear();
                String startElectionDateFormatted = String.valueOf(day) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year);
                int dayE = endElectionDate.getDayOfMonth();
                int monthE = endElectionDate.getMonth();
                int yearE = endElectionDate.getYear();
                String endElectionDateFormatted = String.valueOf(dayE) + "/" + String.valueOf(monthE + 1) + "/" + String.valueOf(yearE);

                String elename = electionName.getText().toString().trim();
                String creatorUid = Objects.requireNonNull(auth.getCurrentUser()).getUid();

                if (!TextUtils.isEmpty(elename) && !TextUtils.isEmpty(startElectionDateFormatted) && !TextUtils.isEmpty(endElectionDateFormatted)) {


                    Map<String, Object> electionData = new HashMap<>();
                    electionData.put("name", elename);
                    electionData.put("start_date", startElectionDateFormatted);
                    electionData.put("end_date", endElectionDateFormatted);
                    electionData.put("creator", creatorUid);

                    firestore.collection("Elections")
                            .add(electionData)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(Create_Election_Activity.this, HomeActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(Create_Election_Activity.this, "Data not stored", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                }
            }

        });
    }
}