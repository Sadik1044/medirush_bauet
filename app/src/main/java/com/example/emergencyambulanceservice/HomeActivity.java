package com.example.emergencyambulanceservice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    Button btnRequestAmbulance;
    Button btnAmbulanceInfo;
    Button btnRequestHistory;
    Button btnDriverRegistration;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnRequestAmbulance = findViewById(R.id.btnRequestAmbulance);
        btnAmbulanceInfo = findViewById(R.id.btnAmbulanceInfo);
        btnRequestHistory = findViewById(R.id.btnRequestHistory);
        btnDriverRegistration =
                findViewById(R.id.btnDriverRegistration);
        btnLogout = findViewById(R.id.btnLogout);


        btnRequestAmbulance.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    AmbulanceRequestActivity.class);
            startActivity(intent);
        });


        btnAmbulanceInfo.setOnClickListener(v ->
                Toast.makeText(this,
                        "Ambulance information coming next",
                        Toast.LENGTH_SHORT).show());

        btnRequestHistory.setOnClickListener(v ->
                Toast.makeText(this,
                        "Request history coming next",
                        Toast.LENGTH_SHORT).show());
        btnDriverRegistration.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    DriverRegistrationActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(
                    HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}