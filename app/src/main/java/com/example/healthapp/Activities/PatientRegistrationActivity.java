package com.example.healthapp.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PatientRegistrationActivity extends AppCompatActivity {

    private TextView regPageQtn;
    private TextInputEditText regFullname, regpnumber, regLocation, regEmail, regPassword;
    private Button regButton;

    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseRef;
    private ProgressDialog loader;
    private String currentUserId;

    private CircleImageView profile_image;
    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_registration);

        regPageQtn = findViewById(R.id.regPageQtn);
        regPageQtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientRegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        regFullname = findViewById(R.id.regFullname);
        regpnumber = findViewById(R.id.regpnumber);
        regLocation = findViewById(R.id.regLocation);
        regEmail = findViewById(R.id.regEmail);
        regPassword = findViewById(R.id.regPassword);
        regButton = findViewById(R.id.regButton);
        profile_image = findViewById(R.id.profile_image);

        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Ensuring fields are not empty
                final String email = regEmail.getText().toString().trim();
                final String password = regPassword.getText().toString().trim();
                final String fullName = regFullname.getText().toString().trim();
                final String phoneNumber = regpnumber.getText().toString().trim();
                final String location = regLocation.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    regEmail.setError("Email is required!");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    regEmail.setError("Password is required!");
                    return;
                }
                if (TextUtils.isEmpty(fullName)){
                    regEmail.setError("Full Name is required!");
                    return;
                }
                if (TextUtils.isEmpty(phoneNumber)){
                    regEmail.setError("Phone Number is required!");
                    return;
                }
                if (TextUtils.isEmpty(location)){
                    regEmail.setError("Location is required!");
                    return;
                }
                if (resultUri ==null){
                    Toast.makeText(PatientRegistrationActivity.this, "Your profile Image is required!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    loader.setMessage("Registration in progress...");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    //registering the user
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(!task.isSuccessful()){
                                        String error = task.getException().toString();
                                        Toast.makeText(PatientRegistrationActivity.this,"Error Occured" + error, Toast.LENGTH_SHORT).show();
                                        loader.dismiss();
                                    }
                                    else {
                                        currentUserId = mAuth.getCurrentUser().getUid();
                                        userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);

                                        HashMap userInfo = new HashMap();
                                        userInfo.put("id", currentUserId);
                                        userInfo.put("name", fullName);
                                        userInfo.put("email", email);
                                        userInfo.put("phonenumber", phoneNumber);
                                        userInfo.put("location", location);
                                        userInfo.put("type", "patient");

                                        //saving to firebase
                                        userDatabaseRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(PatientRegistrationActivity.this, "Details Set Successfully", Toast.LENGTH_SHORT).show();
                                                }
                                                else {
                                                    Toast.makeText(PatientRegistrationActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                }
                                                finish();
                                                loader.dismiss();
                                            }
                                        });

                                        if (resultUri !=null) {
                                            final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profile pictures").child(currentUserId);
                                            Bitmap bitmap = null;
                                            try {
                                                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
                                            } catch (IOException e){
                                                e.printStackTrace();
                                            }
                                            ByteArrayOutputStream byteArrayOutputStStream = new ByteArrayOutputStream();
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 20,byteArrayOutputStStream);
                                            byte[] data = byteArrayOutputStStream.toByteArray();
                                            UploadTask uploadTask = filepath.putBytes(data);

                                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    finish();
                                                    return;
                                                }
                                            });

                                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    if (taskSnapshot.getMetadata() !=null) {
                                                        if (taskSnapshot.getMetadata().getReference() !=null){
                                                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri uri) {
                                                                    String imageUrl = uri.toString();
                                                                    Map newImageMap = new HashMap();
                                                                    newImageMap.put("profilepictureurl", imageUrl);
                                                                    userDatabaseRef.updateChildren(newImageMap).addOnCompleteListener(new OnCompleteListener() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task task) {
                                                                            if (task.isSuccessful()){
                                                                                Toast.makeText(PatientRegistrationActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                                                            } else {
                                                                                String error = task.getException().toString();
                                                                                Toast.makeText(PatientRegistrationActivity.this, "Process failed "+ error, Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                                    finish();
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            });

                                            Intent intent = new Intent(PatientRegistrationActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                            loader.dismiss();
                                        }
                                    }
                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==1 && resultCode == Activity.RESULT_OK ){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            profile_image.setImageURI(resultUri);
        }
    }
}
