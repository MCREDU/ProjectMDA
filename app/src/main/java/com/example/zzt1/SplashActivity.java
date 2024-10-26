package com.example.zzt1;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is logged in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in, navigate to MainActivity
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        } else {
            // No user is signed in, navigate to Signup Activity
            startActivity(new Intent(SplashActivity.this, Signup.class));
        }
        finish(); // Close SplashActivity
    }
}
