package com.gertec.recognition;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.gertec.recognition.utils.ProductDatabase;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 100;
    private MaterialButton btnOpenCamera;
    private MaterialButton btnProductList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ProductDatabase
        ProductDatabase.getInstance().loadProducts(this);

        btnOpenCamera = findViewById(R.id.btn_open_camera);
        btnProductList = findViewById(R.id.btn_product_list);

        btnOpenCamera.setOnClickListener(v -> openCamera());
        btnProductList.setOnClickListener(v -> openProductList());
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_CODE);
            }
        } else {
            startActivity(new Intent(this, CameraActivity.class));
        }
    }

    private void openProductList() {
        startActivity(new Intent(this, ProductListActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this, CameraActivity.class));
            }
        }
    }
}
