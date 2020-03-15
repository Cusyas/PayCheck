package com.cusyas.android.paycheck

import android.graphics.*
import android.graphics.drawable.Drawable

class BillColorBarDrawable(barColors: IntArray, daysTillDuePercentage: Float): Drawable() {
    var barColors: IntArray = barColors
    val daysTillDuePercentage = daysTillDuePercentage

    override fun draw(canvas: Canvas) {
        val bounds: Rect = bounds
        val width = bounds.right - bounds.left
        val height = bounds.bottom - bounds.top

        val backgroundPaint = Paint()
        val currentBarWidth: Float = width * daysTillDuePercentage

        backgroundPaint.color = barColors[0]
        canvas.drawRect(
                0f, 0f,
                currentBarWidth, height.toFloat(), backgroundPaint
        )
        backgroundPaint.color = barColors[1]
        canvas.drawRect(
                currentBarWidth, 0f,
                width.toFloat(), height.toFloat(),backgroundPaint)
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }
}