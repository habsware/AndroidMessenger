package com.habsware.messenger;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference ref;
    private StorageReference imageStorageReference;
    private String currentUserId;
    private Button saveSettingsButton;
    private EditText userNameEditText, fullNameEditText, phoneNumEditText, statusEditText;
    private CircleImageView profilePic;
    private Toolbar settingsToolbar;
    private ProgressDialog progressDialog;
    private static final int PhoneGallery = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();
        imageStorageReference = FirebaseStorage.getInstance().getReference().child("User Profile Images");
        currentUserId = auth.getCurrentUser().getUid();
        saveSettingsButton = findViewById(R.id.saveSettingsButton);
        userNameEditText = findViewById(R.id.userNameEditText);
        fullNameEditText = findViewById(R.id.fullNameEditText);
        phoneNumEditText = findViewById(R.id.phoneNumEditText);
        statusEditText = findViewById(R.id.statusEditText);
        profilePic = findViewById(R.id.profile_image);
        progressDialog = new ProgressDialog(this);

        settingsToolbar = findViewById(R.id.settingsToolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Account Settings");

        userNameEditText.setVisibility(View.INVISIBLE);

        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });

        getUserInfo();

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressDialog.setTitle("Profile Picture");
                progressDialog.setMessage("Picture is uploading..");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                final Uri resultUri = result.getUri();
                final StorageReference filePath = imageStorageReference.child(currentUserId + ".jpg");
                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadUrl = uri.toString();
                                ref.child("Users").child(currentUserId).child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SettingsActivity.this, "Profile image stored successfully.", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        } else {
                                            String errorMessage = task.getException().getMessage();
                                            Toast.makeText(SettingsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }
    }

            public void displayUserNamePoliticsWarning () {

                new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
                        .setTitle("User Name Politics")
                        .setMessage("Welcome! You're about to provide your personal information. Please note that," +
                                " your user name can not be changed. This is used to explicitly identify your account. Make sure to take the preferred decision!")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }

            private void getUserInfo () {
                ref.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        setSettingsFieldsFromDb(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

            private void setSettingsFieldsFromDb (@NonNull DataSnapshot dataSnapshot){
                if (dataSnapshot.exists() && dataSnapshot.hasChild("fullName") && dataSnapshot.hasChild("userName")
                        && dataSnapshot.hasChild("phoneNum") && dataSnapshot.hasChild("statusMode") && dataSnapshot.hasChild("image")) {

                    String getUserName = dataSnapshot.child("userName").getValue().toString();
                    String getFullName = dataSnapshot.child("fullName").getValue().toString();
                    String getPhoneNum = dataSnapshot.child("phoneNum").getValue().toString();
                    String getStatusMode = dataSnapshot.child("statusMode").getValue().toString();
                    String getImage = dataSnapshot.child("image").getValue().toString();
                    userNameEditText.setText(getUserName);
                    fullNameEditText.setText(getFullName);
                    phoneNumEditText.setText(getPhoneNum);
                    statusEditText.setText(getStatusMode);
                    Picasso.get().load(getImage).into(profilePic);
                }
                if (dataSnapshot.exists() && dataSnapshot.hasChild("fullName") && dataSnapshot.hasChild("userName")
                        && dataSnapshot.hasChild("phoneNum") && dataSnapshot.hasChild("statusMode")) {

                    String getUserName = dataSnapshot.child("userName").getValue().toString();
                    String getFullName = dataSnapshot.child("fullName").getValue().toString();
                    String getPhoneNum = dataSnapshot.child("phoneNum").getValue().toString();
                    String getStatusMode = dataSnapshot.child("statusMode").getValue().toString();
                    userNameEditText.setText(getUserName);
                    fullNameEditText.setText(getFullName);
                    phoneNumEditText.setText(getPhoneNum);
                    statusEditText.setText(getStatusMode);
                } else if (dataSnapshot.exists() && dataSnapshot.hasChild("fullName") && dataSnapshot.hasChild("userName")
                        && dataSnapshot.hasChild("phoneNum")) {

                    String getUserName = dataSnapshot.child("userName").getValue().toString();
                    String getFullName = dataSnapshot.child("fullName").getValue().toString();
                    String getPhoneNum = dataSnapshot.child("phoneNum").getValue().toString();
                    userNameEditText.setText(getUserName);
                    fullNameEditText.setText(getFullName);
                    phoneNumEditText.setText(getPhoneNum);
                } else if (dataSnapshot.exists() && dataSnapshot.hasChild("fullName") && dataSnapshot.hasChild("userName")) {

                    String getUserName = dataSnapshot.child("userName").getValue().toString();
                    String getFullName = dataSnapshot.child("fullName").getValue().toString();
                    userNameEditText.setText(getUserName);
                    fullNameEditText.setText(getFullName);
                } else if (dataSnapshot.exists() && dataSnapshot.hasChild("userName")) {

                    String getUserName = dataSnapshot.child("userName").getValue().toString();
                    userNameEditText.setText(getUserName);
                } else {
                    userNameEditText.setVisibility(View.VISIBLE);
                    Toast.makeText(SettingsActivity.this, "Please update your personal information", Toast.LENGTH_SHORT).show();
                }
                if (!dataSnapshot.hasChild("userName"))
                    displayUserNamePoliticsWarning();
            }

            private void saveSettings () {
                String userName = userNameEditText.getText().toString().trim();
                String fullName = fullNameEditText.getText().toString().trim();
                String phoneNum = phoneNumEditText.getText().toString().trim();
                String status = statusEditText.getText().toString().trim();

                if (fullName.isEmpty()) {
                    Toast.makeText(this, "Please enter your full name before proceeding", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (status.isEmpty()) {
                    Toast.makeText(this, "Please provide a state for your presence status", Toast.LENGTH_SHORT).show();
                    return;
                }
                //UserInfo user = new UserInfo(userName, fullName, phoneNum, status);
                HashMap<String, Object> userMap = new HashMap<>();
                userMap.put("userName",userName);
                userMap.put("fullName",fullName);
                userMap.put("phoneNum",phoneNum);
                userMap.put("statusMode", status);
                ref.child("Users").child(currentUserId).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            redirectUserToMainActivity();
                            Toast.makeText(SettingsActivity.this, "Your information updated successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(SettingsActivity.this, errorMessage, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
            }

            private void redirectUserToMainActivity () {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }
