package com.habsware.messenger;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private FirebaseAuth auth;
    private DatabaseReference usersRef;
    private String currentUserId;
    private List<Message> messageList;

    public MessageAdapter (List<Message> messageList){
        this.messageList = messageList;
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.messages_layout, viewGroup, false);

        auth = FirebaseAuth.getInstance();


        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, int position) {

        currentUserId = auth.getCurrentUser().getUid();
        final Message messages = messageList.get(position);
        String senderId = messages.sender;
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(senderId);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("image")){
                    messages.image = dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(messages.image).placeholder(R.drawable.male_image).into(messageViewHolder.recipientImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        messageViewHolder.recipientMessageContent.setVisibility(View.INVISIBLE);
        messageViewHolder.recipientImage.setVisibility(View.INVISIBLE);
        messageViewHolder.senderMessageContent.setVisibility(View.INVISIBLE);

        if (senderId.equals(currentUserId)){
            messageViewHolder.senderMessageContent.setVisibility(View.VISIBLE);
            messageViewHolder.senderMessageContent.setBackgroundResource(R.drawable.sender_message_layout);
            messageViewHolder.senderMessageContent.setText(messages.messageText);
        }
        else{
            messageViewHolder.recipientImage.setVisibility(View.VISIBLE);
            messageViewHolder.recipientMessageContent.setVisibility(View.VISIBLE);
            messageViewHolder.recipientMessageContent.setBackgroundResource(R.drawable.recipient_message_layout);
            messageViewHolder.recipientMessageContent.setText(messages.messageText);

        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView senderMessageContent, recipientMessageContent;
        public CircleImageView recipientImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageContent = itemView.findViewById(R.id.senderMessageContent);
            recipientMessageContent = itemView.findViewById(R.id.recipientMessageContent);
            recipientImage = itemView.findViewById(R.id.recipientMessageImage);
        }
    }
}
