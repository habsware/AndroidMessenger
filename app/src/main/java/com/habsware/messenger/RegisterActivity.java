package com.habsware.messenger;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference rootRef;
    private Button registerButton;
    private EditText emailEditText, passwordEditText;
    private TextView signInTextView;
    private ProgressDialog creatingUserDialogBox;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        registerButton = findViewById(R.id.registerButton);
        emailEditText = findViewById(R.id.registerEmailEditView);
        passwordEditText = findViewById(R.id.registerPassEditView);
        signInTextView = findViewById(R.id.orLoginTextView);
        creatingUserDialogBox = new ProgressDialog(this);
        
        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectUserToLoginActivity();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUserAccount();
            }
        });
    }



    private void createUserAccount(){
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        creatingUserDialogBox.setTitle("Sign Up");
        creatingUserDialogBox.setMessage("Creating Account ..");
        creatingUserDialogBox.show();

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String userId = auth.getCurrentUser().getUid();
                    rootRef.child("Users").child(userId).setValue("");
                    redirectUserToMainActivity();
                    creatingUserDialogBox.dismiss();
                    Toast.makeText(RegisterActivity.this, "Successfully Registered!", Toast.LENGTH_SHORT)
                            .show();
                }
                else {
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT)
                            .show();
                    creatingUserDialogBox.dismiss();
                }
            }
        });
    }

    private void redirectUserToLoginActivity() {
        Intent intent =  new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void redirectUserToMainActivity() {
        Intent intent =  new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
