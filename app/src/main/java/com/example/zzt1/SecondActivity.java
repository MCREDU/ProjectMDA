package com.example.zzt1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SecondActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private EditText etNameSurname, etPhoneNumber, etEmailAddress, etNumberOfGuests, etDateOfBooking, etRequest;
    FirebaseAuth auth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set edge-to-edge support
        EdgeToEdge.enable(this);

        // Set the content view to the drawer layout
        setContentView(R.layout.nav_second_activity);

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        View menuButton = findViewById(R.id.menu_button);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Set up Firebase
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Bookings");

        // Initialize form elements
        etNameSurname = findViewById(R.id.etNameSurname);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etEmailAddress = findViewById(R.id.etEmailAddress);
        etNumberOfGuests = findViewById(R.id.etNumberOfGuests);
        etDateOfBooking = findViewById(R.id.etDateOfBooking);
        etRequest = findViewById(R.id.etRequest);
        Button btnSubmitBooking = findViewById(R.id.btnSubmitBooking);

        // Check if user is logged in
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            finish(); // Redirect to Login activity if not logged in
            return;
        }

        // Menu button to open navigation drawer
        menuButton.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        // Navigation item selection
        navigationView.setNavigationItemSelectedListener(item -> {
            // Handle navigation item selection
            if (item.getItemId() == R.id.nav_home) {
                Toast.makeText(SecondActivity.this, "Home selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SecondActivity.this, MainActivity.class); // Navigate to MainActivity
                startActivity(intent);
                finish(); // Close the current activity to avoid stacking
            }

            if (item.getItemId() == R.id.nav_booking) {
                // Navigate to the GalleryActivity
                Intent intent = new Intent(SecondActivity.this, SecondActivity.class);
                startActivity(intent);
            }

            if (item.getItemId() == R.id.nav_faq) {
                // Navigate to the GalleryActivity
                Intent intent = new Intent(SecondActivity.this, FAQ.class);
                startActivity(intent);
            }

            if (item.getItemId() == R.id.nav_cart) {
                Intent intent = new Intent(SecondActivity.this, CartActivity.class);
                startActivity(intent);
            }

            if (item.getItemId() == R.id.nav_logout) {
                auth.signOut(); // Sign out from Firebase
                Intent intent = new Intent(SecondActivity.this, login.class); // Change to your login activity class
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Close the SecondActivity
            }

            // Close the drawer after selection
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Date picker for booking date
        etDateOfBooking.setOnClickListener(view -> showDatePicker());

        // Submit booking button
        btnSubmitBooking.setOnClickListener(view -> submitBooking());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> etDateOfBooking.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1),
                year, month, day);

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private void submitBooking() {
        String nameSurname = etNameSurname.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String emailAddress = etEmailAddress.getText().toString().trim();
        String numberOfGuests = etNumberOfGuests.getText().toString().trim();
        String dateOfBooking = etDateOfBooking.getText().toString().trim();
        String request = etRequest.getText().toString().trim();

        // Check if fields are empty
        if (nameSurname.isEmpty() || phoneNumber.isEmpty() || emailAddress.isEmpty() || numberOfGuests.isEmpty() || dateOfBooking.isEmpty() || request.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate Name and Surname (only alphabetic characters and spaces)
        if (!nameSurname.matches("^[a-zA-Z\\s]+$")) {
            Toast.makeText(this, "Name and Surname must contain only letters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate Email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate Phone Number (digits only, 10 digits for South African numbers)
        if (!phoneNumber.matches("^[0-9]{10}$")) {
            Toast.makeText(this, "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate Number of Guests (digits only)
        if (!numberOfGuests.matches("^[0-9]+$")) {
            Toast.makeText(this, "Please enter a valid number of guests", Toast.LENGTH_SHORT).show();
            return;
        }

        // Enhanced Request validation to reject HTML-like tags
        if (!request.matches("^[a-zA-Z0-9\\s.]+$") || request.matches(".*<\\s*script.*>.*|.*[<>&\"'].*")) {
            Toast.makeText(this, "Request must contain only letters, numbers, spaces, and full stops, without HTML tags", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proceed with Firebase submission
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            String bookingId = mDatabase.push().getKey();

            Map<String, String> bookingData = new HashMap<>();
            bookingData.put("NameSurname", nameSurname);
            bookingData.put("PhoneNumber", phoneNumber);
            bookingData.put("EmailAddress", emailAddress);
            bookingData.put("NumberOfGuests", numberOfGuests);
            bookingData.put("DateOfBooking", dateOfBooking);
            bookingData.put("Request", request);
            bookingData.put("UserId", userId);

            assert bookingId != null;
            mDatabase.child(bookingId).setValue(bookingData, (error, ref) -> {
                if (error != null) {
                    Toast.makeText(SecondActivity.this, "Booking failed to submit", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SecondActivity.this, "Booking submitted successfully", Toast.LENGTH_SHORT).show();
                    clearFields(); // Clear fields after submission
                }
            });
        }
    }

    private void clearFields() {
        etNameSurname.setText("");
        etPhoneNumber.setText("");
        etEmailAddress.setText("");
        etNumberOfGuests.setText("");
        etDateOfBooking.setText("");
        etRequest.setText("");
    }
}
