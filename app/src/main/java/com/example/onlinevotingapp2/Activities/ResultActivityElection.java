package com.example.onlinevotingapp2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.onlinevotingapp2.Adapter.ElectionAdapter;
import com.example.onlinevotingapp2.Adapter.ElectionResultAdapter;
import com.example.onlinevotingapp2.Model.Election;
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

public class ResultActivityElection extends AppCompatActivity {
    private RecyclerView elecresultRecyclerView;
    private List<Election> electionsList;
    private ElectionResultAdapter electionResultAdapter;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_election);
        elecresultRecyclerView=findViewById(R.id.recyclerview_result);
        elecresultRecyclerView.setHasFixedSize(true);
        elecresultRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        electionsList = new ArrayList<Election>();
        electionResultAdapter = new ElectionResultAdapter(this, electionsList);
        firestore=FirebaseFirestore.getInstance();
        elecresultRecyclerView.setAdapter(electionResultAdapter);

        elecresultRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                View childView = rv.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && electionResultAdapter != null) {
                    int position = rv.getChildAdapterPosition(childView);
                    if (position != RecyclerView.NO_POSITION) {
                        // Get the selected election
                        Election selectedElection = electionsList.get(position);


                        Intent intent = new Intent(ResultActivityElection.this, ResultActivity.class);

                        Log.d("Election","selected : "+selectedElection);

                        startActivity(intent);
                    }
                }

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        // Load and display the list of elections
        loadElections();

    }

    private void loadElections() {
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            firestore.collection("Elections")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for(DocumentSnapshot snapshot: Objects.requireNonNull(task.getResult())){

                                    Log.d("ElectionActivity", "Election name: " + snapshot.getString("name"));
                                    // Add election data to the list
                                    electionsList.add(new Election(
                                            snapshot.getString("name"),
                                            snapshot.getString("start_date"),
                                            snapshot.getString("end_date"),
                                            snapshot.getString("creator"),
                                            snapshot.getId()   //it will get document id
                                    ));
                                }
                                electionResultAdapter.notifyDataSetChanged();
                            } else {
                                Log.e("ElectionsActivity", "Error fetching elections: ", task.getException());
                                Toast.makeText(ResultActivityElection.this, "Failed to load elections", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }




    }
}