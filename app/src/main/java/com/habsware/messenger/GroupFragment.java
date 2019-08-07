package com.habsware.messenger;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {


    private DatabaseReference groupsRef;
    private View groupView;
    private ListView groupsListView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> groupsList = new ArrayList<>();
    private FloatingActionButton createGroupButton;
    private TextView noRequestsTextView;

    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         groupView = inflater.inflate(R.layout.fragment_group, container, false);

         createGroupButton  = groupView.findViewById(R.id.CreateGroupActionButton);
         groupsRef = FirebaseDatabase.getInstance().getReference().child("Groups");
         noRequestsTextView = groupView.findViewById(R.id.noRequestsTextView_groups);
         groupsListView = groupView.findViewById(R.id.groupsListView);
         arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, groupsList);
         groupsListView.setAdapter(arrayAdapter);

         getGroupsFromDb();

         groupsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 String currentGroup = parent.getItemAtPosition(position).toString();
                 Intent intent = new Intent(getContext(), GroupChatActivity.class);
                 intent.putExtra("groupName", currentGroup);
                 startActivity(intent);
             }
         });

         createGroupButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                startNewGroupChat();

             }
         });

         return groupView;
    }

    private void getGroupsFromDb() {
        groupsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    noRequestsTextView.setVisibility(View.VISIBLE);
                }
                Set<String> set = new HashSet<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    set.add((data).getKey());
                }
                groupsList.clear();
                groupsList.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void startNewGroupChat() {

        final EditText groupNameEditText = new EditText(getContext());
        groupNameEditText.setHint("Provide a Name..");


        new AlertDialog.Builder(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog)
                .setTitle("Create a New Group Chat")
                .setView(groupNameEditText)
                .setPositiveButton("Create ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String getGroupName = groupNameEditText.getText().toString().trim();
                        if (getGroupName.isEmpty())
                            Toast.makeText(getContext(), "Please provide a group name", Toast.LENGTH_SHORT).show();
                        else
                            createNewGroupChat(getGroupName);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_input_add)
                .show();
    }

    private void createNewGroupChat(final String groupName) {
        groupsRef.child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Toast.makeText(getContext(), groupName + " has been created successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
