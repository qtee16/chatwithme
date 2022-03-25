package com.example.chatwithme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chatwithme.Model.User;
import com.example.chatwithme.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding bd;

    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bd = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(bd.getRoot());
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We're creating your account.");

        bd.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = bd.edtUsernameSignUp.getText().toString().trim();
                String email    = bd.edtEmailSignUp.getText().toString().trim();
                String pass     = bd.edtPassSignUp.getText().toString().trim();
                String cfPass   = bd.edtPassSignUp.getText().toString().trim();

                if (username.isEmpty() || email.isEmpty() || pass.isEmpty() || cfPass.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    if (!pass.equals(cfPass)) {
                        Toast.makeText(SignUpActivity.this, "Please fill the same password", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.show();
                        mAuth.createUserWithEmailAndPassword(email, pass)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        progressDialog.dismiss();
                                        if (task.isSuccessful()) {
                                            User user = new User(username, email, pass, "offline");
                                            String id = task.getResult().getUser().getUid();
                                            database.getReference()
                                                    .child("Users")
                                                    .child(id)
                                                    .setValue(user);

                                            Toast.makeText(SignUpActivity.this, "Sign up successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(SignUpActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            }
        });

        bd.tvToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}