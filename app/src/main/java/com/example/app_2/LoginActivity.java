package com.example.app_2;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailTxt, passwordTxt;
    TextView registerTxt;
    Button Loginbtn;
    ProgressBar progressBarLogin;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        emailTxt = findViewById(R.id.emailLogin);
        passwordTxt = findViewById(R.id.passwordLogin);
        Loginbtn = findViewById(R.id.buttonLogin);
        progressBarLogin = findViewById(R.id.progressBarLogin);
        registerTxt = findViewById(R.id.textViewLogin);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            finish();
        }

        Loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email ,password;
                email = emailTxt.getText().toString();
                password = passwordTxt.getText().toString();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(LoginActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length()<8){
                    Toast.makeText(LoginActivity.this, "Password must be atleast 8 character long", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBarLogin.setVisibility(View.VISIBLE);

                //Authentication of User
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "signInWithEmail:success");
                                    Toast.makeText(LoginActivity.this, "Authentication Successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                                    finish();
                                } else {
                                    Log.w(TAG, "signInWithEmail:failure "+task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication Failed"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        registerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarLogin.setVisibility(View.VISIBLE);
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
                finish();
            }
        });
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        finish();
    }
}