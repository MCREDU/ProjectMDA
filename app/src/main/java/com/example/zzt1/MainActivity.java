package com.example.zzt1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    TextView userName;
    ImageView menuButton;
    Button logoutButton;
    FirebaseAuth auth;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_main);

        // Initialize UI elements
        menuButton = findViewById(R.id.menu_button);
        userName = findViewById(R.id.userName);
        logoutButton = findViewById(R.id.logout);
        drawerLayout = findViewById(R.id.drawer_layout); // Make sure you have this ID in your layout
        navigationView = findViewById(R.id.nav_view); // Make sure you have this ID in your layout

        // Initialize Firebase authentication
        auth = FirebaseAuth.getInstance();

        // Get the currently signed-in user
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            userName.setText(email); // Display the logged-in user's email
        }

        // Set menu button functionality to open the drawer
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START); // Open drawer from the start (left side)
            }
        });

        // Set navigation item selection listener
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation item selection
                if (item.getItemId() == R.id.nav_home) {
                    Toast.makeText(MainActivity.this, "Home selected", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.nav_booking) {
                    // Navigate to the GalleryActivity
                    Intent intent = new Intent(MainActivity.this, SecondActivity.class); // Change to your gallery activity class
                    startActivity(intent);
                }

                // Handle logout item selection
                if (item.getItemId() == R.id.nav_logout) {
                    auth.signOut(); // Sign out from Firebase
                    Intent intent = new Intent(MainActivity.this, login.class); // Change to your login activity class
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // Close the MainActivity
                }

                // Close the drawer after selection
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // Set logout button functionality
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut(); // Sign out from Firebase
                Intent intent = new Intent(MainActivity.this, login.class); // Change to your login activity class
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Close the MainActivity
            }
        });
    }
}
