package com.example.app_2;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText editTextEmail ,editTextPassword ,editTextName;
    TextView loginTxt;
    Button buttonReg;
    ProgressBar progressBarReg;
    FirebaseAuth mAuth;
    FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);
        progressBarReg = findViewById(R.id.progressBarReg);
        editTextEmail = findViewById(R.id.emailReg);
        editTextPassword = findViewById(R.id.passwordReg);
        editTextName = findViewById(R.id.nameReg);
        buttonReg = findViewById(R.id.buttonReg);
        loginTxt = findViewById(R.id.textViewReg);
        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            finish();
        }

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, email, password;
                name = String.valueOf(editTextName.getText());
                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();

                if (TextUtils.isEmpty(name)){
                    Toast.makeText(RegisterActivity.this, "Enter Your Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(RegisterActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length()<8){
                    Toast.makeText(RegisterActivity.this, "Password must be atleast 8 character long", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBarReg.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "createUserWithEmail:success");
                                    String userId = mAuth.getCurrentUser().getUid();
                                    Map<String,Object> user = new HashMap<>();
                                    user.put("Name",name);
                                    user.put("Email",email);
                                    user.put("Password",password);
                                    DocumentReference documentReference = fstore.collection("Users").document(userId);
                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.w(TAG,"User Successfully Profile Created for "+userId);
                                            Toast.makeText(RegisterActivity.this, "User Successfully Created", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG,"Failed to add user data to Firestore Error: "+ e.getMessage());
                                        }
                                    });
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(RegisterActivity.this, "Authentication failed."+task.getException(), Toast.LENGTH_SHORT).show();
                                    progressBarReg.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
            }
        });

        loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarReg.setVisibility(View.VISIBLE);
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
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