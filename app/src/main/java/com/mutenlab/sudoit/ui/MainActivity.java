package com.mutenlab.sudoit.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mutenlab.sudoit.R;
import com.mutenlab.sudoit.util.PermissionHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author icerrate
 */
public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_CAMERA_PERMISSION = 100;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
    }

    @OnClick(R.id.start_camera)
    public void startCameraClick(View view) {
        String[] permission = new String[]{Manifest.permission.CAMERA};

        if (!PermissionHelper.checkPermissions(this, permission)) {
            ActivityCompat.requestPermissions(this, permission, REQUEST_CAMERA_PERMISSION);
        } else {
            startActivity(new Intent(this, CameraActivity.class));
        }
    }
}
