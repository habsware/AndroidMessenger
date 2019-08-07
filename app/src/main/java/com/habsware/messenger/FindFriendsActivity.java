package com.habsware.messenger;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private String currentUserId;
    private DatabaseReference usersRef;
    private Toolbar toolbar;
    private RecyclerView findFreindsRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

        findFreindsRecyclerView = findViewById(R.id.findFriendsRecyclerView);
        findFreindsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(usersRef, Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, final int position, @NonNull Contacts model) {

                        holder.userName.setText(model.getUserName());
                        holder.statusMode.setText(model.getStatusMode());
                        Picasso.get().load(model.getImage()).placeholder(R.drawable.male_image).into(holder.image);
                        final String selectedUserId = getRef(position).getKey();

                        if (selectedUserId.equals(currentUserId)){
                            holder.itemView.setVisibility(View.INVISIBLE);
                        }

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                redirectToSelectedUserProfileActivity(selectedUserId);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.display_users_layout, viewGroup, false);
                        return new FindFriendsViewHolder(view);
                    }
                };
        findFreindsRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void redirectToSelectedUserProfileActivity(String selectedUserId){
        Intent intent = new Intent(this, SelectedUserProfileActivity.class);
        intent.putExtra("selectedUserId", selectedUserId);
        startActivity(intent);
    }

    private static class FindFriendsViewHolder extends RecyclerView.ViewHolder {

        TextView userName;
        TextView statusMode;
        CircleImageView image;

        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);


            userName = itemView.findViewById(R.id.displayUserNameTextView);
            statusMode = itemView.findViewById(R.id.displayUserStatusTextView);
            image = itemView.findViewById(R.id.displayUsersCurrentImage);






        }
    }
}

