package com.mutenlab.sudoit.ui

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.google.android.gms.vision.text.TextBlock
import com.mutenlab.sudoit.ui.camera.GraphicOverlay

/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 */
class OcrGraphic internal constructor(overlay: GraphicOverlay<*>, val textBlock: TextBlock?) : GraphicOverlay.Graphic(overlay) {

    var id: Int = 0

    init {

        if (sRectPaint == null) {
            sRectPaint = Paint()
            sRectPaint!!.color = TEXT_COLOR
            sRectPaint!!.style = Paint.Style.STROKE
            sRectPaint!!.strokeWidth = 4.0f
        }

        if (sTextPaint == null) {
            sTextPaint = Paint()
            sTextPaint!!.color = TEXT_COLOR
            sTextPaint!!.textSize = 54.0f
        }
        // Redraw the overlay, as this graphic has been added.
        postInvalidate()
    }

    /**
     * Checks whether a point is within the bounding box of this graphic.
     * The provided point should be relative to this graphic's containing overlay.
     * @param x An x parameter in the relative context of the canvas.
     * *
     * @param y A y parameter in the relative context of the canvas.
     * *
     * @return True if the provided point is contained within this graphic's bounding box.
     */
    override fun contains(x: Float, y: Float): Boolean {
        // TODO: Check if this graphic's text contains this point.
        if (textBlock == null) {
            return false
        }
        val rect = RectF(textBlock.boundingBox)
        rect.left = translateX(rect.left)
        rect.top = translateY(rect.top)
        rect.right = translateX(rect.right)
        rect.bottom = translateY(rect.bottom)
        return rect.left < x && rect.right > x && rect.top < y && rect.bottom > y
    }

    /**
     * Draws the text block annotations for position, size, and raw value on the supplied canvas.
     */
    override fun draw(canvas: Canvas) {
        // TODO: Draw the text onto the canvas.
        if (textBlock == null) {
            return
        }

        // Draws the bounding box around the TextBlock.
        val rect = RectF(textBlock.boundingBox)
        rect.left = translateX(rect.left)
        rect.top = translateY(rect.top)
        rect.right = translateX(rect.right)
        rect.bottom = translateY(rect.bottom)
        canvas.drawRect(rect, sRectPaint!!)

        // Break the text into multiple lines and draw each one according to its own bounding box.
        val textComponents = textBlock.components
        for (currentText in textComponents) {
            val left = translateX(currentText.boundingBox.left.toFloat())
            val bottom = translateY(currentText.boundingBox.bottom.toFloat())
            canvas.drawText(currentText.value, left, bottom, sTextPaint)
        }
    }

    companion object {

        private val TEXT_COLOR = Color.WHITE

        private var sRectPaint: Paint? = null
        private var sTextPaint: Paint? = null
    }
}