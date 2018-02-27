package com.mutenlab.sudoit.ui

import android.util.Log
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.mutenlab.sudoit.ui.camera.GraphicOverlay

/**
 * A very simple Processor which gets detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 * TODO: Make this implement Detector.Processor<TextBlock> and add text to the GraphicOverlay
</TextBlock> */
class OcrDetectorProcessor internal constructor(private val mGraphicOverlay: GraphicOverlay<OcrGraphic>) : Detector.Processor<TextBlock> {

    override fun receiveDetections(detections: Detector.Detections<TextBlock>) {
        mGraphicOverlay.clear()
        val items = detections.detectedItems
        for (i in 0..items.size() - 1) {
            val item = items.valueAt(i)
            if (item != null && item.value != null) {
                Log.d("Processor", "Text detected! " + item.value)
            }
            val graphic = OcrGraphic(mGraphicOverlay, item)
            mGraphicOverlay.add(graphic)
        }
    }

    override fun release() {
        mGraphicOverlay.clear()
    }
    // TODO:  Once this implements Detector.Processor<TextBlock>, implement the abstract methods.
}