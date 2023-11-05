package com.example.onlinevotingapp2.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.onlinevotingapp2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileActivity extends AppCompatActivity {


    private EditText nameTxt,emailTxt,aadharnoTxt;
    private CircleImageView image;
    private Button updateBtn;
    private Uri mainUri=null;
    private String uid;
    private StorageReference reference;
    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        firebaseFirestore = FirebaseFirestore.getInstance();
        reference= FirebaseStorage.getInstance().getReference();

        nameTxt=findViewById(R.id.profilename);
        emailTxt=findViewById(R.id.profileemail);
        aadharnoTxt=findViewById(R.id.profileaadharno);
        updateBtn=findViewById(R.id.profile_update_btn);
        image=findViewById(R.id.profile_image);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        uid=user.getUid();

        loadProfileDetails();

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImage();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newname=nameTxt.getText().toString().trim();
                String newemail=emailTxt.getText().toString().trim();
                String newaadharno=aadharnoTxt.getText().toString().trim();


                if(!TextUtils.isEmpty(newname) && !TextUtils.isEmpty(newemail) && !TextUtils.isEmpty(newaadharno)  &&  mainUri!=null){

                    StorageReference imagePath=reference.child("image_profile").child( uid+".jpg");
                    imagePath.putFile(mainUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        Map<String,Object> map= new HashMap<>();
                                        map.put("name",newname);
                                        map.put("email",newemail);
                                        map.put("image",uri.toString());
                                        map.put("aadharno",newaadharno);
                                        map.put("timestamp", FieldValue.serverTimestamp());


                                        firebaseFirestore.collection("Users")
                                                .document(uid)
                                                .set(map)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){

                                                            startActivity(new Intent(UpdateProfileActivity.this,ProfileActivity.class));
                                                            finish();
                                                        }else{
                                                            Toast.makeText(UpdateProfileActivity.this, "Data not stored", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                    }
                                });
                            }else{
                                Toast.makeText(UpdateProfileActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else{
                    Toast.makeText(UpdateProfileActivity.this, "Enter the credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });







    }

    private void loadProfileDetails() {
        firebaseFirestore.collection("Users")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            String img=documentSnapshot.getString("image");
                            String nm=documentSnapshot.getString("name");
                            String em=documentSnapshot.getString("email");
                            String aadhar_no=documentSnapshot.getString("aadharno");

                            nameTxt.setText(nm);
                            emailTxt.setText(em);
                            aadharnoTxt.setText(aadhar_no);

                            Log.d("ImageURL", "Image URL: " + image);
                            Glide.with(UpdateProfileActivity.this).load(img).into(image);


                        }else{
                            Toast.makeText(UpdateProfileActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                            finish();
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