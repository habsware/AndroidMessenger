package com.habsware.messenger;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference ref;
    private String currentUserId;
    Button saveSettingsButton;
    EditText userNameEditText, fullNameEditText, phoneNumEditText, statusEditText;
    CircleImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();
        currentUserId = auth.getCurrentUser().getUid();
        saveSettingsButton = findViewById(R.id.saveSettingsButton);
        userNameEditText = findViewById(R.id.userNameEditText);
        fullNameEditText = findViewById(R.id.fullNameEditText);
        phoneNumEditText = findViewById(R.id.phoneNumEditText);
        statusEditText = findViewById(R.id.statusEditText);
        profilePic = findViewById(R.id.profile_image);

        userNameEditText.setVisibility(View.INVISIBLE);

        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });

        getUserInfo();
    }

    public void displayUserNamePoliticsWarning(){

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

    private void getUserInfo() {
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

    private void setSettingsFieldsFromDb(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists() && dataSnapshot.hasChild("fullName") && dataSnapshot.hasChild("userName")
                && dataSnapshot.hasChild("phoneNum") && dataSnapshot.hasChild("statusMode")){

            String getUserName = dataSnapshot.child("userName").getValue().toString();
            String getFullName = dataSnapshot.child("fullName").getValue().toString();
            String getPhoneNum = dataSnapshot.child("phoneNum").getValue().toString();
            String getStatusMode = dataSnapshot.child("statusMode").getValue().toString();
            userNameEditText.setText(getUserName);
            fullNameEditText.setText(getFullName);
            phoneNumEditText.setText(getPhoneNum);
            statusEditText.setText(getStatusMode);
        }
        else if (dataSnapshot.exists() && dataSnapshot.hasChild("fullName") && dataSnapshot.hasChild("userName")
                && dataSnapshot.hasChild("phoneNum")){

            String getUserName = dataSnapshot.child("userName").getValue().toString();
            String getFullName = dataSnapshot.child("fullName").getValue().toString();
            String getPhoneNum = dataSnapshot.child("phoneNum").getValue().toString();
            userNameEditText.setText(getUserName);
            fullNameEditText.setText(getFullName);
            phoneNumEditText.setText(getPhoneNum);
        }
        else if (dataSnapshot.exists() && dataSnapshot.hasChild("fullName") && dataSnapshot.hasChild("userName")){

            String getUserName = dataSnapshot.child("userName").getValue().toString();
            String getFullName = dataSnapshot.child("fullName").getValue().toString();
            userNameEditText.setText(getUserName);
            fullNameEditText.setText(getFullName);
        }
        else if (dataSnapshot.exists() && dataSnapshot.hasChild("userName")){

            String getUserName = dataSnapshot.child("userName").getValue().toString();
            userNameEditText.setText(getUserName);
        }
        else
            userNameEditText.setVisibility(View.VISIBLE);
            Toast.makeText(SettingsActivity.this, "Please update your personal information", Toast.LENGTH_SHORT).show();

        if (!dataSnapshot.hasChild("userName"))
            displayUserNamePoliticsWarning();
    }

    private void saveSettings() {
        String userName = userNameEditText.getText().toString().trim();
        String fullName = fullNameEditText.getText().toString().trim();
        String phoneNum = phoneNumEditText.getText().toString().trim();
        String status = statusEditText.getText().toString().trim();

        if (fullName.isEmpty()){
            Toast.makeText(this, "Please enter your full name before proceeding", Toast.LENGTH_SHORT).show();
            return;
        }
        if (status.isEmpty()) {
            Toast.makeText(this, "Please provide a state for your presence status", Toast.LENGTH_SHORT).show();
            return;
        }
        UserInfo user = new UserInfo(userName, fullName,phoneNum, status);
        ref.child("Users").child(currentUserId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    redirectUserToMainActivity();
                    Toast.makeText(SettingsActivity.this, "Your information updated successfully!", Toast.LENGTH_SHORT).show();
                }
               else{
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(SettingsActivity.this, errorMessage, Toast.LENGTH_SHORT)
                            .show();
               }
            }
        });
    }

    private void redirectUserToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
