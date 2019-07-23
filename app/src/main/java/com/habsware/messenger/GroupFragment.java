package com.habsware.messenger;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         groupView = inflater.inflate(R.layout.fragment_group, container, false);

         groupsRef = FirebaseDatabase.getInstance().getReference().child("Groups");
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

         return groupView;
    }

    private void getGroupsFromDb() {
        groupsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    set.add((dataSnapshot1).getKey());
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

}
