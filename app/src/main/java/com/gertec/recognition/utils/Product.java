package com.gertec.recognition.utils;

import java.util.List;
import java.util.Map;

public class Product {
    private String id;
    private String name;
    private String category;
    private String description;
    private Map<String, String> specifications;
    private String price;
    private List<String> features;

    public Product() {
    }

    public Product(String id, String name, String category, String description,
                   Map<String, String> specifications, String price, List<String> features) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.specifications = specifications;
        this.price = price;
        this.features = features;
    }

    // Constructor for fallback/manual creation
    public Product(String id, String name, String category,
                   double price, String description,
                   String specificationFallback, String featureFallback) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = String.valueOf(price);
        this.description = description;
        this.specifications = Map.of("info", specificationFallback);
        this.features = List.of(featureFallback);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Map<String, String> getSpecifications() { return specifications; }
    public void setSpecifications(Map<String, String> specifications) { this.specifications = specifications; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features; }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
