package com.example.zzt1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Objects;
import java.util.regex.Pattern;
import android.text.InputType;

public class Signup extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText signupEmail, signupPassword;
    private CheckBox showPasswordCheckbox;

    // Define password pattern: at least one uppercase, one lowercase, one digit, one special character, and minimum 8 characters
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$"); // Updated pattern to exclude HTML characters

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        showPasswordCheckbox = findViewById(R.id.show_password);
        Button signupButton = findViewById(R.id.signup_button);
        TextView loginRedirectText = findViewById(R.id.loginRedirectText);

        // Toggle password visibility
        showPasswordCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                signupPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                signupPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            signupPassword.setSelection(signupPassword.getText().length());
        });

        signupButton.setOnClickListener(view -> {
            // Retrieve and sanitize input immediately
            String user = sanitizeInput(signupEmail.getText().toString().trim());
            String pass = sanitizeInput(signupPassword.getText().toString().trim());

            // Email validation
            if (user.isEmpty()) {
                signupEmail.setError("Email cannot be empty");
                return;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(user).matches()) {
                signupEmail.setError("Please enter a valid email address");
                return;
            }

            // Password validation
            if (pass.isEmpty()) {
                signupPassword.setError("Password cannot be empty");
                return;
            } else if (containsHtmlTags(pass)) {
                signupPassword.setError("Password cannot contain HTML tags or special characters.");
                return;
            } else if (!PASSWORD_PATTERN.matcher(pass).matches()) {
                signupPassword.setError("Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character.");
                return;
            }

            // Create user with email and password in Firebase
            auth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Objects.requireNonNull(auth.getCurrentUser()).sendEmailVerification()
                            .addOnCompleteListener(verificationTask -> {
                                if (verificationTask.isSuccessful()) {
                                    Toast.makeText(Signup.this, "SignUp Successful. Please verify your email.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Signup.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                }
                            });
                    startActivity(new Intent(Signup.this, login.class));
                } else {
                    Toast.makeText(Signup.this, "SignUp Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        loginRedirectText.setOnClickListener(view -> startActivity(new Intent(Signup.this, login.class)));
    }

    // Method to sanitize input, removing HTML tags or encoding special characters
    private String sanitizeInput(String input) {
        return input.replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&#39;")
                .replaceAll("&", "&amp;");
    }

    // Method to check for HTML tags or characters in a string
    private boolean containsHtmlTags(String input) {
        // Regex to check for any HTML tags or suspicious characters
        return input.toLowerCase().matches(".*<\\s*script.*>.*|.*[<>&\"'].*"); // Matches <, >, &, ", and '
    }
}
