package com.example.emergencyambulanceservice;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DriverRegistrationActivity extends AppCompatActivity {

    EditText etDriverName, etDriverPhone;
    EditText etDriverLicense, etDriverArea;
    Button btnSubmitDriver;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_registration);

        etDriverName = findViewById(R.id.etDriverName);
        etDriverPhone = findViewById(R.id.etDriverPhone);
        etDriverLicense = findViewById(R.id.etDriverLicense);
        etDriverArea = findViewById(R.id.etDriverArea);
        btnSubmitDriver = findViewById(R.id.btnSubmitDriver);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnSubmitDriver.setOnClickListener(v -> submitApplication());
    }

    private void submitApplication() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this,
                    "Please log in first",
                    Toast.LENGTH_LONG).show();
            return;
        }

        String name = etDriverName.getText().toString().trim();
        String phone = etDriverPhone.getText().toString().trim();
        String license = etDriverLicense.getText().toString().trim();
        String area = etDriverArea.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty()
                || license.isEmpty() || area.isEmpty()) {
            Toast.makeText(this,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();

        Map<String, Object> driver = new HashMap<>();
        driver.put("uid", uid);
        driver.put("name", name);
        driver.put("phone", phone);
        driver.put("licenseNumber", license);
        driver.put("serviceArea", area);
        driver.put("status", "pending");
        driver.put("available", false);

        btnSubmitDriver.setEnabled(false);

        db.collection("drivers")
                .document(uid)
                .set(driver)
                .addOnSuccessListener(unused -> {
                    btnSubmitDriver.setEnabled(true);
                    Toast.makeText(this,
                            "Application submitted for admin approval",
                            Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnSubmitDriver.setEnabled(true);
                    Toast.makeText(this,
                            "Submission failed: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}