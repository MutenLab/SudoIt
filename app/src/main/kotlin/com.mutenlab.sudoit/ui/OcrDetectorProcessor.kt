package com.mutenlab.sudoit.ui

import android.util.Log
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock

class OcrDetectorProcessor internal constructor() : Detector.Processor<TextBlock> {

    override fun receiveDetections(detections: Detector.Detections<TextBlock>) {
        val items = detections.detectedItems
        for (i in 0.until(items.size() - 1)) {
            val item = items.valueAt(i)
            if (item != null && item.value != null) {
                Log.d("Processor"+i+"/"+(items.size()-1), "Text detected! " + item.value)
            }
        }
    }

    override fun release() {
        //Nothing
    }
}