package com.gertec.recognition;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gertec.recognition.utils.Product;
import com.gertec.recognition.utils.ProductDatabase;

import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {

    private static final String TAG = "ProductDetailsActivity";

    private TextView txtName;
    private TextView txtCategory;
    private TextView txtPrice;
    private TextView txtDescription;
    private TextView txtDetails;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        initViews();
        loadProduct();
    }

    private void initViews() {
        txtName = findViewById(R.id.txt_name);
        txtCategory = findViewById(R.id.txt_category);
        txtPrice = findViewById(R.id.txt_price);
        txtDescription = findViewById(R.id.txt_description);
        txtDetails = findViewById(R.id.txt_details);
        btnBack = findViewById(R.id.btn_back_details);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void loadProduct() {
        String productId = getIntent().getStringExtra("product_id");

        if (productId == null || productId.isEmpty()) {
            Log.e(TAG, "ID do produto inválido");
            Toast.makeText(this, "Produto inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Product product = ProductDatabase.getInstance().getProductById(productId);

        if (product == null) {
            Log.e(TAG, "Produto não encontrado: " + productId);
            Toast.makeText(this, "Produto não encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindProduct(product);
    }

    private void bindProduct(Product product) {
        txtName.setText(product.getName());
        txtCategory.setText("Categoria: " + product.getCategory());
        txtPrice.setText("Preço: R$ " + product.getPrice());
        txtDescription.setText(product.getDescription());

        // Format specifications and features into details
        StringBuilder details = new StringBuilder();
        
        if (product.getSpecifications() != null && !product.getSpecifications().isEmpty()) {
            details.append("Especificações:\n");
            for (Map.Entry<String, String> entry : product.getSpecifications().entrySet()) {
                details.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            details.append("\n");
        }

        if (product.getFeatures() != null && !product.getFeatures().isEmpty()) {
            details.append("Características:\n");
            for (String feature : product.getFeatures()) {
                details.append("- ").append(feature).append("\n");
            }
        }

        if (details.length() == 0) {
            details.append("Sem detalhes adicionais.");
        }

        txtDetails.setText(details.toString());
    }
}
