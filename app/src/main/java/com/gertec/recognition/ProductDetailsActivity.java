package com.gertec.recognition;

<<<<<<< HEAD
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
=======
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {
    private TextView productName;
    private TextView productCategory;
    private TextView productPrice;
    private TextView productDescription;
    private LinearLayout specificationsContainer;
    private LinearLayout featuresContainer;
    private Toolbar toolbar;

>>>>>>> 092c94a (Primeiro commit do projeto)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

<<<<<<< HEAD
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
=======
        productName = findViewById(R.id.product_name);
        productCategory = findViewById(R.id.product_category);
        productPrice = findViewById(R.id.product_price);
        productDescription = findViewById(R.id.product_description);
        specificationsContainer = findViewById(R.id.specifications_container);
        featuresContainer = findViewById(R.id.features_container);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String productId = getIntent().getStringExtra("product_id");
        if (productId != null) {
            displayProduct(productId);
        }
    }

    private void displayProduct(String productId) {
        Product product = ProductDatabase.getInstance().getProductById(productId);

        if (product != null) {
            productName.setText(product.getName());
            productCategory.setText(product.getCategory());
            productPrice.setText(product.getPrice());
            productDescription.setText(product.getDescription());

            // Display specifications
            if (product.getSpecifications() != null) {
                for (Map.Entry<String, String> entry : product.getSpecifications().entrySet()) {
                    addSpecificationRow(entry.getKey(), entry.getValue());
                }
            }

            // Display features
            if (product.getFeatures() != null) {
                for (String feature : product.getFeatures()) {
                    addFeatureRow(feature);
                }
            }
        } else {
            productName.setText("Produto não encontrado");
        }
    }

    private void addSpecificationRow(String key, String value) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 8, 0, 8);

        TextView keyView = new TextView(this);
        keyView.setText(capitalizeKey(key) + ":");
        keyView.setTextSize(14);
        keyView.setTextColor(getResources().getColor(R.color.primary, null));
        keyView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f));

        TextView valueView = new TextView(this);
        valueView.setText(value);
        valueView.setTextSize(14);
        valueView.setTextColor(getResources().getColor(R.color.on_background, null));
        valueView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.6f));

        row.addView(keyView);
        row.addView(valueView);
        specificationsContainer.addView(row);
    }

    private void addFeatureRow(String feature) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(16, 8, 0, 8);

        TextView bulletView = new TextView(this);
        bulletView.setText("• ");
        bulletView.setTextSize(14);
        bulletView.setTextColor(getResources().getColor(R.color.secondary, null));

        TextView featureView = new TextView(this);
        featureView.setText(feature);
        featureView.setTextSize(14);
        featureView.setTextColor(getResources().getColor(R.color.on_background, null));

        row.addView(bulletView);
        row.addView(featureView);
        featuresContainer.addView(row);
    }

    private String capitalizeKey(String key) {
        if (key == null || key.isEmpty()) {
            return key;
        }
        return key.substring(0, 1).toUpperCase() + key.substring(1).toLowerCase();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
>>>>>>> 092c94a (Primeiro commit do projeto)
