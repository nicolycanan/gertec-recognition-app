package com.gertec.recognition;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.gertec.recognition.uitls.ProductDatabase;
import com.gertec.recognition.utils.Product;
import com.gertec.recognition.utils.TFLiteHelper;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "CameraActivity";
    private PreviewView previewView;
    private Button btnCapture;
    private Button btnBack;
    private TextView detectionStatus;
    private ImageCapture imageCapture;
    private ProcessCameraProvider cameraProvider;

    private TFLiteHelper tfliteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.preview_view);
        btnCapture = findViewById(R.id.btn_capture);
        btnBack = findViewById(R.id.btn_back);
        detectionStatus = findViewById(R.id.detection_status);

        try {
            // Construtor com dois parâmetros (contexto + nome do modelo)
            tfliteHelper = new TFLiteHelper(this, "product_model.tflite");
        } catch (Exception e) {
            Log.e(TAG, "Erro ao carregar modelo TFLite: " + e.getMessage());
            Toast.makeText(this, "Falha ao carregar modelo", Toast.LENGTH_LONG).show();
        }

        btnCapture.setOnClickListener(v -> captureImage());
        btnBack.setOnClickListener(v -> finish());

        startCamera();
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Erro ao iniciar câmera: " + e.getMessage());
            }
        }, getExecutor());
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        imageCapture = new ImageCapture.Builder()
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        try {
            cameraProvider.unbindAll();
            Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao vincular câmera: " + e.getMessage());
        }
    }

    private void captureImage() {
        if (imageCapture == null) return;

        File photoFile = new File(getCacheDir(), "captured_image.jpg");

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile)
                .build();

        imageCapture.takePicture(outputOptions, getExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        Log.d(TAG, "Imagem capturada com sucesso");
                        processImage(photoFile);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "Falha ao capturar imagem: " + exception.getMessage());
                        Toast.makeText(CameraActivity.this, "Erro ao capturar imagem", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void processImage(File imageFile) {
        try {
            if (tfliteHelper == null) {
                Toast.makeText(this, "Modelo não carregado", Toast.LENGTH_SHORT).show();
                return;
            }

            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            String detectedLabel = tfliteHelper.recognizeImage(bitmap);   // ✅ precisa existir no TFLiteHelper

            if (detectedLabel.endsWith("_preprocessed")) {
                detectedLabel = detectedLabel.replace("_preprocessed", "");
            }

            Log.d(TAG, "Detectado: " + detectedLabel);

            Product product = ProductDatabase.getInstance().getProductById(detectedLabel);

            if (product == null) {
                List<String> labels = TFLiteHelper.loadLabels(this, "labels.txt");  // ✅ precisa existir no TFLiteHelper
                if (labels.contains(detectedLabel)) {
                    product = new Product(
                            detectedLabel,
                            detectedLabel,
                            "Reconhecido",
                            0.0,
                            "Produto reconhecido pelo modelo",
                            "Informações não cadastradas",
                            "Sem detalhes adicionais"
                    );
                }
            }

            if (product != null) {
                Intent intent = new Intent(CameraActivity.this, ProductDetailsActivity.class);
                intent.putExtra("product_id", product.getId());
                startActivity(intent);
            } else {
                detectionStatus.setText("Produto não encontrado: " + detectedLabel);
                Toast.makeText(CameraActivity.this, "Produto não encontrado", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "Erro ao processar imagem: " + e.getMessage());
        }
    }

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tfliteHelper != null) {
            tfliteHelper.close();   // ✅ precisa existir no TFLiteHelper
        }
    }
}