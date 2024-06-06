package com.example.app_2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;

import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mediapipe.tasks.components.containers.Detection;
import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector;

import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class FurnitureCaptureActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private PreviewView previewView;
    private ImageView imageView, background_image;
    private Button captureButton, recaptureButton, proceedButton;
    private Spinner spinnerDetectedItems;
    private FrameLayout frameLayout;
    private TextView instructiontxt;
    String selectedObject;
    ImageCapture imageCapture;
    ObjectDetector objectDetector;
    ObjectDetectorResult detectionResult;
    ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture;
    Bitmap inpaintedBitmap;
    ResizableMovableImageView imageView2;
    HashMap<String,String> modelData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furniture_capture);

        modelData.put("I1","Single Sofa.png");
        modelData.put("I2","Table.png");
        modelData.put("I3","sofa.png");
        modelData.put("I4","Chair.png");

        instructiontxt = findViewById(R.id.instructiontxt);
        previewView = findViewById(R.id.previewView);
        imageView = findViewById(R.id.imageView);
        captureButton = findViewById(R.id.captureButton);
        spinnerDetectedItems = findViewById(R.id.spinnerDetectedItems);
        recaptureButton = findViewById(R.id.recaptureButton);
        proceedButton = findViewById(R.id.proceedButton);
        background_image = findViewById(R.id.background_image);
        frameLayout = findViewById(R.id.framelayout1);
        imageView2 = findViewById(R.id.imageView2);
        imageView2.setVisibility(View.GONE);

        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV Initialization", "OpenCV initialization failed.");
        } else {
            Log.d("OpenCV Initialization", "OpenCV initialized successfully.");
        }

        ObjectDetector.ObjectDetectorOptions options =
                ObjectDetector.ObjectDetectorOptions.builder()
                        .setBaseOptions(BaseOptions.builder().setModelAssetPath("efficientdet_lite2.tflite").build())
                        .setRunningMode(RunningMode.IMAGE)
                        .setMaxResults(10)
                        .setScoreThreshold(0.5f)
                        .build();
        objectDetector = ObjectDetector.createFromOptions(this, options);

        cameraProviderListenableFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderListenableFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderListenableFuture.get();
                startCamera(cameraProvider);
            }
            catch (Exception e) {
                Log.e("START CAMERA ERROR", Objects.requireNonNull(e.getMessage()));
                Toast.makeText(this, "Start camera error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        },ContextCompat.getMainExecutor(this));

        captureButton.setOnClickListener(v -> capturePhoto());

        recaptureButton.setOnClickListener(v -> {

            instructiontxt.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            previewView.setVisibility(View.VISIBLE);
            recaptureButton.setVisibility(View.GONE);
            captureButton.setVisibility(View.VISIBLE);
            spinnerDetectedItems.setVisibility(View.GONE);
            proceedButton.setVisibility(View.GONE);
        });

        proceedButton.setOnClickListener(this::popitem);
        }

    private void popitem(View view) {
        PopupMenu popupMenu = new PopupMenu(this,view);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.menu_furniture);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        String i = String.valueOf(item.getTitleCondensed());
        String str = modelData.get(i);
        if (modelData.containsKey(i)){

            captureButton.setVisibility(View.GONE);
            spinnerDetectedItems.setVisibility(View.GONE);
            proceedButton.setVisibility(View.GONE);
            recaptureButton.setVisibility(View.GONE);

            frameLayout.setVisibility(View.VISIBLE);
            background_image.setVisibility(View.VISIBLE);
            background_image.setImageBitmap(inpaintedBitmap);
            imageView2.setVisibility(View.VISIBLE);
            imageView2.setImageFromAssets(FurnitureCaptureActivity.this,str);
        }
        else {
            Toast.makeText(this, "Plz select a model", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void capturePhoto() {

        imageCapture.takePicture(ContextCompat.getMainExecutor(this), new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                super.onCaptureSuccess(image);

                Bitmap bitmap = imageProxyToBitmap(image);

                float rotationDegrees = image.getImageInfo().getRotationDegrees();
                Bitmap rotatedBitmap = rotateBitmap(bitmap, rotationDegrees);

                MPImage mpImage = new BitmapImageBuilder(rotatedBitmap).build();
                detectionResult = objectDetector.detect(mpImage);

                populateSpinnerWithDetectedItems();
                previewView.setVisibility(View.GONE);

                spinnerDetectedItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedObject = parent.getItemAtPosition(position).toString();

                        inpaintedBitmap = applyInpainting(rotatedBitmap,selectedObject);

                        runOnUiThread(() -> {
                            instructiontxt.setVisibility(View.GONE);
                            imageView.setImageBitmap(inpaintedBitmap);
                            imageView.setVisibility(View.VISIBLE);
                            previewView.setVisibility(View.GONE);
                            captureButton.setVisibility(View.GONE);
                            recaptureButton.setVisibility(View.VISIBLE);
                            spinnerDetectedItems.setVisibility(View.VISIBLE);
                            proceedButton.setVisibility(View.VISIBLE);
                        });

                        image.close();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Toast.makeText(FurnitureCaptureActivity.this, "Please Select an item", Toast.LENGTH_SHORT).show();
                    }
                });

                image.close();
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                super.onError(exception);
                Log.e("CAPTURE IMAGE ERROR","Error in capturing image "+exception.getMessage());
                Toast.makeText(FurnitureCaptureActivity.this, "Error in capturing image "+exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Bitmap rotateBitmap(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    private Bitmap applyInpainting(Bitmap bitmap,String selecteditem) {

        // Convert Bitmap to Mat
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);

        // Remove alpha channel if present
        if (src.channels() == 4) {
            Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2RGB);
        }

        // Create a mask for the ROIs and draw bounding boxes
        Mat mask = Mat.zeros(src.size(), CvType.CV_8UC1);
        for (Detection detectedObject : detectionResult.detections()) {
            RectF boundingBox = detectedObject.boundingBox();
            String objectName = detectedObject.categories().get(0).categoryName();
            String confidence = String.valueOf(detectedObject.categories().get(0).score());

            Log.e("Detection Result", "Object Name: " + objectName +" , Confidence : "+confidence);
//            Toast.makeText(this, "Object Name: " + objectName , Toast.LENGTH_SHORT).show();

            if( objectName == selecteditem) {
                Point tl = new Point(boundingBox.left, boundingBox.top);
                Point br = new Point(boundingBox.right, boundingBox.bottom);
                Imgproc.rectangle(mask, tl, br, new Scalar(255), -1);
            }
        }

        // Apply inpainting to the mask
        Mat dst = new Mat();
        Photo.inpaint(src, mask, dst, 3, Photo.INPAINT_TELEA);

        // Convert the modified Mat object back to Bitmap
        Bitmap resultBitmap = Bitmap.createBitmap(dst.cols(), dst.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst, resultBitmap);

        // Release Mats
        src.release();
        mask.release();
        dst.release();

        return resultBitmap;
    }

    private void populateSpinnerWithDetectedItems() {
        List<String> detectedItems = new ArrayList<>();
        for (Detection detectedObject : detectionResult.detections()) {
            String objectName = detectedObject.categories().get(0).categoryName();
            detectedItems.add(objectName);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, detectedItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDetectedItems.setAdapter(adapter);
        spinnerDetectedItems.setVisibility(View.VISIBLE);
    }

    private void startCamera(ProcessCameraProvider cameraProvider) {

        cameraProvider.unbindAll();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Preview preview = new Preview.Builder().build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        cameraProvider.bindToLifecycle(this, cameraSelector,preview,imageCapture);
    }

    private Bitmap imageProxyToBitmap(ImageProxy image) {
        if (image.getFormat() == ImageFormat.JPEG) {
            return jpegToBitmap(image);
        }
        else {
            Log.e("ImageProxyToBitmap", "Unsupported image format: " + image.getFormat());
            throw new IllegalArgumentException("Unsupported image format: " + image.getFormat());
        }

    }

    private Bitmap jpegToBitmap(ImageProxy image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}


//FurnitureCaptureActivity