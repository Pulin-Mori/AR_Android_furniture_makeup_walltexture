package com.example.app_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class ChooseActivity extends AppCompatActivity {

    Spinner spinner;
    TextView backbtn;
    Button proceedbtn;
    String[] catagory = {"Furniture","Makeup","Wall Texture"};
    String value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        spinner = findViewById(R.id.spinner);
        backbtn = findViewById(R.id.backbtn);
        proceedbtn = findViewById(R.id.proceedbtn);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,catagory);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                value = catagory[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(ChooseActivity.this, "Plz Select something", Toast.LENGTH_SHORT).show();
            }
        });

        proceedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.equals(value, "Furniture")) {
                    startActivity(new Intent(getApplicationContext(), FurnitureDashboardActivity.class));
                }
                else if (Objects.equals(value, "Makeup")) {
                    startActivity(new Intent(getApplicationContext(), SelectMakeupActivity.class));
                }
                else if (Objects.equals(value, "Wall Texture")) {
                    startActivity(new Intent(getApplicationContext(), WalltextureActivity.class));
                }
                else {
                    Toast.makeText(ChooseActivity.this, "Error in selection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        });
    }
}