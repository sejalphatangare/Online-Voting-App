package com.example.onlinevotingapp2.Activities;

import static com.example.onlinevotingapp2.Activities.SplashActivity.IsLogIn;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.onlinevotingapp2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.checkerframework.checker.units.qual.A;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    private CircleImageView userProfile;
    private EditText userName,userPassword,userEmail,userAadharNo;
    private Button signUpBtn;
    private Uri mainUri=null;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;

    public static final String PREFERENCES = "prefKey";
    public static final String Name="nameKey";
    public static final String Email="emailKey";
    public static final String Password="passwordKey";
    public static final String AadharNo="aadharnoKey";
    public static final String Image="imageKey";

    SharedPreferences sharedPreferences;
    String name,password,email,aadharno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        sharedPreferences= getApplicationContext().getSharedPreferences(PREFERENCES,MODE_PRIVATE);

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        findViewById(R.id.loginredirectText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        userProfile=findViewById(R.id.profile_image);
        userName=findViewById(R.id.user_Name);
        userPassword=findViewById(R.id.user_Password);
        userEmail=findViewById(R.id.user_Email);
        userAadharNo=findViewById(R.id.user_aadharno);
        signUpBtn=findViewById(R.id.signup_btn);

        mAuth=FirebaseAuth.getInstance();

        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N_MR1){
                    if(ContextCompat.checkSelfPermission(SignUpActivity.this,
                            Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SignUpActivity.this,
                                new String[]{Manifest.permission.READ_MEDIA_IMAGES},1);
                    }else{
                        cropImage();
                    }
                }else{
                    cropImage();
                }
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=userName.getText().toString().trim();
                password= userPassword.getText().toString().trim();
                email=userEmail.getText().toString().trim();
                aadharno=userAadharNo.getText().toString().trim();


                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(password)
                        && !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
                        && !TextUtils.isEmpty(aadharno)){

                    SharedPreferences.Editor pref = sharedPreferences.edit();
                    pref.putString(Name, name);
                    pref.putString(Password, password);
                    pref.putString(Email, email);
                    pref.putString(AadharNo, aadharno);
                    pref.putString(Image, mainUri.toString());
                    pref.putBoolean(IsLogIn, true);
                    pref.apply();
                    createUser(email,password);

                }else{
                    Toast.makeText(SignUpActivity.this,"Please enter your credentials",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if(mainUri!=null) {
                        // password must be 6 or greater than 6
                        uploadUserDataToDatabase(email, password);
                        uploadImageToStorage(mAuth.getUid());
                        Toast.makeText(SignUpActivity.this, "User created", Toast.LENGTH_SHORT).show();
                        verifyEmail();
                    }else{
                        Toast.makeText(SignUpActivity.this, "Profile picture is missing.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(SignUpActivity.this, "Failed Try Again!", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadUserDataToDatabase(String email, String password) {
        // Upload user data to the database (Firestore or Firebase Realtime Database)
        // You should use a Map or a data model to structure your data
        // For example, create a map containing the user's name, email, and other details
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", email);
        userData.put("password", password);
        userData.put("aadharno", aadharno);
        userData.put("image", mainUri.toString());

        // Upload the user data to the database using Firestore
        String uid = mAuth.getUid();
        assert uid != null;
        firebaseFirestore.collection("Users")
                .document(uid)
                .set(userData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Data uploaded successfully
                            Toast.makeText(SignUpActivity.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Data not stored", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void uploadImageToStorage(String uid) {
        // Create a reference to the Firebase Storage location where you want to store the image
        StorageReference imagePath = FirebaseStorage.getInstance().getReference()
                .child("image_profile")
                .child(uid + ".jpg");  // You can change the path and file name as needed

        // Upload the image to Firebase Storage
        imagePath.putFile(Uri.parse(mainUri.toString())).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    // Image uploaded successfully
                    Toast.makeText(SignUpActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                    // Optionally, you can navigate to the next screen or perform any other action
                    // after the image is uploaded.
                } else {
                    Toast.makeText(SignUpActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void verifyEmail() {
        FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        SharedPreferences.Editor pref=sharedPreferences.edit();
                        pref.putString(Name,name);
                        pref.putString(Password,password);
                        pref.putString(Email,email);
                        pref.putString(AadharNo,aadharno);
                        pref.putString(Image,mainUri.toString());
                        pref.apply();


//                        email sent

                        Toast.makeText(SignUpActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                        finish();
                    }else{
                        mAuth.signOut();
                        finish();
                    }
                }
            });
        }
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
                userProfile.setImageURI(mainUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


}