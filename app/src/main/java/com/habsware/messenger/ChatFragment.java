package com.habsware.messenger;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private FirebaseAuth auth;
    private DatabaseReference currentUserContactsRef, usersRef,chatsRef;
    private String currentUserId;
    private View chatView;
    private RecyclerView chatRecyclerView;
    private FloatingActionButton createChatButton;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        currentUserContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        chatView = inflater.inflate(R.layout.fragment_chat, container, false);

        chatRecyclerView = chatView.findViewById(R.id.chatFragmentRecyclerView);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        createChatButton = chatView.findViewById(R.id.createChatActionButton);

        return chatView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(currentUserContactsRef, Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, ChatFragmentViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ChatFragmentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatFragmentViewHolder holder, int position, @NonNull final Contacts model) {
                final String usersIds = getRef(position).getKey();


            }

            @NonNull
            @Override
            public ChatFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.display_users_layout,viewGroup,false);
                return new ChatFragmentViewHolder(view);
            }
        };
        chatRecyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    public static class ChatFragmentViewHolder extends RecyclerView.ViewHolder{

        TextView userName, statusMode;
        CircleImageView image;

        public ChatFragmentViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.displayUserNameTextView);
            statusMode = itemView.findViewById(R.id.displayUserStatusTextView);
            image = itemView.findViewById(R.id.displayUsersCurrentImage);

        }
    }
}
