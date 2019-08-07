package com.habsware.messenger;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private FirebaseAuth auth;
    private String currentUserId;
    private DatabaseReference contactsRef, usersRef;
    private View contactsView;
    private RecyclerView contactRecyclerView;
    private TextView noRequestsTexView;

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         // Inflate the layout for this fragment
         contactsView = inflater.inflate(R.layout.fragment_contacts, container, false);

         auth = FirebaseAuth.getInstance();
         currentUserId = auth.getCurrentUser().getUid();
         contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
         usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

         contactRecyclerView = contactsView.findViewById(R.id.contactsRecyclerView);
         noRequestsTexView = contactsView.findViewById(R.id.noRequestsTextView_contacts);
         contactRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        return contactsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactsRef, Contacts.class)
                .build();
        final FirebaseRecyclerAdapter<Contacts,ContactsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, final int position, @NonNull final Contacts model) {
                final String usersIds = getRef(position).getKey();
                model.image = "male_image";

                usersRef.child(usersIds).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("image")){
                            model.userName = dataSnapshot.child("userName").getValue().toString();
                            model.statusMode = dataSnapshot.child("statusMode").getValue().toString();
                            model.image = dataSnapshot.child("image").getValue().toString();

                            holder.userName.setText(model.userName);
                            holder.statusMode.setText(model.statusMode);
                            Picasso.get().load(model.image).placeholder(R.drawable.male_image).into(holder.image);

                        }else{
                            model.userName = dataSnapshot.child("userName").getValue().toString();
                            model.statusMode = dataSnapshot.child("statusMode").getValue().toString();

                            holder.userName.setText(model.userName);
                            holder.statusMode.setText(model.statusMode);
                        }

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String selectedUserId = getRef(position).getKey();
                                redirectToSelectedUserProfileActivity(selectedUserId);
                            }
                        });
                        holder.sendMessageButton.setVisibility(View.VISIBLE);
                        holder.sendMessageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), ChatActivity.class);
                                intent.putExtra("selectedUserId", usersIds);
                                intent.putExtra("selectedUserName", model.userName);
                                intent.putExtra("selectedUserImage", model.image);
                                startActivity(intent);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.display_users_layout, viewGroup, false);
                ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                return viewHolder;
            }

        };
        contactRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    private void redirectToSelectedUserProfileActivity(String selectedUserId){
        Intent intent = new Intent(getActivity(), SelectedUserProfileActivity.class);
        intent.putExtra("selectedUserId", selectedUserId);
        startActivity(intent);
    }
    public static class ContactsViewHolder extends RecyclerView.ViewHolder{

        TextView userName, statusMode;
        CircleImageView image;
        Button sendMessageButton;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.displayUserNameTextView);
            statusMode = itemView.findViewById(R.id.displayUserStatusTextView);
            image = itemView.findViewById(R.id.displayUsersCurrentImage);
            sendMessageButton = itemView.findViewById(R.id.displayUserSendMessageButton);

        }
    }
}
