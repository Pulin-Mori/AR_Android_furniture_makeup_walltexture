package com.example.app_2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark;
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult;

import java.util.List;

public class OverLayFace extends View {

    private FaceLandmarkerResult faceLandmarkerResult;
    private Paint paint;
    int LIP_STICK, EYE_BROWS, IRIS_COLOR;

    public OverLayFace(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setFaceLandmarks(FaceLandmarkerResult faceLandmarkerResult, Paint paint, int LIP_STICK, int EYE_BROWS, int IRIS_COLOR) {
        this.faceLandmarkerResult = faceLandmarkerResult;
        this.paint = paint;
        this.LIP_STICK = LIP_STICK;
        this.EYE_BROWS = EYE_BROWS;
        this.IRIS_COLOR = IRIS_COLOR;
        invalidate(); // Request to redraw the view with the new landmarks
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (faceLandmarkerResult != null && !faceLandmarkerResult.faceLandmarks().isEmpty()) {

            List<NormalizedLandmark> landmarks = faceLandmarkerResult.faceLandmarks().get(0);

            if (LIP_STICK == 1) {

                int[] arr1 = new int[]{61, 146, 91, 181, 84, 17, 314, 405, 321, 375, 291, 324, 318, 402, 317, 14, 87, 178, 88, 95, 78};
                int[] arr2 = new int[]{191, 80, 81, 82, 13, 312, 311, 310, 415, 308, 409, 270, 269, 267, 0, 37, 39, 40, 185};
//          /lb61, 146, 91, 181, 84, 17, 314, 405, 321, 375, 291,/ut185, 40, 39, 37, 0, 267, 269, 270, 409,/lt 78, 95, 88, 178, 87, 14, 317, 402, 318, 324,/ub 191, 80, 81, 82, 13, 312, 311, 310, 415, 308

                Path path = new Path();
                Path path2 = new Path();
                boolean firstPoint = true;
                boolean firstPoint2 = true;

                for (int k : arr1) {
                    float x = landmarks.get(k).x() * getWidth();
                    float y = landmarks.get(k).y() * getHeight();
                    if (firstPoint) {
                        path.moveTo(x, y);
                        firstPoint = false;
                    } else {
                        path.lineTo(x, y);
                    }
                }
                path.close();
                canvas.drawPath(path, paint);

                for (int j : arr2) {
                    float x = landmarks.get(j).x() * getWidth();
                    float y = landmarks.get(j).y() * getHeight();
                    if (firstPoint2) {
                        path2.moveTo(x, y);
                        firstPoint2 = false;
                    } else {
                        path2.lineTo(x, y);
                    }
                }
                path2.close();
                canvas.drawPath(path2, paint);
            }
            if (EYE_BROWS == 1) {

                int[] arr3 = new int[]{55,65,52,53,46,70,63,105,66,107};
                int[] arr4 = new int[]{285,295,282,283,276,300,293,334,296,336};

                Path path3 = new Path();
                Path path4 = new Path();
                boolean firstPoint = true;
                boolean firstPoint2 = true;

                for (int k : arr3) {
                    float x = landmarks.get(k).x() * getWidth();
                    float y = landmarks.get(k).y() * getHeight();
                    if (firstPoint) {
                        path3.moveTo(x, y);
                        firstPoint = false;
                    } else {
                        path3.lineTo(x, y);
                    }
                }
                path3.close();
                canvas.drawPath(path3, paint);

                for (int j : arr4) {
                    float x = landmarks.get(j).x() * getWidth();
                    float y = landmarks.get(j).y() * getHeight();
                    if (firstPoint2) {
                        path4.moveTo(x, y);
                        firstPoint2 = false;
                    } else {
                        path4.lineTo(x, y);
                    }
                }
                path4.close();
                canvas.drawPath(path4, paint);
            }
            if (IRIS_COLOR == 1) {

                int[] arr5 = new int[]{468};
                int[] arr6 = new int[]{473};

                float x1 = landmarks.get(arr5[0]).x() * getWidth();
                float y1 = landmarks.get(arr5[0]).y() * getWidth();
                canvas.drawCircle(x1,y1,25f,paint);

                float x2 = landmarks.get(arr6[0]).x() * getWidth();
                float y2 = landmarks.get(arr6[0]).y() * getWidth();
                canvas.drawCircle(x2,y2,25f,paint);
            }

//            for (NormalizedLandmark normalizedLandmark : landmarks) {
//                float w = normalizedLandmark.x() * getWidth();
//                float z = normalizedLandmark.y() * getHeight();
//                canvas.drawPoint(w, z, paint);
//                Log.e("landmark points","w:"+w+" z:"+z);
//            }
//
////             Draw lines between landmarks - Example: Connect all consecutive points
//            for (int i = 0; i < landmarks.size() - 1; i++) {
//                NormalizedLandmark startLandmark = landmarks.get(i);
//                NormalizedLandmark endLandmark = landmarks.get(i + 1);
//
//                canvas.drawLine(
//                        startLandmark.x() * getWidth(), startLandmark.y() * getHeight(),
//                        endLandmark.x() * getWidth(), endLandmark.y() * getHeight(),
//                        paint
//                );
//            }
        }
    }
}

//OverLayViewLipstick
