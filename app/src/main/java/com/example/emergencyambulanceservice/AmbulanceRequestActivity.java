package com.example.emergencyambulanceservice;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AmbulanceRequestActivity extends AppCompatActivity {

    EditText etPatientName, etPatientPhone;
    EditText etPickupLocation, etEmergencyDetails;
    Button btnSubmitRequest;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambulance_request);

        etPatientName = findViewById(R.id.etPatientName);
        etPatientPhone = findViewById(R.id.etPatientPhone);
        etPickupLocation = findViewById(R.id.etPickupLocation);
        etEmergencyDetails = findViewById(R.id.etEmergencyDetails);
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnSubmitRequest.setOnClickListener(v -> submitRequest());
    }

    private void submitRequest() {

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this,
                    "Please log in first",
                    Toast.LENGTH_LONG).show();
            return;
        }

        String name = etPatientName.getText().toString().trim();
        String phone = etPatientPhone.getText().toString().trim();
        String location = etPickupLocation.getText().toString().trim();
        String details = etEmergencyDetails.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty()
                || location.isEmpty() || details.isEmpty()) {
            Toast.makeText(this,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        Map<String, Object> request = new HashMap<>();
        request.put("patientUid", uid);
        request.put("patientName", name);
        request.put("patientPhone", phone);
        request.put("pickupLocation", location);
        request.put("emergencyDetails", details);
        request.put("status", "pending");
        request.put("createdAt", FieldValue.serverTimestamp());

        btnSubmitRequest.setEnabled(false);

        db.collection("ambulanceRequests")
                .add(request)
                .addOnSuccessListener(documentReference -> {
                    btnSubmitRequest.setEnabled(true);

                    Toast.makeText(this,
                            "Request submitted successfully",
                            Toast.LENGTH_LONG).show();

                    finish();
                })
                .addOnFailureListener(e -> {
                    btnSubmitRequest.setEnabled(true);

                    Toast.makeText(this,
                            "Request failed: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}
