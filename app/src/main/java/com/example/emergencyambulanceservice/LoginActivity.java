package com.example.emergencyambulanceservice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin, btnSignUp;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this,
                        "Please enter email and password",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            btnLogin.setEnabled(false);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        btnLogin.setEnabled(true);

                        if (task.isSuccessful()) {

                            String adminUID =
                                    "BYUgbgykqrel8fmnnHPXHNSIrIE3";

                            String loggedInUID =
                                    mAuth.getCurrentUser().getUid();

                            Intent intent;

                            if (loggedInUID.equals(adminUID)) {
                                intent = new Intent(
                                        LoginActivity.this,
                                        AdminActivity.class);
                            } else {
                                intent = new Intent(
                                        LoginActivity.this,
                                        HomeActivity.class);
                            }

                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(this,
                                    "Login failed: "
                                            + task.getException()
                                            .getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });

        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(
                    LoginActivity.this,
                    RegistrationActivity.class);
            startActivity(intent);
        });
    }
}
