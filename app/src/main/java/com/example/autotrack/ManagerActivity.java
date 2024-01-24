package com.example.autotrack;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ManagerActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView tvProfileInfo;


    @SuppressLint("UseSupportActionBar")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        // Set up click listeners
        setupClickListeners();

        // Initialize Firebase Authentication and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        tvProfileInfo = findViewById(R.id.tvProfileInfo);

        // Retrieve the currently signed-in user
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            retrieveManagerInfo(user.getUid());
        } else {
            // If the user is not signed in, navigate to the login screen
            navigateToLoginScreen("User not signed in");
        }
    }

    private void setupClickListeners() {
        setupClickListener(R.id.btnRegisterTool, RegisterToolActivity.class);
        setupClickListener(R.id.btnRegisterEmployee, RegisterEmployeeActivity.class);
        setupClickListener(R.id.btnLogout, LoginActivity.class);
        setupClickListener(R.id.btnToolsList, EmployeeActivity.class);
        // setupClickListener(R.id.btnEmployeesList, .class) ; //TODO change when merge with avia code
    }

    private void retrieveManagerInfo(String uid) {
        db.collection("Managers")
                .document(uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Document found, retrieve first and last name
                            String firstName = document.getString("first_name");
                            String lastName = document.getString("last_name");

                            // Display welcome message with the user's name
                            String welcomeMessage = "Welcome, " + firstName + " " + lastName;
                            tvProfileInfo.setText(welcomeMessage);
                        } else {
                            // Document does not exist
//                            navigateToLoginScreen("Error finding document");
                        }
                    } else if (task.getException() != null) {
                        // Task failed with an exception
//                        navigateToLoginScreen("Error: " + task.getException().getMessage());
                    }
                });
    }

    private void navigateToLoginScreen(String errorMessage) {
        // Pop an error message using toast and go back to the login screen
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ManagerActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Helper method to set up click listeners for buttons
    private void setupClickListener(int buttonId, Class<?> destinationClass) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> navigateToActivity(destinationClass));
    }

    // Helper method to navigate to another activity and finish the current activity
    private void navigateToActivity(Class<?> destinationClass) {
        Intent intent = new Intent(ManagerActivity.this, destinationClass);
        startActivity(intent);
        finish(); // Finish the current activity to prevent going back via backspace button
    }

}