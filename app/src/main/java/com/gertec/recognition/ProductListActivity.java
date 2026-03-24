package com.gertec.recognition;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.gertec.recognition.uitls.ProductDatabase;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private ListView listViewProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        listViewProducts = findViewById(R.id.list_view_products);

        // Carrega todos os produtos do banco
        List<com.gertec.recognition.utils.Product> products = ProductDatabase.getInstance().getAllProducts();

        // Monta uma lista de strings com ID + Nome
        List<String> productNames = new ArrayList<>();
        for (com.gertec.recognition.utils.Product p : products) {
            productNames.add(p.getId());
        }

        // Exibe no ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                productNames
        );
        listViewProducts.setAdapter(adapter);
    }
}