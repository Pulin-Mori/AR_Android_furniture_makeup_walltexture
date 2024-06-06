package com.example.app_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    Animation topAnim, bottomAnim;
    ImageView topImg, bottomImg1, bottonImg2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_page);

        if (OpenCVLoader.initLocal()) {
            Log.e("OpenCV error", "OpenCV loaded successfully");
        } else {
            Log.e("OpenCV error", "OpenCV initialization failed!");
            (Toast.makeText(this, "OpenCV initialization failed!", Toast.LENGTH_LONG)).show();
            return;
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        topImg = findViewById(R.id.imageView6);
        bottomImg1 = findViewById(R.id.imageView);
        bottonImg2 = findViewById(R.id.imageView4);

        topAnim = AnimationUtils.loadAnimation(this,R.anim.splash_screen_top);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.splash_screen_bottom);

        topImg.setAnimation(topAnim);
        bottomImg1.setAnimation(bottomAnim);
        bottonImg2.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
                finish();
            }
        },4000);
    }
}