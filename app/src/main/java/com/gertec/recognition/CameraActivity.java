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
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.gertec.recognition.utils.Product;
import com.gertec.recognition.utils.ProductDatabase;
import com.gertec.recognition.utils.TFLiteHelper;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class CameraActivity extends AppCompatActivity {

    private static final String TAG = "CameraActivity";
    private static final String MODEL_FILE = "product_model.tflite";
    private static final String LABELS_FILE = "labels.txt";

    private PreviewView previewView;
    private Button btnCapture;
    private Button btnBack;
    private TextView detectionStatus;

    private ImageCapture imageCapture;
    private ProcessCameraProvider cameraProvider;

    private TFLiteHelper tfliteHelper;
    private ImageLabeler imageLabeler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        initViews();
        initTFLite();
        initMlKit();
        setupListeners();
        startCamera();
    }

    private void initViews() {
        previewView = findViewById(R.id.preview_view);
        btnCapture = findViewById(R.id.btn_capture);
        btnBack = findViewById(R.id.btn_back);
        detectionStatus = findViewById(R.id.detection_status);
    }

    private void initTFLite() {
        try {
            tfliteHelper = new TFLiteHelper(this, MODEL_FILE);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao carregar modelo TFLite", e);
            Toast.makeText(this, "Falha ao carregar modelo TFLite", Toast.LENGTH_LONG).show();
        }
    }

    private void initMlKit() {
        try {
            ImageLabelerOptions options = new ImageLabelerOptions.Builder()
                    .setConfidenceThreshold(0.5f)
                    .build();

            imageLabeler = ImageLabeling.getClient(options);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao inicializar ML Kit", e);
            Toast.makeText(this, "Falha ao carregar ML Kit", Toast.LENGTH_LONG).show();
        }
    }

    private void setupListeners() {
        btnCapture.setOnClickListener(v -> captureImage());
        btnBack.setOnClickListener(v -> finish());
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Erro ao iniciar câmera", e);
                Toast.makeText(this, "Erro ao iniciar câmera", Toast.LENGTH_SHORT).show();
            }
        }, getExecutor());
    }

    private void bindPreview(@NonNull ProcessCameraProvider provider) {
        Preview preview = new Preview.Builder().build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        imageCapture = new ImageCapture.Builder().build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        try {
            provider.unbindAll();
            provider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao vincular câmera", e);
            Toast.makeText(this, "Erro ao configurar câmera", Toast.LENGTH_SHORT).show();
        }
    }

    private void captureImage() {
        if (imageCapture == null) {
            Toast.makeText(this, "Câmera não inicializada", Toast.LENGTH_SHORT).show();
            return;
        }

        File photoFile = new File(getCacheDir(), "captured_image.jpg");

        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(
                outputOptions,
                getExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Log.d(TAG, "Imagem capturada com sucesso");
                        processImage(photoFile);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "Falha ao capturar imagem", exception);
                        Toast.makeText(CameraActivity.this, "Erro ao capturar imagem", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void processImage(File imageFile) {
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

        if (bitmap == null) {
            Log.e(TAG, "Não foi possível decodificar a imagem capturada");
            Toast.makeText(this, "Erro ao carregar imagem", Toast.LENGTH_SHORT).show();
            return;
        }

        if (tfliteHelper == null) {
            Log.w(TAG, "TFLiteHelper não inicializado. Usando fallback com ML Kit.");
            processImageWithMlKit(bitmap);
            return;
        }

        try {
            String detectedLabel = tfliteHelper.recognizeImage(bitmap);

            if (detectedLabel != null) {
                detectedLabel = sanitizeLabel(detectedLabel);
            }

            if (detectedLabel == null || detectedLabel.trim().isEmpty()) {
                Log.w(TAG, "Nenhum label válido retornado pelo TFLite");
                processImageWithMlKit(bitmap);
                return;
            }

            Log.d(TAG, "Detectado via TFLite: " + detectedLabel);

            Product product = findProductByLabel(detectedLabel);

            if (product != null) {
                detectionStatus.setText("Produto detectado: " + product.getName());
                openProductDetails(product);
            } else {
                detectionStatus.setText("Produto não encontrado: " + detectedLabel);
                Toast.makeText(this, "Produto não encontrado no banco local", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "Erro no TFLite, tentando fallback com ML Kit", e);
            processImageWithMlKit(bitmap);
        }
    }

    private void processImageWithMlKit(Bitmap bitmap) {
        if (imageLabeler == null) {
            Log.e(TAG, "ML Kit não inicializado");
            Toast.makeText(this, "Reconhecimento indisponível", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            InputImage image = InputImage.fromBitmap(bitmap, 0);

            imageLabeler.process(image)
                    .addOnSuccessListener(labels -> {
                        if (labels == null || labels.isEmpty()) {
                            detectionStatus.setText("Nenhum produto detectado");
                            Toast.makeText(CameraActivity.this, "Produto não identificado", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String detectedLabel = labels.get(0).getText();
                        float confidence = labels.get(0).getConfidence();

                        if (detectedLabel != null) {
                            detectedLabel = sanitizeLabel(detectedLabel);
                        }

                        Log.d(TAG, "Detectado via ML Kit: " + detectedLabel + " (" + confidence + ")");

                        Product product = findProductByLabel(detectedLabel);

                        if (product != null) {
                            detectionStatus.setText("Produto detectado: " + product.getName());
                            openProductDetails(product);
                        } else {
                            detectionStatus.setText("Produto não encontrado: " + detectedLabel);
                            Toast.makeText(CameraActivity.this, "Produto não encontrado", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Erro ao processar imagem com ML Kit", e);
                        detectionStatus.setText("Erro ao processar imagem");
                        Toast.makeText(CameraActivity.this, "Erro ao processar imagem", Toast.LENGTH_SHORT).show();
                    });

        } catch (Exception e) {
            Log.e(TAG, "Erro ao preparar fallback com ML Kit", e);
            Toast.makeText(this, "Erro ao carregar imagem", Toast.LENGTH_SHORT).show();
        }
    }

    private Product findProductByLabel(String detectedLabel) {
        if (detectedLabel == null || detectedLabel.trim().isEmpty()) {
            return null;
        }

        Product product = ProductDatabase.getInstance().getProductById(detectedLabel);

        if (product == null) {
            product = ProductDatabase.getInstance().getProductByName(detectedLabel);
        }

        if (product == null) {
            try {
                List<String> labels = TFLiteHelper.loadLabels(this, LABELS_FILE);
                if (labels != null && labels.contains(detectedLabel)) {
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
            } catch (Exception e) {
                Log.e(TAG, "Erro ao carregar labels", e);
            }
        }

        return product;
    }

    private String sanitizeLabel(String label) {
        if (label == null) {
            return null;
        }

        label = label.trim();

        if (label.endsWith("_preprocessed")) {
            label = label.replace("_preprocessed", "");
        }

        return label.trim();
    }

    private void openProductDetails(Product product) {
        if (product == null) {
            Toast.makeText(this, "Produto inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(CameraActivity.this, ProductDetailsActivity.class);
        intent.putExtra("product_id", product.getId());
        startActivity(intent);
    }

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (tfliteHelper != null) {
            try {
                tfliteHelper.close();
            } catch (Exception e) {
                Log.e(TAG, "Erro ao fechar TFLiteHelper", e);
            }
        }

        if (imageLabeler != null) {
            try {
                imageLabeler.close();
            } catch (Exception e) {
                Log.e(TAG, "Erro ao fechar ImageLabeler", e);
            }
        }
    }
}
