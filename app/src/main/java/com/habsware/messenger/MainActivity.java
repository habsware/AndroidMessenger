package com.habsware.messenger;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference ref;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabsLayout;
    private TabsAccessorAdapter tabsAccessorAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference();
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Messenger");
        viewPager = findViewById(R.id.main_tab_pager);
        tabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabsAccessorAdapter);
        tabsLayout = findViewById(R.id.main_tabs);
        tabsLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null)
            redirectUserToLoginActivity();
        else
            verifyUserByExtraInfo();
    }

    private void verifyUserByExtraInfo() {
        String currentUserId = auth.getCurrentUser().getUid();
        ref.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("fullName").exists())
                    redirectUserToSettingsActivity();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.dropDownMenu_logoutOption){
            auth.signOut();
            redirectUserToLoginActivity();
        }
        if (item.getItemId() == R.id.dropDownMenu_accountSettings){
            redirectUserToSettingsActivity();
        }
        if (item.getItemId() == R.id.dropDownMenu_findFriends){
            redirectToFindFriendsActivity();
        }
        return true;
    }





    public void redirectUserToLoginActivity(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void redirectUserToRegisterActivity(){
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void redirectUserToSettingsActivity() {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
    }

    private void redirectToFindFriendsActivity() {
        Intent intent = new Intent(getApplicationContext(), FindFriendsActivity.class);
        startActivity(intent);
    }
}
