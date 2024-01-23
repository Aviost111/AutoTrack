package com.example.autotrack;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
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

        // Validate all input fields
        if (validateInput(companyId, managerId, email, firstName, lastName, phone)) {
            // Continue with the registration process if all validations pass

            // Create a Map to store the employee data
            Map<String, String> employeeData = createEmployeeDataMap(firstName, lastName, email, phone, managerId, companyId);

            // Upload data to Firebase
            uploadDataToFirebase(email, employeeData);
        }
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
    private Map<String, String> createEmployeeDataMap(String firstName, String lastName, String email, String phone, String managerId, String companyId) {
        Map<String, String> employeeData = new HashMap<>();
        employeeData.put("first_name", firstName);
        employeeData.put("last_name", lastName);
        employeeData.put("email", email);
        employeeData.put("phone", phone);
        employeeData.put("manager_ID", managerId);
        employeeData.put("company_ID", companyId);
        return employeeData;
    }

    // Helper method to upload data to Firebase
    private void uploadDataToFirebase(String documentID, Map<String, String> data) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(data.get("email"), data.get("password")).addOnCompleteListener(this,
                task -> {
                    if (task.isSuccessful()) {
                        // User registration successful
                        Toast.makeText(RegisterEmployeeActivity.this, "User Registered", Toast.LENGTH_SHORT).show();
                        FirebaseUser firebaseUser = auth.getCurrentUser();

                        assert firebaseUser != null;
                        firestore.collection("Employees")
                                .document(firebaseUser.getUid())
                                .set(data)
                                .addOnSuccessListener(aVoid -> {
                                    // Data successfully uploaded
                                    // Show a success message
                                    Toast.makeText(RegisterEmployeeActivity.this, "Employee registration was successful", Toast.LENGTH_LONG).show();

                                    // Navigate to ManagerActivity
                                    navigateToActivity(ManagerActivity.class);
                                })
                                .addOnFailureListener(e -> {
                                    // Error uploading data
                                    // Show an error message
                                    Toast.makeText(RegisterEmployeeActivity.this, "Employee registration failed", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // User registration failed
                        handleRegistrationFailure(task.getException());
                    }
                });
    }

    // Helper method to handle user registration failure
    private void handleRegistrationFailure(Exception exception) {
        try {
            throw exception;
        } catch (FirebaseAuthWeakPasswordException e) {
            setErrorAndRequestFocus(etPhone, "Your password is too weak");
        } catch (FirebaseAuthInvalidCredentialsException e) {
            setErrorAndRequestFocus(etEmail, "Your email is invalid or already in use. Please re-enter.");
        } catch (FirebaseAuthUserCollisionException e) {
            setErrorAndRequestFocus(etEmail, "User is already registered with this email.");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(RegisterEmployeeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to set error message for EditText and request focus
    private void setErrorAndRequestFocus(EditText editText, String errorMessage) {
        editText.setError(errorMessage);
        editText.requestFocus();
    }

    // Helper method to set up click listeners for buttons
    private void setupClickListener(int buttonId, Class<?> destinationClass) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> navigateToActivity(destinationClass));
    }

    // Helper method to navigate to another activity and finish the current activity
    private void navigateToActivity(Class<?> destinationClass) {
        Intent intent = new Intent(RegisterEmployeeActivity.this, destinationClass);
        startActivity(intent);
        finish(); // Finish the current activity to prevent going back via backspace button
    }

    // Function to validate all input fields and show error messages if needed (using Toast)
    private boolean validateInput(String companyId, String managerId, String email,
                                  String firstName, String lastName, String phone) {
        // Validate that all fields are filled
        if (areFieldsEmpty(companyId, managerId, email, firstName, lastName, phone)) {
            // Show an error message using a Toast
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false; // Exit the method if any field is empty
        }

        // Validate phone number
        if (!PhoneNumberValidation(phone)) {
            // Show an error message using a Toast
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            return false; // Exit the method if the phone number is invalid
        }

        // Validate email
        if (!isValidEmail(email)) {
            // Show an error message using a Toast
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return false; // Exit the method if the email is invalid
        }

        // Validate company ID and manager ID (assuming they should be numeric)
        if (!isValidNumericId(companyId) || !isValidNumericId(managerId)) {
            // Show an error message using a Toast
            Toast.makeText(this, "Invalid company ID or manager ID", Toast.LENGTH_SHORT).show();
            return false; // Exit the method if company ID or manager ID is invalid
        }

        // Validate first name and last name
        if (!isValidName(firstName) || !isValidName(lastName)) {
            // Show an error message using a Toast
            Toast.makeText(this, "Invalid first name or last name", Toast.LENGTH_SHORT).show();
            return false; // Exit the method if first name or last name is invalid
        }

        // If all validations pass, return true
        return true;
    }

    // Validate phone number format
    private boolean PhoneNumberValidation(String number) {
        if (number.length() != 10) {
            return false;
        }
        for (int i = 0; i < number.length(); i++) {
            if (number.charAt(i) < '0' || number.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }

    // Validate email address
    private boolean isValidEmail(String email) {
        // Use android.util.Patterns.EMAIL_ADDRESS
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Validate numeric ID
    private boolean isValidNumericId(String id) {
        // Use Integer.parseInt() and catch NumberFormatException
        try {
            Integer.parseInt(id);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Validate first name and last name
    private boolean isValidName(String name) {
        // Check if the name contains only letters
        return name.matches("[a-zA-Z]+");
    }
}
