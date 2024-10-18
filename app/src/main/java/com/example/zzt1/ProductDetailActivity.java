package com.example.zzt1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView menuButton;
    private TextView productName;
    private TextView productPrice;
    private TextView productDescription;
    private ImageView productImage;  // Add ImageView for product image
    private Button addToCartButton;  // Add this line for the button
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    public static List<CartItem> cart = new ArrayList<>();  // Store cart items

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_products);  // Ensure this layout has the navigation drawer

        // Initialize UI elements
        menuButton = findViewById(R.id.menu_button);
        productImage = findViewById(R.id.product_image);  // Initialize product image view
        productName = findViewById(R.id.product_name);
        productPrice = findViewById(R.id.product_price);
        productDescription = findViewById(R.id.product_description);
        addToCartButton = findViewById(R.id.add_to_cart_button);  // Initialize button
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Set menu button functionality to open the drawer
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Handle Add to Cart button click
        addToCartButton.setOnClickListener(v -> {
            String name = productName.getText().toString();
            double price = Double.parseDouble(productPrice.getText().toString().replace("R ", ""));
            String imageUrl = "default_image_url";  // You can set this dynamically as well

            // Check if item is already in the cart
            boolean itemExists = false;
            for (CartItem item : cart) {
                if (item.getProductName().equals(name)) {
                    item.setQuantity(item.getQuantity() + 1);  // Increase quantity if it exists
                    itemExists = true;
                    break;
                }
            }

            // If not in the cart, add new item
            if (!itemExists) {
                CartItem cartItem = new CartItem(name, price, imageUrl, 1);
                cart.add(cartItem);
            }

            // Show a confirmation message
            Toast.makeText(ProductDetailActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
        });

        // Set navigation item selection listener
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    Toast.makeText(ProductDetailActivity.this, "Home selected", Toast.LENGTH_SHORT).show();
                    Intent homeIntent = new Intent(ProductDetailActivity.this, MainActivity.class);
                    startActivity(homeIntent);
                }

                if (id == R.id.nav_booking) {
                    Intent bookingIntent = new Intent(ProductDetailActivity.this, SecondActivity.class);
                    startActivity(bookingIntent);
                }

                if (item.getItemId() == R.id.nav_faq) {
                    // Navigate to the FAQ Activity
                    Intent intent = new Intent(ProductDetailActivity.this, FAQ.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.nav_cart) {
                    Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
                    startActivity(intent);
                }
                if (id == R.id.nav_logout) {
                    auth.signOut();
                    Intent logoutIntent = new Intent(ProductDetailActivity.this, login.class);
                    logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(logoutIntent);
                    finish();  // Close the current activity
                }

                // Close the drawer after an item is selected
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // Fetch product details
        fetchProductDetails();
    }

    private void fetchProductDetails() {
        // Get the product name passed through the Intent
        String productNameStr = getIntent().getStringExtra("PRODUCT_NAME");

        if (productNameStr != null) {
            // Query Firestore to find the product by its name
            firestore.collection("products")
                    .whereEqualTo("product_name", productNameStr)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            QueryDocumentSnapshot documentSnapshot = (QueryDocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0);

                            // Fetch the data from the document
                            String name = documentSnapshot.getString("product_name");
                            double price = documentSnapshot.getDouble("price");
                            String formattedPrice = String.format("R %.2f", price);  // Format price to 2 decimal places
                            String description = documentSnapshot.getString("description");
                            String imageUrl = documentSnapshot.getString("image_name");

                            // Set data to the views
                            productName.setText(name);
                            productPrice.setText(formattedPrice);
                            productDescription.setText(description);

                            // Load the image using Picasso
                            Picasso.get().load(imageUrl).into(productImage);
                        } else {
                            Toast.makeText(ProductDetailActivity.this, "Product not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ProductDetailActivity", "Error fetching product details", e);
                        Toast.makeText(ProductDetailActivity.this, "Error fetching product details", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Product name is missing", Toast.LENGTH_SHORT).show();
        }
    }
}
