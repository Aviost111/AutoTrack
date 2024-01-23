package com.example.autotrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterEmployeeActivity extends AppCompatActivity {

    private static final String TAG = "RegisterEmployeeActivity";

    private FirebaseFirestore firestore;

    // UI elements
    private EditText etCompanyId, etManagerId, etEmail, etFirstName, etLastName, etPhone;
    private Button btnRegisterEmployee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_employee);

        // Set up click listener for the "Back" button
        setupClickListener(R.id.btnBack, ManagerActivity.class);

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI elements
        initializeViews();

        // Set click listener for the "Register Employee" button
        btnRegisterEmployee.setOnClickListener(view -> registerEmployee());
    }

    // Helper method to initialize UI elements
    private void initializeViews() {
        etCompanyId = findViewById(R.id.etCompanyId);
        etManagerId = findViewById(R.id.etManagerId);
        etEmail = findViewById(R.id.etEmail);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etPhone = findViewById(R.id.etPhone);
        btnRegisterEmployee = findViewById(R.id.btnRegisterEmployee);
    }

    // Helper method to handle the registration of an employee
    private void registerEmployee() {
        // Get data from EditText fields
        String companyId = etCompanyId.getText().toString();
        String managerId = etManagerId.getText().toString();
        String email = etEmail.getText().toString();
        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        String phone = etPhone.getText().toString();

        // Validate that all fields are filled
        if (areFieldsEmpty(companyId, managerId, email, firstName, lastName, phone)) {
            // Show an error message using a Toast
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return; // Exit the method if any field is empty
        }

        // Create a Map to store the employee data
        Map<String, Object> employeeData = createEmployeeDataMap(firstName, lastName, email, phone, managerId, companyId);

        // Upload data to Firebase
        uploadDataToFirebase(email, employeeData);
    }

    // Helper method to check if any of the provided fields are empty
    private boolean areFieldsEmpty(String... fields) {
        for (String field : fields) {
            if (field.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    // Helper method to create a Map with employee data
    private Map<String, Object> createEmployeeDataMap(String firstName, String lastName, String email, String phone, String managerId, String companyId) {
        Map<String, Object> employeeData = new HashMap<>();
        employeeData.put("first_name", firstName);
        employeeData.put("last_name", lastName);
        employeeData.put("email", email);
        employeeData.put("phone", phone);
        employeeData.put("manager_ID", managerId);
        employeeData.put("company_ID", companyId);
        employeeData.put("password", phone); // Set the password to the employee's phone number
        return employeeData;
    }

    // Helper method to upload data to Firebase
    private void uploadDataToFirebase(String documentID, Map<String, Object> data) {
        firestore.collection("Employees")
                .document(documentID)
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    // Data successfully uploaded
                    // Show a success message
                    Toast.makeText(RegisterEmployeeActivity.this, "Employee registration was successful", Toast.LENGTH_LONG).show();

                    // Finish the activity to go back to the manager homepage
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Error uploading data
                    // Show an error message
                    Toast.makeText(RegisterEmployeeActivity.this, "Employee registration failed", Toast.LENGTH_SHORT).show();
                });
    }

    // Helper method to set up click listeners for buttons
    private void setupClickListener(int buttonId, Class<?> destinationClass) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterEmployeeActivity.this, destinationClass);
            startActivity(intent);
        });
    }
}
