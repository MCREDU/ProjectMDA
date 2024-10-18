package com.example.zzt1;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class FAQ extends AppCompatActivity {

    private Button btnDeliveriesFAQ, btnAddictionHelpFAQ, btnReturnFAQ,
            btnOrderOutOfStockFAQ, btnPaymentMethodFAQ,
            btnLegalAgeFAQ, btnCancelFAQ;
    private View menuButton;
    Button logoutButton;
    private TextView tvDeliveryAnswer, tvAddictionHelp, tvReturn,
            tvOrderOutOfStock, tvPaymentMethod,
            tvLegalAge, tvCancel;

    private FirebaseAuth auth; // Firebase Auth instance
    private DrawerLayout drawerLayout; // DrawerLayout instance




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_faq_activity);

        // Initialize Firebase Auth instance
        auth = FirebaseAuth.getInstance(); // Add this line


    // Set edge-to-edge support
        EdgeToEdge.enable(this);


        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        menuButton = findViewById(R.id.menu_button);
        NavigationView navigationView = findViewById(R.id.nav_view);



        // Check if user is logged in
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            finish(); // Redirect to Login activity if not logged in
            return;
        }

        // Menu button to open navigation drawer
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Initialize buttons and text views
        btnDeliveriesFAQ = findViewById(R.id.btnDeliveriesFAQ);
        btnAddictionHelpFAQ = findViewById(R.id.btnAddictionHelpFAQ);
        btnReturnFAQ = findViewById(R.id.btnReturnFAQ);
        btnOrderOutOfStockFAQ = findViewById(R.id.btnOrderOutOfStockFAQ);
        btnPaymentMethodFAQ = findViewById(R.id.btnPaymentMethodFAQ);
        btnLegalAgeFAQ = findViewById(R.id.btnLegalAgeFAQ);
        btnCancelFAQ = findViewById(R.id.btnCancelFAQ);

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


        // Set navigation item selection listener
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    Toast.makeText(FAQ.this, "Home selected", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FAQ.this, MainActivity.class); // Navigate to MainActivity
                    startActivity(intent);
                    finish(); // Close the current activity to avoid stacking
                }

                if (item.getItemId() == R.id.nav_booking) {
                    Intent intent = new Intent(FAQ.this, SecondActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.nav_faq) {
                    Intent intent = new Intent(FAQ.this, FAQ.class);
                    startActivity(intent);
                }

                if (item.getItemId() == R.id.nav_logout) {
                    auth.signOut();
                    Intent intent = new Intent(FAQ.this, login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                // Close the drawer after selection
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
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
