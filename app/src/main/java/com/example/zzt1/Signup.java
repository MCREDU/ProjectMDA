package com.example.zzt1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.zzt1.databinding.ActivitySignupBinding;

public class Signup extends AppCompatActivity {

    ActivitySignupBinding binding;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        binding.signupButton.setOnClickListener(view -> {
            String email = binding.signupEmail.getText().toString();
            String password = binding.signupPassword.getText().toString();
            String confirmPassword = binding.signupConfirm.getText().toString();

            if(email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty())
                Toast.makeText(Signup.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
            else{
                if(password.equals(confirmPassword)){
                    Boolean checkUserEmail = databaseHelper.checkEmail(email);

                    if(!checkUserEmail){
                        Boolean insert = databaseHelper.insertData(email, password);

                        if(insert){
                            Toast.makeText(Signup.this, "Signup Successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), login.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(Signup.this, "Signup Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(Signup.this, "User already exists! Please login", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(Signup.this, "Invalid Password!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.loginRedirectText.setOnClickListener(view -> {
            Intent intent = new Intent(Signup.this, login.class);
            startActivity(intent);
        });

    }
}