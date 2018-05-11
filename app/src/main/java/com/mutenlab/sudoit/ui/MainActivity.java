package com.mutenlab.sudoit.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mutenlab.sudoit.R;
import com.mutenlab.sudoit.util.PermissionHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author icerrate
 */
public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();

    private final static int REQUEST_CAMERA_PERMISSION = 100;

    @BindView(R.id.start_camera)
    Button startCamera;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        startCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCameraPermissions();
            }
        });
    }

    private void checkCameraPermissions() {
        String[] permission = new String[]{Manifest.permission.CAMERA};

        if (!PermissionHelper.checkPermissions(this, permission)) {
            Log.i(TAG, "Permission to media denied");
            ActivityCompat.requestPermissions(this, permission, REQUEST_CAMERA_PERMISSION);
        } else {
            launchCamera();
        }
    }

    private void launchCamera() {
        startActivity(new Intent(this, CameraActivity.class));
    }
}
