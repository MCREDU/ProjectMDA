package com.example.zzt1;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;

public class SecondActivity extends AppCompatActivity {

    // Uncomment and initialize auth if you are using Firebase for authentication
    // FirebaseAuth auth;

    // Declare DrawerLayout and menuButton
    private DrawerLayout drawerLayout;
    private View menuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set edge-to-edge support
        EdgeToEdge.enable(this);
        setContentView(R.layout.nav_second_activity);

        // Initialize DrawerLayout and NavigationView (ensure you have these IDs in your layout)
        drawerLayout = findViewById(R.id.drawer_layout); // Ensure this ID matches your layout
        menuButton = findViewById(R.id.menu_button); // Ensure this ID matches your layout
        NavigationView navigationView = findViewById(R.id.nav_view); // Ensure this ID is correct

        // Set menu button click listener to open the drawer
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
                    Toast.makeText(SecondActivity.this, "Home selected", Toast.LENGTH_SHORT).show();
                    // Optionally, navigate to MainActivity
                    Intent intent = new Intent(SecondActivity.this, MainActivity.class);
                    startActivity(intent);
                }

                if (item.getItemId() == R.id.nav_booking) {
                    // Navigate to the GalleryActivity
                    Intent intent = new Intent(SecondActivity.this, SecondActivity.class); // Change to your gallery activity class
                    startActivity(intent);
                }

                // Handle logout item selection
                if (item.getItemId() == R.id.nav_logout) {
                    // Uncomment if using Firebase
                    // auth.signOut(); // Sign out from Firebase
                    Intent intent = new Intent(SecondActivity.this, login.class); // Change to your login activity class
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // Close the SecondActivity
                }

                // Close the drawer after selection
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }
}
