package com.example.app_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class FurnitureDashboardActivity extends AppCompatActivity {

    private Button realtimebtn, capturebtn;
    private TextView backbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furniture_dashboard);

        realtimebtn = findViewById(R.id.realtimebtn);
        capturebtn = findViewById(R.id.capturebtn);
        backbtn = findViewById(R.id.backbtn);

        realtimebtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), ARFurnitureActivity.class));
        });

        capturebtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), FurnitureCaptureActivity.class));
        });

        backbtn.setOnClickListener(v -> {
            finish();
        });
    }
}