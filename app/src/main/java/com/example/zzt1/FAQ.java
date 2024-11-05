package com.example.zzt1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class FAQ extends AppCompatActivity {


    private TextView tvDeliveryAnswer, tvAddictionHelp, tvReturn,
            tvOrderOutOfStock, tvPaymentMethod,
            tvLegalAge, tvCancel;

    private FirebaseAuth auth; // Firebase Auth instance for user authentication
    private DrawerLayout drawerLayout; // DrawerLayout instance for navigation drawer




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_faq_activity);

        // Initialize Firebase Auth instance for user authentication
        auth = FirebaseAuth.getInstance();


    // Set edge-to-edge support
        EdgeToEdge.enable(this);


        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        View menuButton = findViewById(R.id.menu_button);
        NavigationView navigationView = findViewById(R.id.nav_view);



        // Check if user is logged in and redirect if not
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            finish(); // Redirect to Login activity if not logged in
            return;
        }

        // Menu button to open navigation drawer when clicked on
        menuButton.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        // Initialize buttons and text views for FAQ
        Button btnDeliveriesFAQ = findViewById(R.id.btnDeliveriesFAQ);
        Button btnAddictionHelpFAQ = findViewById(R.id.btnAddictionHelpFAQ);
        Button btnReturnFAQ = findViewById(R.id.btnReturnFAQ);
        Button btnOrderOutOfStockFAQ = findViewById(R.id.btnOrderOutOfStockFAQ);
        Button btnPaymentMethodFAQ = findViewById(R.id.btnPaymentMethodFAQ);
        Button btnLegalAgeFAQ = findViewById(R.id.btnLegalAgeFAQ);
        Button btnCancelFAQ = findViewById(R.id.btnCancelFAQ);

        tvDeliveryAnswer = findViewById(R.id.tvDeliveryAnswer);
        tvAddictionHelp = findViewById(R.id.tvAddictionHelp);
        tvReturn = findViewById(R.id.tvReturn);
        tvOrderOutOfStock = findViewById(R.id.tvOrderOutOfStock);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvLegalAge = findViewById(R.id.tvLegalAge);
        tvCancel = findViewById(R.id.tvCancel);

        // Set button click listeners
        btnDeliveriesFAQ.setOnClickListener(v -> toggleVisibility(tvDeliveryAnswer));
        btnAddictionHelpFAQ.setOnClickListener(v -> toggleVisibility(tvAddictionHelp));
        btnReturnFAQ.setOnClickListener(v -> toggleVisibility(tvReturn));
        btnOrderOutOfStockFAQ.setOnClickListener(v -> toggleVisibility(tvOrderOutOfStock));
        btnPaymentMethodFAQ.setOnClickListener(v -> toggleVisibility(tvPaymentMethod));
        btnLegalAgeFAQ.setOnClickListener(v -> toggleVisibility(tvLegalAge));
        btnCancelFAQ.setOnClickListener(v -> toggleVisibility(tvCancel));


        // Set navigation item selection listener for navigation drawer
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                Toast.makeText(FAQ.this, "Home selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FAQ.this, MainActivity.class); // navigates to main page
                startActivity(intent);
                finish(); // Close the current activity to avoid stacking
            }

            if (item.getItemId() == R.id.nav_booking) {
                Intent intent = new Intent(FAQ.this, SecondActivity.class);
                startActivity(intent); // navigates to booking page if booking is selected
            }
            if (item.getItemId() == R.id.nav_faq) {
                Intent intent = new Intent(FAQ.this, FAQ.class);
                startActivity(intent); // navigates to faq page if faq is selected
            }

            if (item.getItemId() == R.id.nav_cart) {
                Intent intent = new Intent(FAQ.this, CartActivity.class);
                startActivity(intent); // navigates to cart page if cart is selected
            }

            if (item.getItemId() == R.id.nav_logout) {
                auth.signOut();
                Intent intent = new Intent(FAQ.this, login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // logs user out when selected , redirects to signup page
            }
            // Close the drawer after selection
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

    }

    // Method to toggle visibility of the answer TextView
    private void toggleVisibility(TextView textView) {
        if (textView.getVisibility() == View.VISIBLE) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
        }
    }
}
