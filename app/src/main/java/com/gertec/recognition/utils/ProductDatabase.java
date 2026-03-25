package com.gertec.recognition.utils;

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
    private List<Product> products;
    private Map<String, Product> productMap;

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
                Product product = gson.fromJson(productsArray.get(i), Product.class);
                products.add(product);
                productMap.put(product.getId().toUpperCase(), product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Product getProductById(String id) {
        return productMap.get(id.toUpperCase());
    }

    public Product getProductByName(String name) {
        for (Product product : products) {
            if (product.getName().equalsIgnoreCase(name)) {
                return product;
            }
        }
        return null;
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public List<Product> searchProducts(String query) {
        List<Product> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        for (Product product : products) {
            if (product.getId().toLowerCase().contains(lowerQuery) ||
                product.getName().toLowerCase().contains(lowerQuery) ||
                product.getCategory().toLowerCase().contains(lowerQuery)) {
                results.add(product);
            }
        }

        return results;
    }

    public void updateProduct(Product product) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equals(product.getId())) {
                products.set(i, product);
                productMap.put(product.getId().toUpperCase(), product);
                break;
            }
        }
    }
}
