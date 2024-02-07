package com.example.autotrack;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class CompanyActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView tvProfileInfo;
    private String company_uid;

    @SuppressLint("UseSupportActionBar")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);

        // Set up click listeners
        setupClickListeners();

        // Initialize Firebase Authentication and Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI element for displaying welcome message
        tvProfileInfo = findViewById(R.id.tvProfileInfo);

        // Get the company ID from the intent extra
        company_uid = getIntent().getStringExtra("company_uid");

        if (company_uid != null) {
            retrieveCompanyInfo(company_uid);
        } else {
            // Pop an error message using toast and go back to the login screen
            Toast.makeText(this, "Error sign in company ", Toast.LENGTH_SHORT).show();
            navigateToActivity(LoginActivity.class);
        }
    }

    private void setupClickListeners() {
        setupClickListener(R.id.btnRegisterTool, RegisterVehicleActivity.class);
        setupClickListener(R.id.btnRegisterEmployee, RegisterEmployeeActivity.class);
        setupClickListener(R.id.btnLogout, LoginActivity.class);
        setupClickListener(R.id.btnToolsList, VehicleListActivity.class);
        setupClickListener(R.id.btnEmployeesList, EmployeesListActivity.class);

        // Add click listener for the "Delete Employee" button
        Button btnDeleteEmployee = findViewById(R.id.btnDeleteEmployee);
        btnDeleteEmployee.setOnClickListener(v -> showDeleteDialog("Employees"));

        // Add click listener for the "Delete Vehicle" button
        Button btnDeleteVehicle = findViewById(R.id.btnDeleteVehicle);
        btnDeleteVehicle.setOnClickListener(v -> showDeleteDialog("Vehicles"));

    }

    private void retrieveCompanyInfo(String uid) {
        db.collection("Companies")
                .document(uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Document found, retrieve first and last name
                            String firstName = document.getString("first_name");
                            String lastName = document.getString("last_name");
                            displayManagerInfo(firstName, lastName);
                        } else {
                            // Document does not exist
                            // Display an error message
                            Toast.makeText(this, "Manager not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Task failed with an exception
                        handleTaskFailure(task.getException());
                    }
                });
    }

    private void displayManagerInfo(String firstName, String lastName) {
        String welcomeMessage = "Welcome, " + firstName + " " + lastName;
        tvProfileInfo.setText(welcomeMessage);
    }

    private void handleTaskFailure(Exception exception) {
        // Display an error message
        Toast.makeText(this, "Task failed with exception: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
    }

    // Helper method to set up click listeners for buttons
    private void setupClickListener(int buttonId, Class<?> destinationClass) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> navigateToActivity(destinationClass));
    }

    // Helper method to navigate to another activity and finish the current activity
    private void navigateToActivity(Class<?> destinationClass) {
        Intent intent = new Intent(CompanyActivity.this, destinationClass);
        intent.putExtra("company_uid", company_uid);

        //get the email of the current company using company_uid and pass it to the next activity
        DocumentReference companyRef = db.document("Companies/" + company_uid);

        companyRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    String email = documentSnapshot.getString("email");
                    intent.putExtra("email", email);
                    startActivity(intent);
                })
                .addOnFailureListener(this::handleTaskFailure);
    }

    // Method to show the delete dialog
    private void showDeleteDialog(String type) {
        DeleteDialogHelper.showDialog(this, db, company_uid, type);
    }
}