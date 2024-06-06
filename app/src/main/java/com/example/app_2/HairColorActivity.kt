package com.example.app_2

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.ByteBufferExtractor
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.imagesegmenter.ImageSegmenter
import com.google.mediapipe.tasks.vision.imagesegmenter.ImageSegmenterResult
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.properties.Delegates

class HairColorActivity : AppCompatActivity() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var previewView: PreviewView
    private lateinit var imageViewer: OverlayViewHair
    private lateinit var imageSegmenter: ImageSegmenter
    private lateinit var EFAB1: ExtendedFloatingActionButton
    private lateinit var EFAB2: ExtendedFloatingActionButton
    private var bool: Boolean = true
    private var num: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_makeup)

        previewView = findViewById(R.id.viewFinder)
        imageViewer = findViewById(R.id.overlayViewhair)
        EFAB1 = findViewById(R.id.EFAB1)
        EFAB2 = findViewById(R.id.EFAB2)
        cameraExecutor = Executors.newSingleThreadExecutor()

        EFAB1.visibility = View.GONE
        EFAB2.visibility = View.VISIBLE

        EFAB2.setOnClickListener{v : View? ->

            if (bool) {
                num=0
                bool = false
            }
            else {
                num=1
                bool = true
            }
        }

        val baseOptions = BaseOptions.builder()
            .setModelAssetPath("hair_segmenter.tflite")
            .setDelegate(Delegate.CPU)
            .build()

        val options = ImageSegmenter.ImageSegmenterOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setResultListener(this::returnSegmentationResult)
            .setErrorListener(this::returnSegmentationHelperError)
            .setOutputCategoryMask(true)
            .setOutputConfidenceMasks(false)
            .build()

        imageSegmenter = ImageSegmenter.createFromOptions(this, options)

        requestCameraPermission() // Request permission at startup
    }

    private fun returnSegmentationResult(imageSegmenterResult: ImageSegmenterResult?, mpImage: MPImage?) {
        if (imageSegmenterResult == null || mpImage == null) {
            Log.e("Segmentation", "Segmentation result or MPImage is null")
            return
        }

        val bytebuffer = ByteBufferExtractor.extract(imageSegmenterResult.categoryMask().get())
        val width = mpImage.width
        val height = mpImage.height
        val num2 = num

        Log.d("Segmentation", "Segmentation result received: Width = $width, Height = $height")

        imageViewer.setResults(bytebuffer, width, height, num2)
    }

    private fun returnSegmentationHelperError(runtimeException: RuntimeException) {
        Log.e("Segmentation Error", "Error: ${runtimeException.message}")
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        } else {
            startCamera() // Permission already granted, proceed
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera() // Permission granted
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCamera() {
        val processCameraProvider = ProcessCameraProvider.getInstance(this)
        processCameraProvider.addListener({
            try {
                val cameraProvider = processCameraProvider.get()
                val preview = Preview.Builder().build()
                preview.setSurfaceProvider(previewView.surfaceProvider)

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer { imageProxy ->
                    try {

                        val bitmap = imageProxy.toBitmap()

                        val matrix = Matrix().apply {
                            postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
                            postScale(
                                -1f,
                                1f,
                                imageProxy.width.toFloat(),
                                imageProxy.height.toFloat()
                            )
                        }

                        val rotatedBitmap = Bitmap.createBitmap(
                            bitmap,
                            0,
                            0,
                            bitmap.width,
                            bitmap.height,
                            matrix,
                            true
                        )

                        val mpImage = BitmapImageBuilder(rotatedBitmap).build()
                        imageSegmenter.segmentAsync(mpImage, imageProxy.imageInfo.timestamp)

                    } catch (e: Exception) {
                        Log.e("ImageAnalysis", "Error during image analysis", e)
                    } finally {
                        imageProxy.close()
                    }
                })

                cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_FRONT_CAMERA, preview, imageAnalysis)
            } catch (e: Exception) {
                Log.e("CameraX", "Error starting camera", e)
                Toast.makeText(this, "Error accessing camera", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        imageSegmenter.close()
    }
}

//HairColorActivity