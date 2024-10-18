package com.example.zzt1;

public class CartItem {
    private String productName;
    private double price;
    private String image_name;
    private int quantity;

    // Constructor
    public CartItem(String productName, double price, String image_name, int quantity) {
        this.productName = productName;
        this.price = price;
        this.image_name = image_name; // Initialize imageUrl
        this.quantity = quantity;
    }

    // Getter for productName
    public String getProductName() {
        return productName;
    }

    // Setter for productName
    public void setProductName(String productName) {
        this.productName = productName;
    }

    // Getter for price
    public double getPrice() {
        return price;
    }

    // Setter for price
    public void setPrice(double price) {
        this.price = price;
    }

    // Getter for imageUrl
    public String getImage_name() {
        return image_name;
    }

    // Setter for imageUrl
    public void setImage_name(String Image_name) {
        this.image_name = Image_name;
    }

    // Getter for quantity
    public int getQuantity() {
        return quantity;
    }

    // Setter for quantity
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
