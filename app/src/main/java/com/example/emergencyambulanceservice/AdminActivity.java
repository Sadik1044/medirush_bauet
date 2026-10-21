package com.example.emergencyambulanceservice;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminActivity extends AppCompatActivity {

    LinearLayout applicationsContainer;
    LinearLayout ambulanceRequestsContainer;
    ProgressBar progressApplications;
    Button btnRefreshApplications;

    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        applicationsContainer =
                findViewById(R.id.driverApplicationsContainer);

        ambulanceRequestsContainer =
                findViewById(R.id.ambulanceRequestsContainer);

        progressApplications =
                findViewById(R.id.progressApplications);

        btnRefreshApplications =
                findViewById(R.id.btnRefreshApplications);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        btnRefreshApplications.setOnClickListener(v -> refreshDashboard());

        refreshDashboard();
    }

    private void refreshDashboard() {
        loadApplications();
        loadAmbulanceRequests();
    }

    // DRIVER APPLICATIONS

    private void loadApplications() {

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this,
                    "Please sign in first",
                    Toast.LENGTH_LONG).show();
            return;
        }

        progressApplications.setVisibility(View.VISIBLE);
        applicationsContainer.removeAllViews();

        db.collection("drivers")
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(snapshot -> {
                    progressApplications.setVisibility(View.GONE);

                    if (snapshot.isEmpty()) {
                        addMessage("No pending applications found.");
                        return;
                    }

                    snapshot.getDocuments().forEach(document -> {

                        String name = document.getString("name");
                        String phone = document.getString("phone");
                        String area = document.getString("serviceArea");
                        String license =
                                document.getString("licenseNumber");

                        LinearLayout card = new LinearLayout(this);
                        card.setOrientation(LinearLayout.VERTICAL);
                        card.setPadding(24, 24, 24, 24);

                        TextView details = new TextView(this);
                        details.setText(
                                "Name: " + name
                                        + "\nPhone: " + phone
                                        + "\nService area: " + area
                                        + "\nLicence: " + license
                                        + "\nStatus: Pending");

                        details.setTextSize(16);
                        details.setTextColor(0xFF123C46);
                        card.addView(details);

                        LinearLayout buttons = new LinearLayout(this);
                        buttons.setOrientation(LinearLayout.HORIZONTAL);

                        Button approveButton = new Button(this);
                        approveButton.setText("Approve");

                        Button rejectButton = new Button(this);
                        rejectButton.setText("Reject");

                        LinearLayout.LayoutParams buttonParams =
                                new LinearLayout.LayoutParams(
                                        0,
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        1);

                        buttonParams.setMargins(4, 12, 4, 4);

                        buttons.addView(approveButton, buttonParams);
                        buttons.addView(rejectButton, buttonParams);
                        card.addView(buttons);
                        Spinner driverSpinner = new Spinner(this);

                        TextView assignmentLabel = new TextView(this);
                        assignmentLabel.setText("Select an approved driver:");

                        card.addView(assignmentLabel);
                        card.addView(driverSpinner);

                        Button assignButton = new Button(this);
                        assignButton.setText("Assign Driver");
                        card.addView(assignButton);

                        loadApprovedDrivers(driverSpinner, assignButton, document.getId());

                        applicationsContainer.addView(card);
                        addDivider(applicationsContainer);

                        approveButton.setOnClickListener(v ->
                                updateDriverStatus(
                                        document.getId(), "approved"));

                        rejectButton.setOnClickListener(v ->
                                updateDriverStatus(
                                        document.getId(), "rejected"));
                    });
                })
                .addOnFailureListener(e -> {
                    progressApplications.setVisibility(View.GONE);
                    addMessage("Could not load applications.");

                    Toast.makeText(this,
                            "Loading failed: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void updateDriverStatus(
            String documentId, String status) {

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this,
                    "Please sign in again",
                    Toast.LENGTH_LONG).show();
            return;
        }

        db.collection("drivers")
                .document(documentId)
                .update("status", status)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this,
                            "Application " + status,
                            Toast.LENGTH_SHORT).show();

                    loadApplications();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Update failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
    }

    // AMBULANCE REQUESTS

    private void loadAmbulanceRequests() {

        ambulanceRequestsContainer.removeAllViews();

        if (auth.getCurrentUser() == null) {
            addRequestMessage("Please sign in first.");
            return;
        }

        db.collection("ambulanceRequests")
                .whereIn("status",
                        java.util.Arrays.asList("pending", "accepted"))
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (snapshot.isEmpty()) {
                        addRequestMessage(
                                "No pending ambulance requests.");
                        return;
                    }

                    snapshot.getDocuments().forEach(document -> {

                        String name =
                                document.getString("patientName");
                        String phone =
                                document.getString("patientPhone");
                        String location =
                                document.getString("pickupLocation");
                        String details =
                                document.getString("emergencyDetails");

                        LinearLayout card = new LinearLayout(this);
                        card.setOrientation(LinearLayout.VERTICAL);
                        card.setPadding(24, 24, 24, 24);

                        TextView info = new TextView(this);
                        info.setText(
                                "Patient: " + name
                                        + "\nPhone: " + phone
                                        + "\nPickup location: " + location
                                        + "\nEmergency details: " + details
                                        + "\nStatus: Pending");

                        info.setTextSize(16);
                        info.setTextColor(0xFF123C46);
                        card.addView(info);

                        LinearLayout buttons = new LinearLayout(this);
                        buttons.setOrientation(LinearLayout.HORIZONTAL);

                        Button acceptButton = new Button(this);
                        acceptButton.setText("Accept");

                        Button rejectButton = new Button(this);
                        rejectButton.setText("Reject");

                        LinearLayout.LayoutParams params =
                                new LinearLayout.LayoutParams(
                                        0,
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        1);

                        params.setMargins(4, 12, 4, 4);

                        buttons.addView(acceptButton, params);
                        buttons.addView(rejectButton, params);
                        card.addView(buttons);

                        ambulanceRequestsContainer.addView(card);
                        addDivider(ambulanceRequestsContainer);

                        acceptButton.setOnClickListener(v ->
                                updateRequestStatus(document.getId(), "accepted"));

                        rejectButton.setOnClickListener(v ->
                                updateRequestStatus(
                                        document.getId(), "rejected"));
                    });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Could not load requests: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG).show());
    }

    private void updateRequestStatus(
            String documentId, String status) {

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this,
                    "Please sign in again",
                    Toast.LENGTH_LONG).show();
            return;
        }

        db.collection("ambulanceRequests")
                .document(documentId)
                .update("status", status)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this,
                            "Ambulance request " + status,
                            Toast.LENGTH_SHORT).show();

                    loadAmbulanceRequests();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Update failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
    }

    // HELPER METHODS

    private void addMessage(String message) {
        TextView text = new TextView(this);
        text.setText(message);
        text.setTextSize(16);
        text.setPadding(12, 20, 12, 20);
        applicationsContainer.addView(text);
    }

    private void addRequestMessage(String message) {
        TextView text = new TextView(this);
        text.setText(message);
        text.setTextSize(16);
        text.setPadding(12, 20, 12, 20);
        ambulanceRequestsContainer.addView(text);
    }

    private void addDivider(LinearLayout container) {
        View divider = new View(this);
        divider.setBackgroundColor(0xFFCFD8DC);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 2);

        container.addView(divider, params);
    }
    private void loadApprovedDrivers(
            Spinner driverSpinner,
            Button assignButton,
            String requestId) {

        java.util.ArrayList<String> driverLabels =
                new java.util.ArrayList<>();

        java.util.ArrayList<String> driverIds =
                new java.util.ArrayList<>();

        driverLabels.add("Choose a driver");
        driverIds.add("");

        db.collection("drivers")
                .whereEqualTo("status", "approved")
                .get()
                .addOnSuccessListener(snapshot -> {

                    for (com.google.firebase.firestore.DocumentSnapshot driver
                            : snapshot.getDocuments()) {

                        String name = driver.getString("name");
                        String phone = driver.getString("phone");

                        driverLabels.add(
                                (name == null ? "Unnamed driver" : name)
                                        + " - "
                                        + (phone == null ? "No phone" : phone));

                        driverIds.add(driver.getId());
                    }

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<>(
                                    this,
                                    android.R.layout.simple_spinner_item,
                                    driverLabels);

                    adapter.setDropDownViewResource(
                            android.R.layout.simple_spinner_dropdown_item);

                    driverSpinner.setAdapter(adapter);

                    assignButton.setEnabled(driverIds.size() > 1);

                    assignButton.setOnClickListener(v -> {

                        int selectedPosition =
                                driverSpinner.getSelectedItemPosition();

                        if (selectedPosition <= 0) {
                            Toast.makeText(this,
                                    "Please select a driver",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String selectedDriverId =
                                driverIds.get(selectedPosition);

                        assignDriver(requestId, selectedDriverId);
                    });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Could not load drivers: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
    }
    private void assignDriver(
            String requestId, String driverId) {

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this,
                    "Please sign in again",
                    Toast.LENGTH_LONG).show();
            return;
        }

        db.collection("drivers")
                .document(driverId)
                .get()
                .addOnSuccessListener(driver -> {

                    if (!driver.exists()
                            || !"approved".equals(driver.getString("status"))) {
                        Toast.makeText(this,
                                "This driver is not approved",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    String name = driver.getString("name");
                    String phone = driver.getString("phone");

                    java.util.Map<String, Object> updates =
                            new java.util.HashMap<>();

                    updates.put("status", "assigned");
                    updates.put("assignedDriverUid", driverId);
                    updates.put("assignedDriverName", name);
                    updates.put("assignedDriverPhone", phone);
                    android.util.Log.e(
                            "MEDIRUSH_ASSIGN",
                            "Current admin UID: " + auth.getCurrentUser().getUid()
                    );

                    db.collection("ambulanceRequests")
                            .document(requestId)
                            .update(updates)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this,
                                        "Driver assigned successfully",
                                        Toast.LENGTH_LONG).show();

                                loadAmbulanceRequests();
                            })
                            .addOnFailureListener(e -> {
                                android.util.Log.e(
                                        "MEDIRUSH_ASSIGN",
                                        "Request update failed",
                                        e
                                );

                                Toast.makeText(
                                        AdminActivity.this,
                                        "Assignment failed: " + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Could not verify driver: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG).show());
    }
}
