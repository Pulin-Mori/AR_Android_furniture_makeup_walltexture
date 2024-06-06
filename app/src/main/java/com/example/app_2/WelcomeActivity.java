package com.example.app_2;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class WelcomeActivity extends AppCompatActivity {

    Button registerbtn, loginbtn;
    FirebaseAuth mAuth;
    Boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);

        registerbtn = findViewById(R.id.buttonWelReg);
        loginbtn = findViewById(R.id.buttonWelLogin);
        mAuth = FirebaseAuth.getInstance();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (exit) {
                    finishAffinity();
                }

                Toast.makeText(WelcomeActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
                exit = true;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                },2000);
            }
        });

        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            finish();
        }

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
//                finish();
            }
        });

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
//                finish();
            }
        });
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        finish();
    }
}