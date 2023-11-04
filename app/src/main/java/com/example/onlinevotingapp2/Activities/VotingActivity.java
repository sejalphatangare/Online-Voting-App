package com.example.onlinevotingapp2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.onlinevotingapp2.Adapter.CandidateAdapter;
import com.example.onlinevotingapp2.Model.Candidate;
import com.example.onlinevotingapp2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class VotingActivity extends AppCompatActivity {

    private CircleImageView image;
    private TextView name, party;
    private CardView cardView;
    private Button voteBtn,updateBtn,deleteBtn;
    private CandidateAdapter adapter;
    private FirebaseFirestore firebaseFirestore;
    List<Candidate> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);

        firebaseFirestore = FirebaseFirestore.getInstance();

        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        party = findViewById(R.id.party);
        voteBtn = findViewById(R.id.vote_btn);
        updateBtn = findViewById(R.id.update_btn);
        deleteBtn = findViewById(R.id.delete_btn);

        list = new ArrayList<>();

        adapter = new CandidateAdapter(VotingActivity.this, list);

        String url = getIntent().getStringExtra("image");
        String nm = getIntent().getStringExtra("name");
        String part = getIntent().getStringExtra("party");
        String ele_name = getIntent().getStringExtra("ele_name");
        String id = getIntent().getStringExtra("id");


        Glide.with(this).load(url).into(image);
        name.setText(nm);
        party.setText(part);

        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();


        firebaseFirestore.collection("Candidate")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String name = document.getString("name");
                                String party = document.getString("party");
                                String image = document.getString("image");
                                String id = document.getId(); // Candidate document ID

                                // Create a Candidate object and add it to the list
                                Candidate candidate = new Candidate(name, party, image, id, ele_name);
                                list.add(candidate);
                            }

                            adapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
                        } else {
                            Toast.makeText(VotingActivity.this, "Failed to fetch candidate data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        firebaseFirestore.collection("Users")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                String userType = document.getString("name"); // Assuming "userType" is the field name

                                if (userType != null && userType.equals("admin")) {
                                    voteBtn.setVisibility(View.GONE);
                                    updateBtn.setVisibility(View.VISIBLE);
                                    deleteBtn.setVisibility(View.VISIBLE);

                                } else {
                                    voteBtn.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            // Handle the case where the document doesn't exist or there's an error
                        }
                    }
                });



        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Candidate")
                        .document(id) // Use the candidate's document ID
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                for (Candidate candidate : list) {
                                    if (candidate.getId().equals(id)) {
                                        list.remove(candidate);
                                        startActivity(new Intent(VotingActivity.this,ElectionActivity.class));
                                        break;
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(VotingActivity.this, "Cannot be Deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


        voteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("VotingActivity", "Vote button clicked");
                String finish = "voted";
                firebaseFirestore.collection("Users")
                        .document(uid).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.contains("votelist")) {
                                    firebaseFirestore.collection("Users")
                                            .document(uid)
                                            .update("votelist", FieldValue.arrayUnion(ele_name))
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        startActivity(new Intent(VotingActivity.this, ElectionActivity.class));
                                                        finish();
                                                    } else {
                                                        Toast.makeText(VotingActivity.this, "Voted Succesfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Map<String, Object> votelistMap = new HashMap<>();
                                    votelistMap.put("votelist", Collections.singletonList(ele_name));
                                    firebaseFirestore.collection("Users")
                                            .document(uid)
                                            .set(votelistMap, SetOptions.merge())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        startActivity(new Intent(VotingActivity.this, ElectionActivity.class));
                                                        finish();
                                                    } else {
                                                        Toast.makeText(VotingActivity.this, "Failed to create votelist", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        });


                Map<String, Object> candidateMap = new HashMap<>();
                candidateMap.put("deviceIp", getDeviceIP());
                candidateMap.put("timestamp", FieldValue.serverTimestamp());

                firebaseFirestore.collection("Candidate/" + id + "/Vote")
                        .document(uid)
                        .set(candidateMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(VotingActivity.this, ElectionActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(VotingActivity.this, "Voted Succesfully", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        });
    }


    private String getDeviceIP() {
        try{
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();){
                NetworkInterface inf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = inf.getInetAddresses(); enumIpAddr.hasMoreElements();){
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if(!inetAddress.isLoopbackAddress()){
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(VotingActivity.this, ""+e, Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}
