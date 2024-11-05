package com.example.zzt1;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.content.Intent;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.developer.gbuttons.GoogleSignInButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
    private EditText loginEmail, loginPassword;
    private FirebaseAuth auth;
    TextView forgotPassword;
    GoogleSignInButton googleBtn;
    GoogleSignInOptions gOptions;
    GoogleSignInClient gClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        Button loginButton = findViewById(R.id.login_button);
        TextView signupRedirectText = findViewById(R.id.signUpRedirectText);
        CheckBox showPasswordCheckbox = findViewById(R.id.show_password);
        forgotPassword = findViewById(R.id.forgot_password);
        googleBtn = findViewById(R.id.googleBtn);
        auth = FirebaseAuth.getInstance();

        // Toggle password visibility based on checkbox state
        showPasswordCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                loginPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                loginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            loginPassword.setSelection(loginPassword.getText().length());
        });

        loginButton.setOnClickListener(v -> {
            // Retrieve and sanitize input immediately after button click
            String email = sanitizeInput(loginEmail.getText().toString().trim());
            String pass = sanitizeInput(loginPassword.getText().toString().trim());

            if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if (!pass.isEmpty()) {
                    // Check for HTML tags in password
                    if (containsHtmlTags(pass)) {
                        loginPassword.setError("Password cannot contain HTML tags.");
                        return;
                    }

                    auth.signInWithEmailAndPassword(email, pass)
                            .addOnSuccessListener(authResult -> {
                                Toast.makeText(login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(login.this, MainActivity.class));
                                finish();
                            }).addOnFailureListener(e -> Toast.makeText(login.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                } else {
                    loginPassword.setError("Empty fields are not allowed");
                }
            } else if (email.isEmpty()) {
                loginEmail.setError("Empty fields are not allowed");
            } else {
                loginEmail.setError("Please enter a correct email");
            }
        });

        signupRedirectText.setOnClickListener(v -> startActivity(new Intent(login.this, Signup.class)));

        forgotPassword.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(login.this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot, null);
            EditText emailBox = dialogView.findViewById(R.id.emailBox);
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();

            dialogView.findViewById(R.id.btnReset).setOnClickListener(view1 -> {
                String userEmail = sanitizeInput(emailBox.getText().toString().trim());

                // Check for empty email and HTML tags
                if (userEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    Toast.makeText(login.this, "Enter your registered email id", Toast.LENGTH_SHORT).show();
                    return;
                } else if (containsHtmlTags(userEmail)) {
                    Toast.makeText(login.this, "Email cannot contain HTML tags.", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.sendPasswordResetEmail(userEmail).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(login.this, "Check your email", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(login.this, "Unable to send, failed", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            dialogView.findViewById(R.id.btnCancel).setOnClickListener(view12 -> dialog.dismiss());

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            dialog.show();
        });

        // Google Sign-In setup
        gOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gClient = GoogleSignIn.getClient(this, gOptions);
        GoogleSignInAccount gAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (gAccount != null) {
            finish();
            Intent intent = new Intent(login.this, MainActivity.class);
            startActivity(intent);
        }
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            task.getResult(ApiException.class);
                            finish();
                            Intent intent = new Intent(login.this, MainActivity.class);
                            startActivity(intent);
                        } catch (ApiException e) {
                            Toast.makeText(login.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        googleBtn.setOnClickListener(view -> {
            Intent signInIntent = gClient.getSignInIntent();
            activityResultLauncher.launch(signInIntent);
        });
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
        return input.toLowerCase().matches(".*<\\s*script.*>.*|.*[<>&\"'].*");
    }
}
