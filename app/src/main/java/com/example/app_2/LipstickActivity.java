package com.example.app_2;

import static com.google.mediapipe.tasks.core.Delegate.CPU;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker;
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class LipstickActivity extends AppCompatActivity implements ImageAnalysis.Analyzer {

    private static final int REQUEST_CAMERA_PERMISSION = 1001;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private OverLayFace overLayFace;
    private FaceLandmarker faceLandmarker;
    private Bitmap bitmap, rotatedBitmap;
    Boolean b = true;
    ExtendedFloatingActionButton EFAB1, EFAB2;
    int LIP_STICK, EYE_BROWS, IRIS_COLOR;
    Paint paint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makeup);

        previewView = findViewById(R.id.viewFinder);
        overLayFace = findViewById(R.id.overlayViewface);
        EFAB1 = findViewById(R.id.EFAB1);
        EFAB2 = findViewById(R.id.EFAB2);
        LIP_STICK = getIntent().getIntExtra("LIP_STICK",-1);
        EYE_BROWS = getIntent().getIntExtra("EYE_BROWS",-1);
        IRIS_COLOR = getIntent().getIntExtra("IRIS_COLOR",-1);

        Log.e("Values","Lipstick value : "+LIP_STICK+" Eyebrows value : "+EYE_BROWS+" Iris value : "+IRIS_COLOR);

        paint = new Paint();
        paint.setColor(Color.GREEN); // Red color for the lines
        paint.setStrokeWidth(2f);
        paint.setAlpha(70);

        EFAB2.setVisibility(View.GONE);

        EFAB1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (b) {
                    paint.setColor(Color.RED);
                    paint.setStrokeWidth(2f);
                    paint.setAlpha(70);
                    b = false;
                }
                else {
                    paint.setColor(Color.GREEN);
                    paint.setStrokeWidth(2f);
                    paint.setAlpha(70);
                    b = true;
                }
            }
        });

        try {
            BaseOptions baseOptions = BaseOptions.builder()
                    .setModelAssetPath("face_landmarker.task")
                    .setDelegate(CPU)
                    .build();

            FaceLandmarker.FaceLandmarkerOptions optionsBuilder = FaceLandmarker.FaceLandmarkerOptions.builder()
                    .setBaseOptions(baseOptions)
                    .setRunningMode(RunningMode.LIVE_STREAM)
                    .setResultListener(this::returnLivestreamResult)
                    .setErrorListener(this::returnLivestreamError)
                    .setMinFaceDetectionConfidence(0.5F)
                    .setMinFacePresenceConfidence(0.5F)
                    .setMinTrackingConfidence(0.5F)
                    .setNumFaces(1)
                    .setOutputFaceBlendshapes(true)
                    .build();

            faceLandmarker = FaceLandmarker.createFromOptions(this, optionsBuilder);
        } catch (Exception e) {
            Log.e("BaseOptions & FaceLandmarkerOptions", "Face Landmarker failed to load model with error: " + e.getMessage());
        }

        // Check and request camera permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            startCamera();
        }
    }

    private void returnLivestreamResult(FaceLandmarkerResult faceLandmarkerResult, MPImage mpImage) {
        if (!faceLandmarkerResult.faceLandmarks().isEmpty()) {
            Log.d("FaceLandmarks", "Detected landmarks: " + faceLandmarkerResult.faceLandmarks().get(0).size());
            overLayFace.setFaceLandmarks(faceLandmarkerResult,paint,LIP_STICK,EYE_BROWS,IRIS_COLOR);
        } else {
            Log.d("FaceLandmarks", "No landmarks detected.");
        }
    }

    private void returnLivestreamError(RuntimeException e) {
        Log.e("returnLivestreamError", "Error in returning live stream: " + e.getMessage());
    }

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraX(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, getExecutor());
    }

    private void bindCameraX(@NonNull ProcessCameraProvider cameraProvider) {
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        Size size = new Size(720,1920 );
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(size)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(getExecutor(), this);

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
    }

    @Override
    public void analyze(@NonNull ImageProxy imageProxy) {
        int imageWidth = imageProxy.getWidth();
        int imageHeight = imageProxy.getHeight();
        Log.d("ImageProxy dimension", "Height: " + imageHeight + " Width: " + imageWidth);

        try {
            bitmap = imageProxy.toBitmap();
            Matrix matrix = new Matrix();
            matrix.postRotate(imageProxy.getImageInfo().getRotationDegrees());
            matrix.postScale(-1f, 1f, ((float) imageProxy.getWidth()), ((float) imageProxy.getHeight()));

            rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            MPImage mpImage = new BitmapImageBuilder(rotatedBitmap).build();
            faceLandmarker.detectAsync(mpImage, imageProxy.getImageInfo().getTimestamp());
        } catch (Exception e) {
            Log.e("ImageAnalysis analyze", "Error during image analysis", e);
        } finally {
            imageProxy.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        faceLandmarker.close();
    }
}


//LipstickActivity