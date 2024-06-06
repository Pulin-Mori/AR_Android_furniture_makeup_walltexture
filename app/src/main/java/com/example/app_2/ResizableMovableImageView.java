package com.example.app_2;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import androidx.appcompat.widget.AppCompatImageView;

import java.io.IOException;
import java.io.InputStream;

public class ResizableMovableImageView extends AppCompatImageView {
    private Matrix matrix = new Matrix();
    private float[] lastEvent = null;
    private float d = 0f;
    private float newRot = 0f;
    private float[] start = new float[2];
    private float[] mid = new float[2];
    private float oldDist = 1f;
    private static final float SCALE_FACTOR = 0.7f;  // Adjust this factor to control the scaling speed

    public ResizableMovableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ScaleType.MATRIX);
        setBackgroundColor(0x00000000); // Transparent background
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                matrix.set(getImageMatrix());
                start[0] = event.getX();
                start[1] = event.getY();
                lastEvent = null;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    matrix.set(getImageMatrix());
                    midPoint(mid, event);
                    lastEvent = new float[4];
                    lastEvent[0] = event.getX(0);
                    lastEvent[1] = event.getX(1);
                    lastEvent[2] = event.getY(0);
                    lastEvent[3] = event.getY(1);
                    d = rotation(event);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (lastEvent != null && event.getPointerCount() == 2) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        // Determine if it's a scale or rotate gesture
                        float scale = (newDist / oldDist - 1) * SCALE_FACTOR + 1;
                        if (Math.abs(scale - 1) > 0.01) {
                            matrix.postScale(scale, scale, mid[0], mid[1]);
                            oldDist = newDist;
                        } else {
                            newRot = rotation(event);
                            float r = newRot - d;
                            float[] values = new float[9];
                            matrix.getValues(values);
                            float px = (values[Matrix.MTRANS_X] + getWidth() / 2f) / values[Matrix.MSCALE_X];
                            float py = (values[Matrix.MTRANS_Y] + getHeight() / 2f) / values[Matrix.MSCALE_Y];
                            matrix.postRotate(r, px, py);
                            d = newRot;
                        }
                    }
                } else if (event.getPointerCount() == 1) {
                    float dx = event.getX() - start[0];
                    float dy = event.getY() - start[1];
                    matrix.postTranslate(dx, dy);
                    start[0] = event.getX();
                    start[1] = event.getY();
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                lastEvent = null;
                checkBounds();
                break;
        }

        setImageMatrix(matrix);
        return true;
    }

    private void checkBounds() {
        Matrix m = getImageMatrix();
        float[] values = new float[9];
        m.getValues(values);
        float transX = values[Matrix.MTRANS_X];
        float transY = values[Matrix.MTRANS_Y];
        float scaleX = values[Matrix.MSCALE_X];
        float scaleY = values[Matrix.MSCALE_Y];

        Drawable drawable = getDrawable();
        if (drawable != null) {
            int drawableWidth = drawable.getIntrinsicWidth();
            int drawableHeight = drawable.getIntrinsicHeight();
            int viewWidth = getWidth();
            int viewHeight = getHeight();

            float width = drawableWidth * scaleX;
            float height = drawableHeight * scaleY;

            float deltaX = 0, deltaY = 0;

            if (transX < 0) {
                deltaX = -transX;
            } else if (transX + width > viewWidth) {
                deltaX = viewWidth - (transX + width);
            }

            if (transY < 0) {
                deltaY = -transY;
            } else if (transY + height > viewHeight) {
                deltaY = viewHeight - (transY + height);
            }

            matrix.postTranslate(deltaX, deltaY);
            setImageMatrix(matrix);
        }
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(float[] point, MotionEvent event) {
        point[0] = (event.getX(0) + event.getX(1)) / 2;
        point[1] = (event.getY(0) + event.getY(1)) / 2;
    }

    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    public void setImageFromAssets(Context context, String assetPath) {
        try {
            InputStream inputStream = context.getAssets().open(assetPath);
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            setImageDrawable(drawable);
            resetMatrix();
            Log.e("ResizableMovableImageView", "Image loaded successfully from assets: " + assetPath);
        } catch (IOException e) {
            Log.e("ResizableMovableImageView", "Error loading image from assets: " + assetPath, e);
        }
    }

    private void resetMatrix() {
        matrix.reset();
        setImageMatrix(matrix);
        invalidate();
    }
}
