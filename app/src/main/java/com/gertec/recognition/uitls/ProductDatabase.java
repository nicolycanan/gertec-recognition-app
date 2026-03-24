package com.gertec.recognition.uitls;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDatabase {
    private static ProductDatabase instance;
    private List<com.gertec.recognition.utils.Product> products;
    private Map<String, com.gertec.recognition.utils.Product> productMap;

    private ProductDatabase() {
        products = new ArrayList<>();
        productMap = new HashMap<>();
    }

    public static synchronized ProductDatabase getInstance() {
        if (instance == null) {
            instance = new ProductDatabase();
        }
        return instance;
    }

    public void loadProducts(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("products.json");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();

            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
            JsonArray productsArray = jsonObject.getAsJsonArray("products");

            for (int i = 0; i < productsArray.size(); i++) {
                com.gertec.recognition.utils.Product product = gson.fromJson(productsArray.get(i), com.gertec.recognition.utils.Product.class);
                products.add(product);
                productMap.put(product.getId().toUpperCase(), product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public com.gertec.recognition.utils.Product getProductById(String id) {
        return productMap.get(id.toUpperCase());
    }

    public com.gertec.recognition.utils.Product getProductByName(String name) {
        for (com.gertec.recognition.utils.Product product : products) {
            if (product.getName().equalsIgnoreCase(name)) {
                return product;
            }
        }
        return null;
    }

    public List<com.gertec.recognition.utils.Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public List<com.gertec.recognition.utils.Product> searchProducts(String query) {
        List<com.gertec.recognition.utils.Product> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        for (com.gertec.recognition.utils.Product product : products) {
            if (product.getId().toLowerCase().contains(lowerQuery) ||
                product.getName().toLowerCase().contains(lowerQuery) ||
                product.getCategory().toLowerCase().contains(lowerQuery)) {
                results.add(product);
            }
        }

        return results;
    }

    public void updateProduct(com.gertec.recognition.utils.Product product) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equals(product.getId())) {
                products.set(i, product);
                productMap.put(product.getId().toUpperCase(), product);
                break;
            }
        }
    }
}
