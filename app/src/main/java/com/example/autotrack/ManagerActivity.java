package com.example.autotrack;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

        // Initialize UI element for displaying welcome message
        tvProfileInfo = findViewById(R.id.tvProfileInfo);

        // Retrieve the currently signed-in user
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            retrieveManagerInfo(user.getUid());
        } else {
            // If the user is not signed in, navigate to the login screen
            // Pop an error message using toast and go back to the login screen
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show();
            navigateToActivity(LoginActivity.class);
        }
    }

    private void setupClickListeners() {
        setupClickListener(R.id.btnRegisterTool, RegisterToolActivity.class);
        setupClickListener(R.id.btnRegisterEmployee, RegisterEmployeeActivity.class);
        setupClickListener(R.id.btnLogout, LoginActivity.class);
        setupClickListener(R.id.btnToolsList, EmployeeActivity.class);
        setupClickListener(R.id.btnEmployeesList, EmployeesListActivity.class);

        Button btnDeleteEmployee = findViewById(R.id.btnDeleteEmployee);
        btnDeleteEmployee.setOnClickListener(v -> showDeleteEmployeeDialog());
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
                            displayManagerInfo(firstName, lastName);
                        } else {
                            // Document does not exist
                            handleManagerNotFound();
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

    private void handleManagerNotFound() {
        // Display an error message
        Toast.makeText(this, "Manager not found", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(ManagerActivity.this, destinationClass);
        startActivity(intent);
        //TODO check if this is needed or if it causes problems
        finish(); // Finish the current activity to prevent going back via backspace button
    }

    // Method to show the delete employee dialog
    private void showDeleteEmployeeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Employee");
        builder.setMessage("Enter the ID of the employee to delete:");

        // Set up the input
        final EditText input = new EditText(this);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String employeeId = input.getText().toString();
                // Call a method to delete the employee with the provided ID
                deleteEmployee(employeeId);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Method to delete the employee from the database
    private void deleteEmployee(String employeeId) {
        // Implement the logic to delete the employee with the provided ID from the database
        // You can use Firestore delete operation or update operation to remove the employee data
        // Example: db.collection("Employees").document(employeeId).delete()
        // After deleting the employee, you can show a toast message to indicate success or failure
        Toast.makeText(this, "Employee deleted successfully", Toast.LENGTH_SHORT).show();
    }

}