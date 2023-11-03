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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AllCandidateActivity extends AppCompatActivity {

    private RecyclerView candidateRV;
    private Button startBtn;
    private List<Candidate> list;
    private CandidateAdapter adapter;
    private FirebaseFirestore firebaseFirestore;
    private String electionId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_candidate);

        Intent intent=getIntent();
        electionId=intent.getStringExtra("ele_name");
//        String electionName=intent.getStringExtra("name");
//        Bundle extras = intent.getExtras();
//        if (extras != null) {
//            // Iterate through the keys and print the corresponding values
//            for (String key : extras.keySet()) {
//                Object value = extras.get(key);
//                if (value != null) {
//                    // Print the key and value
////                    System.out.println();
//                    Log.d(electionId,"Key: " + key + ", Value: " + value.toString());
//                }
//            }
//        }



        candidateRV=findViewById(R.id.candidates_rv);
        startBtn=findViewById(R.id.start);
        firebaseFirestore=FirebaseFirestore.getInstance();


        list=new ArrayList<>();
        adapter=new CandidateAdapter(AllCandidateActivity.this,list);
        candidateRV.setLayoutManager(new LinearLayoutManager(this));
        candidateRV.setAdapter(adapter);

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
                                            snapshot.getString("post"),
                                            snapshot.getString("image"),
                                            snapshot.getId(),
                                            snapshot.getString("electionId")
                                              //itwill get document id
                                    ));
                                }
                                adapter.notifyDataSetChanged();
                            }else{
                                Toast.makeText(AllCandidateActivity.this, "Candidate not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firebaseFirestore.collection("Users")
                .document(uid)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        List<String> voteList = (List<String>)(documentSnapshot.get("votelist"));
                        if (voteList.contains(electionId)) {
                            Toast.makeText(AllCandidateActivity.this, "You have already voted for this election.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AllCandidateActivity.this, ElectionActivity.class));
                            finish();
                        }

                    }
                });

    }
}