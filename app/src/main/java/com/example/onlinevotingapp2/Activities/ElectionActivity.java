//package com.example.onlinevotingapp2.Activities;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.EditText;
//
//import com.example.onlinevotingapp2.Adapter.ElectionAdapter;
//import com.example.onlinevotingapp2.Model.Election;
//import com.example.onlinevotingapp2.R;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ElectionActivity extends AppCompatActivity {
//
//    private EditText electionNameEditText, startDateEditText, endDateEditText;
//    private Button createElectionButton;
//    private RecyclerView electionRecyclerView;
//    private List<Election> electionList;
//    private ElectionAdapter electionAdapter;
//    private FirebaseFirestore firestore;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_election);
//
//
//        firestore = FirebaseFirestore.getInstance();
//
//        electionNameEditText = findViewById(R.id.electionNameEditText);
//        startDateEditText = findViewById(R.id.startDateEditText);
//        endDateEditText = findViewById(R.id.endDateEditText);
//        createElectionButton = findViewById(R.id.createElectionButton);
//        electionRecyclerView = findViewById(R.id.electionRecyclerView);
//
//        electionList = new ArrayList<>();
//        electionAdapter = new ElectionAdapter(electionList);
//        electionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        electionRecyclerView.setAdapter(electionAdapter);
//
//
//    }
//}


package com.example.onlinevotingapp2.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.onlinevotingapp2.Adapter.ElectionAdapter;
import com.example.onlinevotingapp2.Model.Candidate;
import com.example.onlinevotingapp2.Model.Election;
import com.example.onlinevotingapp2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ElectionActivity extends AppCompatActivity {
    private RecyclerView electionsRecyclerView;
    private List<Election> electionsList;
    private ElectionAdapter electionAdapter;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election);

        electionsRecyclerView = findViewById(R.id.recyclerview);
        electionsRecyclerView.setHasFixedSize(true);
        electionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        electionsList = new ArrayList<Election>();

        electionAdapter = new ElectionAdapter(this, electionsList);

        electionAdapter=new ElectionAdapter(ElectionActivity.this,electionsList);

        firestore = FirebaseFirestore.getInstance();
        electionsRecyclerView.setAdapter(electionAdapter);


        electionsRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                View childView = rv.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && electionAdapter != null) {
                    int position = rv.getChildAdapterPosition(childView);
                    if (position != RecyclerView.NO_POSITION) {
                        // Get the selected election
                        Election selectedElection = electionsList.get(position);

                        // Start the AllCandidateActivity with the selected election's ID
//                        Intent intent = new Intent(ElectionActivity.this, AllCandidateActivity.class);

                        Log.d("Election","selected : "+selectedElection);

//                        startActivity(intent);
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
                                electionAdapter.notifyDataSetChanged();
                            } else {
                                Log.e("ElectionsActivity", "Error fetching elections: ", task.getException());
                                Toast.makeText(ElectionActivity.this, "Failed to load elections", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    } // Set click listener to handle election item clicks


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
