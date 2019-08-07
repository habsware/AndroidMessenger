package com.habsware.messenger;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private FirebaseAuth auth;
    private DatabaseReference requestsRef, usersRef, contactsRef;
    private String currentUserId;
    private View requestFragmentView;
    private TextView noRequstsTextView;
    private RecyclerView requestsRecyclerView;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        requestFragmentView = inflater.inflate(R.layout.fragment_requests, container, false);

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        requestsRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        requestsRecyclerView = requestFragmentView.findViewById(R.id.requestsRecyclerView);
        noRequstsTextView = requestFragmentView.findViewById(R.id.noRequestsTextView);

        return requestFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(requestsRef.child(currentUserId), Contacts.class)
                .build();

        final FirebaseRecyclerAdapter<Contacts, RequestsFragmentViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, RequestsFragmentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestsFragmentViewHolder holder, int position, @NonNull final Contacts model) {
                final String userIds = getRef(position).getKey();
                DatabaseReference requestingState = getRef(position).child("state").getRef();
                requestingState.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String state = dataSnapshot.getValue().toString();
                        if (state.equals("requested")){
                            holder.itemView.setVisibility(View.GONE);
                        }
                        else if (state.equals("received")) {
                            usersRef.child(userIds).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.hasChild("image")) {
                                        model.userName = dataSnapshot.child("userName").getValue().toString();
                                        model.statusMode = dataSnapshot.child("statusMode").getValue().toString();
                                        holder.userName.setText(model.userName);
                                        holder.userStatus.setText(String.format("Hey, I'm %s. Lets be friends", model.userName));
                                    } else {
                                        model.userName = dataSnapshot.child("userName").getValue().toString();
                                        model.statusMode = dataSnapshot.child("statusMode").getValue().toString();
                                        model.image = dataSnapshot.child("image").getValue().toString();
                                        holder.userName.setText(model.userName);
                                        holder.userStatus.setText(String.format("Hey, I'm %s. Lets be friends", model.userName));
                                        Picasso.get().load(model.image).placeholder(R.drawable.male_image).into(holder.image);
                                    }
                                    holder.itemView.findViewById(R.id.displayUserAcceptButton).setVisibility(View.VISIBLE);

                                    // accept requests
                                    holder.itemView.findViewById(R.id.displayUserAcceptButton).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            acceptRequest(userIds);
                                        }
                                    });
                                    holder.itemView.findViewById(R.id.displayUserDeclineButton).setVisibility(View.VISIBLE);
                                    // decline requests
                                    holder.itemView.findViewById(R.id.displayUserDeclineButton).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            declineRequests(userIds);
                                        }
                                    });
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
            @NonNull
            @Override
            public RequestsFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.display_users_layout, viewGroup, false);

                return new RequestsFragmentViewHolder(view);
            }
        };
        if (adapter.getItemCount() == 0){
            noRequstsTextView.setVisibility(View.VISIBLE);
        }

        requestsRecyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private void acceptRequest(final String userIds){
        contactsRef.child(currentUserId).child(userIds).child("contact").setValue("saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            contactsRef.child(userIds).child(currentUserId).child("contact").setValue("saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                requestsRef.child(currentUserId).child(userIds).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    requestsRef.child(userIds).child(currentUserId).removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        Toast.makeText(getContext(), "User accepted and saved to your account successfully", Toast.LENGTH_LONG)
                                                                                                .show();
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
    }
    private void declineRequests(final String userIds){
        requestsRef.child(currentUserId).child(userIds).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        requestsRef.child(userIds).child(currentUserId).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "User declined successfully", Toast.LENGTH_SHORT)
                                                    .show();
                                        }
                                    }
                                });
                    }
                });
    }

    public static class RequestsFragmentViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userStatus;
        FloatingActionButton acceptButton, declineButton;
        CircleImageView image;

        public RequestsFragmentViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.displayUserNameTextView);
            userStatus = itemView.findViewById(R.id.displayUserStatusTextView);
            image = itemView.findViewById(R.id.displayUsersCurrentImage);
            acceptButton = itemView.findViewById(R.id.displayUserAcceptButton);
            declineButton = itemView.findViewById(R.id.displayUserDeclineButton);
        }

    }
}
