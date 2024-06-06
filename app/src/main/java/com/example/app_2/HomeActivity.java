package com.example.app_2;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    Button trybtn, ARmakeup, ARwalltexture, ARfurniture;
    ImageView logoutbtn;
    FirebaseAuth mAuth;
    Boolean exit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (exit) {
                    finishAffinity();
                }

                Toast.makeText(HomeActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
                exit = true;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                },2000);
            }
        });

        logoutbtn = findViewById(R.id.logoutbtn);
        trybtn = findViewById(R.id.trybtn);
        ARmakeup = findViewById(R.id.ARmakeup);
        ARwalltexture = findViewById(R.id.ARwalltexture);
        ARfurniture = findViewById(R.id.ARfurniture);
        mAuth = FirebaseAuth.getInstance();

        logoutbtn.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(getApplicationContext(),WelcomeActivity.class));
            finish();
        });

        trybtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ChooseActivity.class)));

        ARmakeup.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SelectMakeupActivity.class)));

        ARwalltexture.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), WalltextureActivity.class)));

        ARfurniture.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), FurnitureDashboardActivity.class)));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}