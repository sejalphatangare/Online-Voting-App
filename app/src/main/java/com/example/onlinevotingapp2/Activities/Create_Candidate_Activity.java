package com.example.onlinevotingapp2.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.onlinevotingapp2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Create_Candidate_Activity extends AppCompatActivity {

    private CircleImageView candidateImg;
    private EditText candidateName,candidateParty;
    private Spinner electionSpinner;
    private Button submitButton;
    private Uri mainUri=null;
    private StorageReference reference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_candidate);


        reference= FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
        candidateImg=findViewById(R.id.candidate_image);
        candidateName=findViewById(R.id.candidate_name);
        candidateParty=findViewById(R.id.candidate_party_name);

        electionSpinner=findViewById(R.id.election_spinner);
        submitButton=findViewById(R.id.candidate_submit_btn);

        fetchElectionsAndPopulateSpinner();

        candidateImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImage();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=candidateName.getText().toString().trim();
                String party=candidateParty.getText().toString().trim();
                String electionId=electionSpinner.getSelectedItem().toString();


                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(party)  && mainUri!=null){


                    String newCandidateDocumentId = firebaseFirestore.collection("Candidate").document().getId();

                    String uid= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                    StorageReference imagePath=reference.child("candidate_img").child(newCandidateDocumentId +".jpg");
                    imagePath.putFile(mainUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        Map<String,Object> map= new HashMap<>();
                                        map.put("name",name);
                                        map.put("party",party);
                                        map.put("image",uri.toString());
                                        map.put("electionId",electionId);
                                        map.put("timestamp", FieldValue.serverTimestamp());

                                        firebaseFirestore.collection("Candidate")
                                                .document(newCandidateDocumentId)
                                                .set(map)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            startActivity(new Intent(Create_Candidate_Activity.this,HomeActivity.class));
                                                            finish();
                                                        }else{
                                                            Toast.makeText(Create_Candidate_Activity.this, "Data not stored", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                    }
                                });
                            }else{
                                Toast.makeText(Create_Candidate_Activity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else{
                    Toast.makeText(Create_Candidate_Activity.this, "Enter the credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void cropImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this);


    }
    private void fetchElectionsAndPopulateSpinner() {
        // Fetch the list of elections from Firestore
        firebaseFirestore.collection("Elections")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> electionNames = new ArrayList<>();
                            for (DocumentSnapshot snapshot : task.getResult()) {
                                String electionName = snapshot.getString("name");
                                electionNames.add(electionName);
                            }
                            // Create an ArrayAdapter and set it to the electionSpinner
                            ArrayAdapter<String> electionAdapter = new ArrayAdapter<>(
                                    Create_Candidate_Activity.this,
                                    android.R.layout.simple_dropdown_item_1line,
                                    electionNames
                            );
                            electionSpinner.setAdapter(electionAdapter);
                        } else {
                            Toast.makeText(Create_Candidate_Activity.this, "Elections not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                mainUri = result.getUri();
                candidateImg.setImageURI(mainUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}