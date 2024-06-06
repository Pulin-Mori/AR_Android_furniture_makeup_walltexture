package com.example.app_2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.nio.ByteBuffer
import kotlin.math.max

class OverlayViewHair(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {
    companion object {
        const val ALPHA_COLOR = 50
    }

    private var scaleBitmap: Bitmap? = null
    private val labelColors = listOf(
        Color.GREEN,
        Color.CYAN
    )

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        scaleBitmap?.let {
            val centerX = (width - it.width) / 2f
            val centerY = (height - it.height) / 2f
            canvas.drawBitmap(it, centerX, centerY, null)
        }
    }

    fun setResults(
        byteBuffer: ByteBuffer,
        outputWidth: Int,
        outputHeight: Int,
        num: Int
    ) {
        // Log for debugging
        Log.d("OverlayView", "Setting results: Width = $outputWidth, Height = $outputHeight")

        // Create the mask bitmap with colors and the set of detected labels.
        val pixels = IntArray(byteBuffer.capacity())
        for (i in pixels.indices) {
            val index = byteBuffer.get(i).toUInt() % 20U
            val color = labelColors[num].toAlphaColor()
            if (index.toInt() == 1) {
                pixels[i] = color
            }
            else {
                pixels[i] = Color.TRANSPARENT
            }
        }
        val image = Bitmap.createBitmap(
            pixels,
            outputWidth,
            outputHeight,
            Bitmap.Config.ARGB_8888
        )

        val scaleFactor = max(width * 1f / outputWidth, height * 1f / outputHeight)

        val scaleWidth = (outputWidth * scaleFactor).toInt()
        val scaleHeight = (outputHeight * scaleFactor).toInt()

        scaleBitmap = Bitmap.createScaledBitmap(
            image, scaleWidth, scaleHeight, false
        )
        Log.d("OverlayView", "Bitmap created and scaled: Width = $scaleWidth, Height = $scaleHeight")
        invalidate()
    }
}

fun Int.toAlphaColor(): Int {
    return Color.argb(
        OverlayViewHair.ALPHA_COLOR,
        Color.red(this),
        Color.green(this),
        Color.blue(this)
    )
}