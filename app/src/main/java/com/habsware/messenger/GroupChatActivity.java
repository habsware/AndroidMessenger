package com.habsware.messenger;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference userRef, groupRef, groupMessageIdRef;
    private Toolbar toolbar;
    private ImageButton sendMessageButton;
    private EditText messageEditText;
    private ScrollView scrollView;
    private TextView messageTextView;
    private String currentGroup, currentUserId, currentUserName, currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentGroup = getIntent().getExtras().get("groupName").toString();

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroup);
        toolbar = findViewById(R.id.groupChat_layoutBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentGroup);

        sendMessageButton = findViewById(R.id.sendMessageButton);
        messageEditText = findViewById(R.id.groupMessage);
        messageTextView =findViewById(R.id.groupChatTextView);
        scrollView = findViewById(R.id.groupChatScrollView);

        getUserInfoFromDb();

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                messageEditText.setText("");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        groupRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists())
                    displayExistingMessages(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists())
                    displayExistingMessages(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayExistingMessages(DataSnapshot dataSnapshot) {

        messageTextView.setVisibility(View.VISIBLE);

        Iterator iterator = dataSnapshot.getChildren().iterator();

        while(iterator.hasNext()){
            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String messageText = (String) ((DataSnapshot)iterator.next()).getValue();
            String currentUserName = (String) ((DataSnapshot)iterator.next()).getValue();
            messageTextView.append(currentUserName + ": " + messageText + "\n" + chatDate + "\n\n");
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }

    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();
        String messageId = groupRef.push().getKey();

        if (messageText.isEmpty()) {
            Toast.makeText(this, "Please type in a message before sending", Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar date =  Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
        currentDate = format.format(date.getTime());

        groupMessageIdRef = groupRef.child(messageId);

        Message messageInfo = new Message(messageText, currentUserName, currentDate);
        groupMessageIdRef.setValue(messageInfo);


    }

    private void getUserInfoFromDb() {
        userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    currentUserName = dataSnapshot.child("userName").getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
