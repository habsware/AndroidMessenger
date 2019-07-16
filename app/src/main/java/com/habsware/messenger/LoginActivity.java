package com.habsware.messenger;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private Button loginButton;
    private EditText emailEditText, passworldEditText;
    private TextView registerTextView, forgotPassTextView;
    private ProgressDialog loggingInDialogBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        loginButton = findViewById(R.id.loginButton);
        emailEditText = findViewById(R.id.loginEmailEditView);
        passworldEditText = findViewById(R.id.loginPasswordEditText);
        registerTextView = findViewById(R.id.orRegisterTextView);
        forgotPassTextView = findViewById(R.id.forgotPasswordTextView);
        loggingInDialogBox = new ProgressDialog(this);

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectUserToRegisterActivity();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    public boolean userInputIsEmpty(String email, String password){

        if (email.isEmpty()) {
            Toast.makeText(this, "Please type in your email", Toast.LENGTH_SHORT)
                    .show();
            return true;
        }
        if (password.isEmpty()){
            Toast.makeText(this, "Please provide a password", Toast.LENGTH_SHORT)
                    .show();
            return true;
        }
        return false;
    }

    public void loginUser(){
        String email = emailEditText.getText().toString().trim();
        String password = passworldEditText.getText().toString().trim();

        if (userInputIsEmpty(email, password)){
            return;
        }

        loggingInDialogBox.setMessage("Signing in, please wait..");
        loggingInDialogBox.show();

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    redirectUserToMainActivity();
                    loggingInDialogBox.dismiss();
                    Toast.makeText(getApplicationContext(), "Successfully signed in!", Toast.LENGTH_SHORT)
                            .show();
                }
                else{
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT)
                            .show();
                    loggingInDialogBox.dismiss();
                }
            }
        });
    }
    private void redirectUserToRegisterActivity() {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }

    private void redirectUserToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
