package com.habsware.messenger;

import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SelectedUserProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference usersRef, requestsRef, contactsRef, notificationsRef;
    private String selectedUserId, currentUserId, currentState="New";
    private Toolbar toolbar;
    private CircleImageView selectedUserImage;
    private TextView selectedUserFullName;
    private TextView selectedUserStatusMode;
    private Button selectedUserRequestButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_user_profile_avtivity);

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        requestsRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        notificationsRef = FirebaseDatabase.getInstance().getReference().child("Notifications");

        selectedUserId = getIntent().getExtras().get("selectedUserId").toString();

        toolbar = findViewById(R.id.userProfileToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(selectedUserId);

        selectedUserImage = findViewById(R.id.userProfileImageView);
        selectedUserFullName = findViewById(R.id.userProfileFullNameTextView);
        selectedUserStatusMode = findViewById(R.id.userProfileStatusTextView);
        selectedUserRequestButton = findViewById(R.id.userProfileRequestButton);

         getSelectedUserInfo();
    }

    private void getSelectedUserInfo(){
        usersRef.child(selectedUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserInfo userInfo = new UserInfo();
                if (dataSnapshot.exists() && dataSnapshot.child("image").exists()){
                    userInfo.fullName = dataSnapshot.child("fullName").getValue().toString();
                    userInfo.statusMode = dataSnapshot.child("statusMode").getValue().toString();
                    userInfo.image = dataSnapshot.child("image").getValue().toString();
                    displayRetrievedUserInfo(userInfo);
                    requestManager();
                }
                else{
                    userInfo.fullName = dataSnapshot.child("fullName").getValue().toString();
                    userInfo.statusMode = dataSnapshot.child("statusMode").getValue().toString();
                    displayRetrievedUserInfo(userInfo);
                    requestManager();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void requestManager() {

        requestsRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(selectedUserId)){
                    String state = dataSnapshot.child(selectedUserId).child("state").getValue().toString();
                    if (state.equals("requested")){
                        currentState = "Sent";
                        selectedUserRequestButton.setText("Cancel Request");
                        selectedUserRequestButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelRequest();
                                currentState = "New";
                            }
                        });
                    }
                }
                contactsRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(selectedUserId)){
                            currentState = "Friends";
                            selectedUserRequestButton.setText("Remove Contact");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (currentUserId.equals(selectedUserId))
            selectedUserRequestButton.setVisibility(View.INVISIBLE);

        selectedUserRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedUserRequestButton.setEnabled(false);
                if (currentState.equals("New")) {
                    sendRequest();
                }
                if (currentState.equals("Sent")){
                    cancelRequest();
                }
                if (currentState.equals("Received")){
                    //acceptRequest();
                }
                if (currentState.equals("Friends")){
                    removeSelectedContact();
                }
            }
        });
    }

    private void removeSelectedContact() {
        contactsRef.child(currentUserId).child(selectedUserId).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            contactsRef.child(selectedUserId).child(currentUserId).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                selectedUserRequestButton.setEnabled(true);
                                                currentState = "New";
                                                selectedUserRequestButton.setText("Send Request");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

   /* private void acceptRequest() {
        contactsRef.child(currentUserId).child(selectedUserId).child("contacts").setValue("saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            contactsRef.child(selectedUserId).child(currentUserId).child("contacts").setValue("saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                // Delete Request when added to contacts
                                                requestsRef.child(currentUserId).child(selectedUserId).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    requestsRef.child(selectedUserId).child(currentUserId).removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()){
                                                                                        selectedUserRequestButton.setEnabled(true);
                                                                                        currentState = "Friends";
                                                                                        selectedUserRequestButton.setText("Remove Contact");
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }*/

    private void cancelRequest() {
        requestsRef.child(currentUserId).child(selectedUserId).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            requestsRef.child(selectedUserId).child(currentUserId).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                selectedUserRequestButton.setEnabled(true);
                                                currentState = "New";
                                                selectedUserRequestButton.setText("Send Request");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void sendRequest() {
        requestsRef.child(currentUserId).child(selectedUserId).child("state")
                .setValue("requested").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    requestsRef.child(selectedUserId).child(currentUserId).child("state")
                    .setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                Notification notification = new Notification(currentUserId, "friendRequest");
                                notificationsRef.child(selectedUserId).push().setValue(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            selectedUserRequestButton.setEnabled(true);
                                            selectedUserRequestButton.setText("Cancel Request");
                                        }
                                    }
                                });
                            }
                        }
                    });
            }
        });
    }

    private void displayRetrievedUserInfo(UserInfo userInfo){
        selectedUserFullName.setText(userInfo.fullName);
        selectedUserStatusMode.setText(userInfo.statusMode);
        if (userInfo.image != null)
            Picasso.get().load(userInfo.image).placeholder(R.drawable.male_image).into(selectedUserImage);
    }

}