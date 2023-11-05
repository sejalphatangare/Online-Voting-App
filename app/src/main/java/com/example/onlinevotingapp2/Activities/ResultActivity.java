package com.example.onlinevotingapp2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlinevotingapp2.Adapter.CandidateAdapter;
import com.example.onlinevotingapp2.Adapter.CandidateResultAdapter;
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
import java.util.List;
import java.util.Objects;

public class ResultActivity extends AppCompatActivity {

    private RecyclerView resultRV;
    private List<Candidate> list;
    private CandidateResultAdapter adapter;
    private FirebaseFirestore firebaseFirestore;
    private TextView warningtxt;
    private String electionId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultRV=findViewById(R.id.result_rv);
        warningtxt=findViewById(R.id.warning_text);
        firebaseFirestore=FirebaseFirestore.getInstance();


        list=new ArrayList<>();
        adapter=new CandidateResultAdapter(ResultActivity.this,list);
        resultRV.setLayoutManager(new LinearLayoutManager(ResultActivity.this));
        resultRV.setAdapter(adapter);

        Intent intent=getIntent();
        electionId=intent.getStringExtra("ele_name");
        Log.d("ResultActivity","ele_name"+electionId);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            loadCandidateForElection(electionId);

        }
    }

    private void loadCandidateForElection(String electionId) {
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
                                Toast.makeText(ResultActivity.this, "Candidate not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();

        firebaseFirestore.collection("Users")
                .document(uid)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        List<String> voteList = (List<String>)(documentSnapshot.get("votelist"));
                        if(Objects.equals(documentSnapshot.get("name"), "admin")){
                            resultRV.setVisibility(View.VISIBLE);
                            warningtxt.setVisibility(View.GONE);
                        }
                        else if (voteList.contains(electionId)) {
                            resultRV.setVisibility(View.VISIBLE);
                            warningtxt.setVisibility(View.GONE);
                        }else{
                            resultRV.setVisibility(View.GONE);
                            warningtxt.setVisibility(View.VISIBLE);
                        }

                    }
                });
    }
}