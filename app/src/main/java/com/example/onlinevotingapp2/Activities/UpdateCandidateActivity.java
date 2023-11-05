package com.example.onlinevotingapp2.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.bumptech.glide.Glide;
import com.example.onlinevotingapp2.Model.Candidate;
import com.example.onlinevotingapp2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
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

public class UpdateCandidateActivity extends AppCompatActivity {

    private String candidateId;
    private EditText name,party;
    private Spinner electionSpinner;
    private Button updateBtn;
    private CircleImageView image;
    private Uri mainUri=null;
    private StorageReference reference;
    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_candidate);


        candidateId=getIntent().getStringExtra("id");
        Log.e("UpdateCandidateActivity","candidateId: "+candidateId);
        firebaseFirestore = FirebaseFirestore.getInstance();
        reference= FirebaseStorage.getInstance().getReference();

        name=findViewById(R.id.candidate_name);
        party=findViewById(R.id.candidate_party_name);
        image=findViewById(R.id.candidate_image);
        electionSpinner=findViewById(R.id.election_spinner);
        updateBtn=findViewById(R.id.candidate_update_btn);
        fetchElectionsAndPopulateSpinner();
        loadCandidateDetails();

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImage();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newname=name.getText().toString().trim();
                String newparty=party.getText().toString().trim();
                String newelectionId=electionSpinner.getSelectedItem().toString();


                if(!TextUtils.isEmpty(newname) && !TextUtils.isEmpty(newparty)  && mainUri!=null){



                    String uid= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                    StorageReference imagePath=reference.child("candidate_img").child( candidateId+".jpg");
                    imagePath.putFile(mainUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        Map<String,Object> map= new HashMap<>();
                                        map.put("name",newname);
                                        map.put("party",newparty);
                                        map.put("image",uri.toString());
                                        map.put("electionId",newelectionId);
                                        map.put("timestamp", FieldValue.serverTimestamp());


                                        firebaseFirestore.collection("Candidate")
                                                .document(candidateId)
                                                .set(map)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){

                                                            startActivity(new Intent(UpdateCandidateActivity.this,ElectionActivity.class));
                                                            finish();
                                                        }else{
                                                            Toast.makeText(UpdateCandidateActivity.this, "Data not stored", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                    }
                                });
                            }else{
                                Toast.makeText(UpdateCandidateActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else{
                    Toast.makeText(UpdateCandidateActivity.this, "Enter the credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });





    }

    private void fetchElectionsAndPopulateSpinner() {
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
                            ArrayAdapter<String> electionAdapter = new ArrayAdapter<>(
                                    UpdateCandidateActivity.this,
                                    android.R.layout.simple_dropdown_item_1line,
                                    electionNames
                            );
                            electionSpinner.setAdapter(electionAdapter);
                        } else {
                            Toast.makeText(UpdateCandidateActivity.this, "Elections not found", Toast.LENGTH_SHORT).show();
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


    private void loadCandidateDetails() {
        firebaseFirestore.collection("Candidate")
                .document(candidateId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            String img=documentSnapshot.getString("image");
                            String nm=documentSnapshot.getString("name");
                            String partynm=documentSnapshot.getString("party");
                            String ele_name=documentSnapshot.getString("electionId");

                            name.setText(nm);
                            party.setText(partynm);

                            Log.d("ImageURL", "Image URL: " + image);
                            Glide.with(UpdateCandidateActivity.this).load(img).into(image);


                        }else{
                            Toast.makeText(UpdateCandidateActivity.this, "Candidate not found", Toast.LENGTH_SHORT).show();
                            finish();
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
                image.setImageURI(mainUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}