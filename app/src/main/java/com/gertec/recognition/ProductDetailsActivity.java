package com.gertec.recognition;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView nameView = findViewById(R.id.product_name);
        TextView categoryView = findViewById(R.id.product_category);
        TextView priceView = findViewById(R.id.product_price);

        String productId = getIntent().getStringExtra("product_id");
        Product product = ProductDatabase.getInstance().getProductById(productId);

        if (product != null) {
            nameView.setText(product.getName());
            categoryView.setText(product.getCategory());
            priceView.setText("R$ " + product.getPrice());
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
