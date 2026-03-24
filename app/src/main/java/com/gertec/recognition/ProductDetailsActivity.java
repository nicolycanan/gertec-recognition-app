package com.gertec.recognition;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.gertec.recognition.uitls.ProductDatabase;

public class ProductDetailsActivity extends AppCompatActivity {
    private TextView txtName;
    private TextView txtCategory;
    private TextView txtPrice;
    private TextView txtDescription;
    private TextView txtSpecifications;
    private TextView txtFeatures;
    private Button btnBack;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        txtName = findViewById(R.id.txt_name);
        txtCategory = findViewById(R.id.txt_category);
        txtPrice = findViewById(R.id.txt_price);
        txtDescription = findViewById(R.id.txt_description);
        txtSpecifications = findViewById(R.id.txt_specifications);
        txtFeatures = findViewById(R.id.txt_features);
        btnBack = findViewById(R.id.btn_back);

        String productId = getIntent().getStringExtra("product_id");
        com.gertec.recognition.utils.Product product = ProductDatabase.getInstance().getProductById(productId);

        if (product == null) {
            product = new com.gertec.recognition.utils.Product(
                    productId,
                    productId,
                    "Reconhecido",
                    0.0,
                    "Produto reconhecido pelo modelo",
                    "Informações não cadastradas",
                    "Sem detalhes adicionais"
            );
        }

        txtName.setText(product.getName());
        txtCategory.setText("Categoria: " + product.getCategory());
        txtPrice.setText("Preço: R$ " + product.getPrice());
        txtDescription.setText("Descrição: " + product.getDescription());
        txtSpecifications.setText("Especificações: " + product.getSpecifications().toString());
        txtFeatures.setText("Recursos: " + product.getFeatures().toString());

        btnBack.setOnClickListener(v -> finish());
    }
}