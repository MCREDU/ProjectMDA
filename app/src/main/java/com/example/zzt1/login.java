package com.example.zzt1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.zzt1.databinding.ActivityLoginBinding;

public class login extends AppCompatActivity {

    ActivityLoginBinding binding;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        binding.loginButton.setOnClickListener(view -> {
            String email = binding.loginEmail.getText().toString();
            String password = binding.loginPassword.getText().toString();

            if(email.isEmpty() || password.isEmpty())
                Toast.makeText(login.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
            else{
                Boolean checkCredentials = databaseHelper.checkEmailPassword(email, password);

                if(checkCredentials){
                    Toast.makeText(login.this, "Login Successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent  = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(login.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.signupRedirectText.setOnClickListener(view -> {
            Intent intent = new Intent(login.this, Signup.class);
            startActivity(intent);
        });
    }
}