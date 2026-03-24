package com.gertec.recognition;

import android.content.Intent;
import android.graphics.Bitmap;
<<<<<<< HEAD
import android.graphics.BitmapFactory;
=======
>>>>>>> 092c94a (Primeiro commit do projeto)
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
<<<<<<< HEAD

=======
>>>>>>> 092c94a (Primeiro commit do projeto)
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
<<<<<<< HEAD

import com.gertec.recognition.uitls.ProductDatabase;
import com.gertec.recognition.utils.Product;
import com.gertec.recognition.utils.TFLiteHelper;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.List;
=======
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import java.io.File;
>>>>>>> 092c94a (Primeiro commit do projeto)
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
<<<<<<< HEAD

    private TFLiteHelper tfliteHelper;
=======
    private ImageLabeler imageLabeler;
>>>>>>> 092c94a (Primeiro commit do projeto)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.preview_view);
        btnCapture = findViewById(R.id.btn_capture);
        btnBack = findViewById(R.id.btn_back);
        detectionStatus = findViewById(R.id.detection_status);

<<<<<<< HEAD
        try {
            // Construtor com dois parâmetros (contexto + nome do modelo)
            tfliteHelper = new TFLiteHelper(this, "product_model.tflite");
        } catch (Exception e) {
            Log.e(TAG, "Erro ao carregar modelo TFLite: " + e.getMessage());
            Toast.makeText(this, "Falha ao carregar modelo", Toast.LENGTH_LONG).show();
        }
=======
        // Initialize ML Kit Image Labeler
        ImageLabelerOptions options = new ImageLabelerOptions.Builder()
                .setConfidenceThreshold(0.5f)
                .build();
        imageLabeler = ImageLabeling.getClient(options);
>>>>>>> 092c94a (Primeiro commit do projeto)

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
<<<<<<< HEAD
                Log.e(TAG, "Erro ao iniciar câmera: " + e.getMessage());
=======
                Log.e(TAG, "Error starting camera: " + e.getMessage());
>>>>>>> 092c94a (Primeiro commit do projeto)
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
<<<<<<< HEAD
            Log.e(TAG, "Erro ao vincular câmera: " + e.getMessage());
=======
            Log.e(TAG, "Error binding camera: " + e.getMessage());
>>>>>>> 092c94a (Primeiro commit do projeto)
        }
    }

    private void captureImage() {
<<<<<<< HEAD
        if (imageCapture == null) return;
=======
        if (imageCapture == null) {
            return;
        }
>>>>>>> 092c94a (Primeiro commit do projeto)

        File photoFile = new File(getCacheDir(), "captured_image.jpg");

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile)
                .build();

        imageCapture.takePicture(outputOptions, getExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
<<<<<<< HEAD
                        Log.d(TAG, "Imagem capturada com sucesso");
=======
                        Log.d(TAG, "Image captured successfully");
>>>>>>> 092c94a (Primeiro commit do projeto)
                        processImage(photoFile);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
<<<<<<< HEAD
                        Log.e(TAG, "Falha ao capturar imagem: " + exception.getMessage());
=======
                        Log.e(TAG, "Image capture failed: " + exception.getMessage());
>>>>>>> 092c94a (Primeiro commit do projeto)
                        Toast.makeText(CameraActivity.this, "Erro ao capturar imagem", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void processImage(File imageFile) {
        try {
<<<<<<< HEAD
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
=======
            Bitmap bitmap = android.graphics.BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            // Updated InputImage.fromBitmap to include rotation degree (0)
            InputImage image = InputImage.fromBitmap(bitmap, 0);

            imageLabeler.process(image)
                    .addOnSuccessListener(labels -> {
                        if (labels.isEmpty()) {
                            detectionStatus.setText("Nenhum produto detectado");
                            Toast.makeText(CameraActivity.this, "Produto não identificado", Toast.LENGTH_SHORT).show();
                        } else {
                            String detectedLabel = labels.get(0).getText();
                            float confidence = labels.get(0).getConfidence();
                            Log.d(TAG, "Detected: " + detectedLabel + " (" + confidence + ")");

                            // Try to find product by detected label
                            Product product = ProductDatabase.getInstance().getProductById(detectedLabel);

                            if (product != null) {
                                Intent intent = new Intent(CameraActivity.this, ProductDetailsActivity.class);
                                intent.putExtra("product_id", product.getId());
                                startActivity(intent);
                            } else {
                                // Try searching by name
                                product = ProductDatabase.getInstance().getProductByName(detectedLabel);
                                if (product != null) {
                                    Intent intent = new Intent(CameraActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("product_id", product.getId());
                                    startActivity(intent);
                                } else {
                                    detectionStatus.setText("Produto não encontrado: " + detectedLabel);
                                    Toast.makeText(CameraActivity.this, "Produto não encontrado", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error processing image: " + e.getMessage());
                        Toast.makeText(CameraActivity.this, "Erro ao processar imagem", Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error loading image: " + e.getMessage());
>>>>>>> 092c94a (Primeiro commit do projeto)
        }
    }

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
<<<<<<< HEAD
        if (tfliteHelper != null) {
            tfliteHelper.close();   // ✅ precisa existir no TFLiteHelper
        }
    }
}
=======
        if (imageLabeler != null) {
            try {
                imageLabeler.close();
            } catch (Exception e) {
                Log.e(TAG, "Error closing image labeler: " + e.getMessage());
            }
        }
    }
}
>>>>>>> 092c94a (Primeiro commit do projeto)
