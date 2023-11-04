package com.example.onlinevotingapp2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.onlinevotingapp2.Adapter.CandidateAdapter;
import com.example.onlinevotingapp2.Model.Candidate;
import com.example.onlinevotingapp2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ManageElectionActivity extends AppCompatActivity {

    private RecyclerView managerv;
    private List<Candidate> list;
    private CandidateAdapter adapter;
    private FirebaseFirestore firebaseFirestore;
    private String electionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_election);

        Intent intent=getIntent();
        electionId=intent.getStringExtra("ele_name");

        managerv=findViewById(R.id.managecandidate_rv);
        firebaseFirestore=FirebaseFirestore.getInstance();


        list=new ArrayList<>();
        adapter=new CandidateAdapter(ManageElectionActivity.this,list);
        managerv.setLayoutManager(new LinearLayoutManager(this));
        managerv.setAdapter(adapter);


        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            loadCandidateForElection(electionId);
        }


    }
    private void loadCandidateForElection(String electionId){
        if(electionId!=null){
            firebaseFirestore.collection("Candidate")
                    .whereEqualTo("electionId",electionId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(DocumentSnapshot snapshot: Objects.requireNonNull(task.getResult())){
                                    Log.d("CandidateActivity", "Candidate name: " + snapshot.getString("name"));
                                    list.add(new Candidate(
                                            snapshot.getString("name"),
                                            snapshot.getString("party"),
                                            snapshot.getString("image"),
                                            snapshot.getId(),
                                            snapshot.getString("electionId")
                                            //itwill get document id
                                    ));
                                }
                                adapter.notifyDataSetChanged();
                            }else{
                                Toast.makeText(ManageElectionActivity.this, "Candidate not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}