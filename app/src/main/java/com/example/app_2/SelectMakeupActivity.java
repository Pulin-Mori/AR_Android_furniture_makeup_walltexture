package com.example.app_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.sceneform.ux.TwistGesture;

public class SelectMakeupActivity extends AppCompatActivity {

    Switch LipStick, HairColor, Eyebrows, Iris;
    TextView backbtn;
    Button proceedbtn;
    int LIP_STICK = 0;
    int HAIR_COLOR = 0;
    int EYE_BROWS = 0;
    int IRIS_COLOR = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_makeup);

        backbtn = findViewById(R.id.backbtn);
        proceedbtn = findViewById(R.id.proceedbtn);
        LipStick = findViewById(R.id.switchLipstick);
        HairColor = findViewById(R.id.switchHairColor);
        Eyebrows = findViewById(R.id.switchEyebrows);
        Iris = findViewById(R.id.switchIris);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        proceedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LIP_STICK == 0 && HAIR_COLOR == 0 && EYE_BROWS == 0 && IRIS_COLOR == 0) {
                    Toast.makeText(SelectMakeupActivity.this, "No Makeup Style Selected", Toast.LENGTH_SHORT).show();
                }
                if (LIP_STICK == 1 && HAIR_COLOR == 0 && EYE_BROWS == 0 && IRIS_COLOR == 0) {
                    startActivity(new Intent(getApplicationContext(), LipstickActivity.class).putExtra("LIP_STICK",LIP_STICK).putExtra("EYE_BROWS",EYE_BROWS).putExtra("IRIS_COLOR",IRIS_COLOR));
                }
                if (LIP_STICK == 0 && HAIR_COLOR == 1 && EYE_BROWS == 0 && IRIS_COLOR == 0) {
                    startActivity(new Intent(getApplicationContext(), HairColorActivity.class));
                }
                if (LIP_STICK == 0 && HAIR_COLOR == 0 && EYE_BROWS == 1 && IRIS_COLOR == 0) {
                    startActivity(new Intent(getApplicationContext(), LipstickActivity.class).putExtra("LIP_STICK",LIP_STICK).putExtra("EYE_BROWS",EYE_BROWS).putExtra("IRIS_COLOR",IRIS_COLOR));
                }
                if (LIP_STICK == 0 && HAIR_COLOR == 0 && EYE_BROWS == 0 && IRIS_COLOR == 1) {
                    startActivity(new Intent(getApplicationContext(), LipstickActivity.class).putExtra("LIP_STICK",LIP_STICK).putExtra("EYE_BROWS",EYE_BROWS).putExtra("IRIS_COLOR",IRIS_COLOR));
                }
                // For Toasting that no two should be selected together
                else {
                    Toast.makeText(SelectMakeupActivity.this, "Select only one activity at moment ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        LipStick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    LIP_STICK = 1;
                }
                else {
                    LIP_STICK =0;
                }
            }
        });

        HairColor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    HAIR_COLOR = 1;
                }
                else {
                    HAIR_COLOR = 0;
                }
            }
        });

        Eyebrows.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    EYE_BROWS = 1;
                }
                else {
                    EYE_BROWS = 0;
                }
            }
        });

        Iris.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    IRIS_COLOR = 1;
                }
                else {
                    IRIS_COLOR = 0;
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}