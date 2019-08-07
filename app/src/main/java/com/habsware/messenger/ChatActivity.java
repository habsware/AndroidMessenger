package com.habsware.messenger;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference messagesRef;
    private String selectedUserId, selectedUserName, selectedUserImage, currentUserId;
    private TextView userNameTextView, lastSeenTextView;
    private EditText messageContentEditText;
    private ImageButton sendMessageButton;
    private CircleImageView userImage;
    private Toolbar chatToolbar;
    final private List<Message> messageList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView messagesRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        messagesRef = FirebaseDatabase.getInstance().getReference().child("Messages");

        selectedUserId = getIntent().getExtras().get("selectedUserId").toString();
        selectedUserName = getIntent().getExtras().get("selectedUserName").toString();
        selectedUserImage = getIntent().getExtras().get("selectedUserImage").toString();

        chatToolbar = findViewById(R.id.chatActivityToolbar_chatActivity);
        setSupportActionBar(chatToolbar);
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowHomeEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View barView = layoutInflater.inflate(R.layout.chat_toolbar, null);
        chatToolbar.addView(barView);

        userNameTextView = findViewById(R.id.chatDisplayUserNameTextView);
        lastSeenTextView = findViewById(R.id.chatLastSeenTextView);
        userImage = findViewById(R.id.chatUserImage);
        sendMessageButton = findViewById(R.id.sendMessageImageButton_chatActivity);
        messageContentEditText = findViewById(R.id.messageEditText_chatActivity);
        messageAdapter = new MessageAdapter(messageList);
        messagesRecyclerView = findViewById(R.id.chatRecyclerView_chatActivity);
        linearLayoutManager = new LinearLayoutManager(this);
        messagesRecyclerView.setLayoutManager(linearLayoutManager);
        messagesRecyclerView.setAdapter(messageAdapter);

        userNameTextView.setText(selectedUserName);
        Picasso.get().load(selectedUserImage).placeholder(R.drawable.male_image).into(userImage);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        messagesRef.child(currentUserId).child(selectedUserId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message = dataSnapshot.getValue(Message.class);
                messageList.add(message);
                messageAdapter.notifyDataSetChanged();
                messagesRecyclerView.smoothScrollToPosition(messagesRecyclerView.getAdapter().getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

    private void sendMessage(){
        String messageContent = messageContentEditText.getText().toString().trim();

        if (messageContent.isEmpty()) {
            Toast.makeText(this, "Please type in a message before sending", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference messageKeyRef = messagesRef.child(currentUserId).child(selectedUserId).push();
        final String messageKey = messageKeyRef.getKey();

        String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(Calendar.getInstance().getTime());
        final Message message = new Message(messageContent, currentUserId, selectedUserId, timeStamp);

        messagesRef.child(currentUserId).child(selectedUserId).child(messageKey).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    messagesRef.child(selectedUserId).child(currentUserId).child(messageKey).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                            }
                        }
                    });
                }
            }
        });
        messageContentEditText.setText("");
    }
}
