package com.example.zzt1;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;  // Picasso import

import java.util.List;

public class CartActivity extends AppCompatActivity {

    LinearLayout cartItemsLayout;
    TextView totalPriceView;
    Button checkoutButton;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuButton;  // Add ImageView for the menu button
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_cart);  // Ensure this matches your layout XML

        // Initialize UI elements
        cartItemsLayout = findViewById(R.id.cart_items_layout); // Update IDs based on your layout
        totalPriceView = findViewById(R.id.total_price);
        checkoutButton = findViewById(R.id.checkout_button);
        drawerLayout = findViewById(R.id.drawer_layout); // Initialize drawer layout
        navigationView = findViewById(R.id.nav_view); // Initialize navigation view
        menuButton = findViewById(R.id.menu_button); // Initialize the menu button

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        displayCartItems(ProductDetailActivity.cart); // Fetch data from the cart list
        calculateTotal(ProductDetailActivity.cart); // Calculate total price

        // Set menu button functionality to open the drawer
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Set navigation item selection listener
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    Toast.makeText(CartActivity.this, "Home selected", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CartActivity.this, MainActivity.class));
                }
                if (item.getItemId() == R.id.nav_booking) {
                    startActivity(new Intent(CartActivity.this, SecondActivity.class));
                }
                if (item.getItemId() == R.id.nav_faq) {
                    startActivity(new Intent(CartActivity.this, FAQ.class));
                }
                if (item.getItemId() == R.id.nav_cart) {
                    Toast.makeText(CartActivity.this, "You are already in the cart", Toast.LENGTH_SHORT).show();
                }
                if (item.getItemId() == R.id.nav_logout) {
                    auth.signOut();
                    Intent logoutIntent = new Intent(CartActivity.this, login.class);
                    logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(logoutIntent);
                    finish();
                }

                // Close the drawer after an item is selected
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // Handle checkout button click
        checkoutButton.setOnClickListener(v -> {
            Toast.makeText(CartActivity.this, "Proceeding to Checkout", Toast.LENGTH_SHORT).show();
            // Implement checkout logic here
        });
    }

    // Function to display cart items dynamically
    private void displayCartItems(List<CartItem> cartItems) {
        cartItemsLayout.removeAllViews(); // Clear previous views

        // Iterate over cart items and dynamically add them to the layout
        for (CartItem item : cartItems) {
            View itemView = getLayoutInflater().inflate(R.layout.cart_item_layout, null); // Replace with your cart item layout

            TextView productNameView = itemView.findViewById(R.id.cart_item_name);
            TextView productPriceView = itemView.findViewById(R.id.cart_item_price);
            TextView quantityView = itemView.findViewById(R.id.cart_item_quantity);
            Button increaseButton = itemView.findViewById(R.id.cart_item_increase);
            Button decreaseButton = itemView.findViewById(R.id.cart_item_decrease);
            ImageView productImageView = itemView.findViewById(R.id.cart_item_image); // Get the image view

            // Set product information
            productNameView.setText(item.getProductName());
            productPriceView.setText(String.format("R %.2f", item.getPrice()));
            quantityView.setText(String.valueOf(item.getQuantity()));

            // Load product image using Picasso (URL should be stored in the CartItem class)
            Picasso.get().load(item.getImage_name())
                    .placeholder(R.drawable.barrelbackground) // Ensure you have placeholder_image in res/drawable
                    .error(R.drawable.logo) // Ensure you have error_image in res/drawable
                    .into(productImageView);

            // Increase quantity
            increaseButton.setOnClickListener(v -> {
                item.setQuantity(item.getQuantity() + 1);
                quantityView.setText(String.valueOf(item.getQuantity()));
                calculateTotal(cartItems); // Recalculate total
            });

            // Decrease quantity
            decreaseButton.setOnClickListener(v -> {
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                    quantityView.setText(String.valueOf(item.getQuantity()));
                } else {
                    // If quantity reaches 0, remove the item from the cart
                    ProductDetailActivity.cart.remove(item);  // Remove item from the static cart list
                    cartItemsLayout.removeView(itemView);     // Remove the view from the layout
                    Toast.makeText(CartActivity.this, item.getProductName() + " removed from cart", Toast.LENGTH_SHORT).show();
                }
                calculateTotal(cartItems); // Recalculate total
            });

            // Add the dynamically created view to the cart layout
            cartItemsLayout.addView(itemView);
        }
    }

    // Function to calculate total price
    private void calculateTotal(List<CartItem> cartItems) {
        double total = 0;

        // Calculate the sum of prices for all items
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }

        // Update total price view
        totalPriceView.setText(String.format("Total: R %.2f", total));
    }
}
