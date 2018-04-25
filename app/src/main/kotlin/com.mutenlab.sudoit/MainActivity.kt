package com.mutenlab.sudoit

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.mutenlab.sudoit.ui.CameraActivity
import com.mutenlab.sudoit.utils.PermissionHelper

class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_MEDIA_PERMISSIONS = 100
        private val TAG = MainActivity::class.qualifiedName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnStartOcr = findViewById<Button>(R.id.start_ocr)
        btnStartOcr.setOnClickListener {
            checkCameraPermission()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_MEDIA_PERMISSIONS -> if (grantResults.size == 2
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                launchCamera()
            } else {
                Toast.makeText(this, getString(R.string.camera_request_rationale),
                        Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkCameraPermission() {
        val mediaPermissions = arrayOf(Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (!PermissionHelper.checkPermissions(this, mediaPermissions)) {
            Log.i(MainActivity.TAG, "Permission to media denied")
            ActivityCompat.requestPermissions(this, mediaPermissions, REQUEST_MEDIA_PERMISSIONS)
        } else {
            launchCamera()
        }
    }

    private fun launchCamera() {
        val intent = Intent(applicationContext, CameraActivity::class.java)
        startActivity(intent)
    }
}
