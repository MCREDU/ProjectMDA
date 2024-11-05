package com.example.zzt1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    LinearLayout cartItemsLayout;
    TextView totalPriceView;
    Button checkoutButton;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuButton;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_cart);

        // Initialize UI elements
        cartItemsLayout = findViewById(R.id.cart_items_layout);
        totalPriceView = findViewById(R.id.total_price);
        checkoutButton = findViewById(R.id.checkout_button);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        menuButton = findViewById(R.id.menu_button);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        displayCartItems(ProductDetailActivity.cart);
        calculateTotal(ProductDetailActivity.cart);

        // Set menu button functionality to open the drawer
        menuButton.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        // Set navigation item selection listener as in original code
        navigationView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(getNavigationIntent(item.getItemId()));
            return true;
        });

        // Handle checkout button click
        checkoutButton.setOnClickListener(v -> {
            Toast.makeText(CartActivity.this, "Proceeding to Checkout", Toast.LENGTH_SHORT).show();
            initiateCheckout();
        });
    }

    private Intent getNavigationIntent(int itemId) {
        if (itemId == R.id.nav_home) {
            return new Intent(CartActivity.this, MainActivity.class);
        }
        if (itemId == R.id.nav_booking) {
            return new Intent(CartActivity.this, SecondActivity.class);
        }
        if (itemId == R.id.nav_faq) {
            return new Intent(CartActivity.this, FAQ.class);
        }
        if (itemId == R.id.nav_cart) {
            Toast.makeText(CartActivity.this, "You are already in the cart", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (itemId == R.id.nav_logout) {
            auth.signOut();
            Intent logoutIntent = new Intent(CartActivity.this, login.class);
            logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            finish();
            return logoutIntent;
        }
        return null;
    }

    // Function to display cart items dynamically
    private void displayCartItems(List<CartItem> cartItems) {
        cartItemsLayout.removeAllViews();

        for (CartItem item : cartItems) {
            View itemView = getLayoutInflater().inflate(R.layout.cart_item_layout, null);

            TextView productNameView = itemView.findViewById(R.id.cart_item_name);
            TextView productPriceView = itemView.findViewById(R.id.cart_item_price);
            TextView quantityView = itemView.findViewById(R.id.cart_item_quantity);
            Button increaseButton = itemView.findViewById(R.id.cart_item_increase);
            Button decreaseButton = itemView.findViewById(R.id.cart_item_decrease);
            ImageView productImageView = itemView.findViewById(R.id.cart_item_image);

            productNameView.setText(item.getProductName());
            productPriceView.setText(String.format(Locale.US, "R %.2f", item.getPrice()));
            quantityView.setText(String.valueOf(item.getQuantity()));

            Picasso.get().load(item.getImage_name())
                    .placeholder(R.drawable.wine)
                    .error(R.drawable.wine)
                    .into(productImageView);

            increaseButton.setOnClickListener(v -> {
                item.setQuantity(item.getQuantity() + 1);
                quantityView.setText(String.valueOf(item.getQuantity()));
                calculateTotal(cartItems);
            });

            decreaseButton.setOnClickListener(v -> {
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                    quantityView.setText(String.valueOf(item.getQuantity()));
                } else {
                    ProductDetailActivity.cart.remove(item);
                    cartItemsLayout.removeView(itemView);
                    Toast.makeText(CartActivity.this, item.getProductName() + " removed from cart", Toast.LENGTH_SHORT).show();
                }
                calculateTotal(cartItems);
            });

            cartItemsLayout.addView(itemView);
        }
    }

    // Function to calculate total price
    private void calculateTotal(List<CartItem> cartItems) {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        totalPriceView.setText(String.format(Locale.US, "Total: R %.2f", total));
    }

    // Function to initiate checkout
    private void initiateCheckout() {
        // backend URL for paygate
        String backendUrl = "https://zzt1-67b8b.uc.r.appspot.com/create-checkout"; // Update with your actual backend URL
        int amount = calculateCartTotal(); // Calculate the total amount from the cart in cents
        String currency = "ZAR"; // Set currency (ZAR for South African Rand)

        // Create JSON object for the checkout request
        JSONObject checkoutDetails = new JSONObject();
        try {
            checkoutDetails.put("amount", amount); // Already in cents
            checkoutDetails.put("currency", currency);
        } catch (JSONException e) {
            Log.e("CartActivity", "Error creating JSON object", e);
            Toast.makeText(this, "Error preparing payment request", Toast.LENGTH_SHORT).show();
            return; // Exit if there’s an error creating the JSON object
        }

        // creates a request queue
        RequestQueue queue = Volley.newRequestQueue(this);

        // Creates the JSON Object Request for the Yoco API
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, backendUrl, checkoutDetails,
                response -> {
                    try {
                        // Log the entire response for debugging
                        Log.d("CartActivity", "Checkout response: " + response.toString());

                        // Gets the message from the response
                        String message = response.optString("message", "Proceeding To Payment");
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show(); // Displays the message

                        // Starts the checkout process by opening the URL in a browser
                        String redirectUrl = response.optString("redirectUrl", "");
                        if (!redirectUrl.isEmpty()) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(redirectUrl)));
                        } else {
                            Log.e("CartActivity", "Redirect URL is missing in the response");
                            Toast.makeText(this, "Payment redirection URL is missing", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("CartActivity", "Error parsing JSON response", e);
                        Toast.makeText(this, "Error processing payment response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Log the entire error for debugging
                    Log.e("CartActivity", "Error during request: " + error.toString());
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        Log.e("CartActivity", "Error response: " + new String(error.networkResponse.data));
                    }
                    Toast.makeText(this, "Checkout request failed", Toast.LENGTH_SHORT).show();
                });

        // Add the request to the queue
        queue.add(jsonObjectRequest);
    }


    private int calculateCartTotal() {
        double total = 0;
        for (CartItem item : ProductDetailActivity.cart) {
            total += item.getPrice() * item.getQuantity();
        }
        int amountInCents = (int) Math.round(total * 100);  // Converts the item amount to cents
        Log.d("CartActivity", "Total amount in cents: " + amountInCents);
        return amountInCents;
    }
}
