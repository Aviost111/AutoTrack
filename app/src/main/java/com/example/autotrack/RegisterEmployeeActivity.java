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

import com.example.autotrack.InputValidator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterEmployeeActivity extends AppCompatActivity {

    private static final String TAG = "RegisterEmployeeActivity";

    private FirebaseFirestore firestore;

    // UI elements
    private EditText etEmail, etFirstName, etLastName, etPhone;
    private Button btnRegisterEmployee;
    private String managerId, companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_employee);

        // Set up click listener for the "Back" button
        setupClickListener(R.id.btnBack, ManagerActivity.class);

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance();

        // Get the currently authenticated user (manager)
        FirebaseUser managerUser = FirebaseAuth.getInstance().getCurrentUser();

        //Get the company ID from the manager's document and assign it to the companyId variable
        assert managerUser != null;
        managerId = managerUser.getUid();
        getCompanyIdFromManager(managerUser.getUid());

        // Initialize UI elements
        initializeViews();

        // Set click listener for the "Register Employee" button
        btnRegisterEmployee.setOnClickListener(view -> registerEmployee());
    }

    private void getCompanyIdFromManager(String managerUid) {
        // Retrieve the manager's document from Firestore
        firestore.collection("Managers")
                .document(managerUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Manager document found
                        // Extract the companyId from the document and assign it to the companyId variable
                        companyId = documentSnapshot.getString("company_id");
                    } else {
                        // Manager document does not exist
                        // Handle the case where the manager's document is not found
                        Toast.makeText(RegisterEmployeeActivity.this, "Manager document not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Error occurred while fetching manager document
                    // Handle the failure scenario
                    Toast.makeText(RegisterEmployeeActivity.this, "Failed to fetch manager document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Helper method to initialize UI elements
    private void initializeViews() {
        etEmail = findViewById(R.id.etEmail);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etPhone = findViewById(R.id.etPhone);
        btnRegisterEmployee = findViewById(R.id.btnRegisterEmployee);
    }

    // Helper method to handle the registration of an employee
    private void registerEmployee() {
        // Get data from EditText fields
        String email = etEmail.getText().toString();
        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        String phone = etPhone.getText().toString();

        // Validate all input fields
        if (validateInput(email, firstName, lastName, phone)) {
            // Continue with the registration process if all validations pass

            // Create a Map to store the employee data
            Map<String, String> employeeData = createEmployeeDataMap(firstName, lastName, email, phone, managerId, companyId);

            // Upload data to Firebase
            uploadDataToFirebase(email, employeeData);
        }
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
        // Create a new user with the provided email and password
        // As default, the new user's password is set to "password"
        auth.createUserWithEmailAndPassword(Objects.requireNonNull(data.get("email")), Objects.requireNonNull(data.get("phone"))).addOnCompleteListener(this,
                task -> {
                    if (task.isSuccessful()) {
                        // User registration successful
                        Toast.makeText(RegisterEmployeeActivity.this, "Employee registered successfully", Toast.LENGTH_SHORT).show();
                        FirebaseUser firebaseUser = auth.getCurrentUser();

                        assert firebaseUser != null;
                        firestore.collection("Employees")
                                .document(firebaseUser.getUid())
                                .set(data)
                                .addOnSuccessListener(aVoid -> {
                                    // Data successfully uploaded
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
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
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


    // Helper method to validate all input fields and display error messages
    private boolean validateInput(String email, String firstName, String lastName, String phone) {
        if (InputValidator.areFieldsEmpty(email, firstName, lastName, phone)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!InputValidator.isValidPhoneNumber(phone)) {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!InputValidator.isValidEmail(email)) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!InputValidator.isValidName(firstName)) {
            Toast.makeText(this, "Invalid first name ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!InputValidator.isValidName(lastName)) {
            Toast.makeText(this, "Invalid last name", Toast.LENGTH_SHORT).show();
            return false;
        }
        // All validations passed
        return true;
    }
}
