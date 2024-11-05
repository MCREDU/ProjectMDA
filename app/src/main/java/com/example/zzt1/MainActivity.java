package com.example.zzt1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView userName;
    ImageView menuButton;
    FirebaseAuth auth;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    RecyclerView recyclerView;
    ProductAdapter productAdapter;
    List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_main);

        // Initialize UI elements and views
        menuButton = findViewById(R.id.menu_button);
        userName = findViewById(R.id.userName);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        recyclerView = findViewById(R.id.recyclerView);

        // Initialize Firebase authentication instance
        auth = FirebaseAuth.getInstance();

        // Get the currently signed-in user and display their email
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            userName.setText(email);
        }

        // Set menu button functionality to open the drawer
        menuButton.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        // Set navigation item selection listener
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                Toast.makeText(MainActivity.this, "Home selected", Toast.LENGTH_SHORT).show();
            }

            if (item.getItemId() == R.id.nav_booking) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
            }
            if (item.getItemId() == R.id.nav_faq) {
                Intent intent = new Intent(MainActivity.this, FAQ.class);
                startActivity(intent);
            }
            if (item.getItemId() == R.id.nav_cart) {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
            }

            if (item.getItemId() == R.id.nav_logout) {
                auth.signOut(); // Sign out the user
                Intent intent = new Intent(MainActivity.this, Signup.class); // Navigate to Signup
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Initialize RecyclerView
        productList = new ArrayList<>();
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 items in a row
        productAdapter = new ProductAdapter(this, productList); // Pass the productList
        recyclerView.setAdapter(productAdapter); // Set the correct adapter

        // Fetch products from Firestore
        fetchProducts();
    }

    private void fetchProducts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products") // Correct collection name as per Firestore structure
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot result = task.getResult();
                        if (result != null && !result.isEmpty()) {
                            for (QueryDocumentSnapshot document : result) {
                                Product product = document.toObject(Product.class);
                                productList.add(product); // Adds each product to the list
                            }
                            productAdapter.notifyDataSetChanged(); // Refresh RecyclerView with new data
                        } else {
                            Toast.makeText(this, "No products found in the database.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(this, "Error getting products: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        // Close the app when back is pressed
        super.onBackPressed();
        finishAffinity(); // This will close the app
    }
}
