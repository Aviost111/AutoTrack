package com.example.autotrack;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

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
        setupClickListener(R.id.btnRegisterTool, RegisterToolActivity.class);
        setupClickListener(R.id.btnRegisterEmployee, RegisterEmployeeActivity.class);
        setupClickListener(R.id.btnLogout, LoginActivity.class);
        setupClickListener(R.id.btnToolsList, EmployeeActivity.class);
        // setupClickListener(R.id.btnEmployeesList, .class) ; //TODO change when merge with avia code

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize TextView
        tvProfileInfo = findViewById(R.id.tvProfileInfo);

        // Retrieve the currently signed-in user
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // Get the user's UID
            String uid = user.getUid();

            // Retrieve first and last name from Firebase
            db.collection("Manger")
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
                            }
                        } else {
                            // Task failed with an exception
                        }
                    });
        }
    }


    // Helper method to set up click listeners for buttons
    private void setupClickListener(int buttonId, Class<?> destinationClass) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerActivity.this, destinationClass);
            startActivity(intent);
        });
    }

}