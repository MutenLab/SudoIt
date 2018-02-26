package com.mutenlab.sudoit

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var btnStartOcr = findViewById<Button>(R.id.start_ocr)
        btnStartOcr.setOnClickListener {
            val intent = Intent(applicationContext, OcrCaptureActivity::class.java)
            startActivity(intent)
        }
    }
}
