package com.example.autotrack;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterEmployeeActivity extends AppCompatActivity {

    private static final String TAG = "RegisterEmployeeActivity";

    private FirebaseFirestore firestore;

    // UI elements
    private EditText etEmail, etFirstName, etLastName, etPhone;
    private Button btnRegisterEmployee;
    private String companyId;
    private String companyPassword;
    private String companyEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_employee);

        // Set up click listener for the "Back" button
        setupClickListener(R.id.btnBack, CompanyActivity.class);

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance();

        // Get the currently authenticated user (company manager)
        FirebaseUser companyUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        companyPassword = intent.getStringExtra("password");

        //Get the company ID from the manager's document and assign it to the companyId variable
        assert companyUser != null;
        companyEmail = companyUser.getEmail();
        companyId = companyUser.getUid();

        // Initialize UI elements
        initializeViews();

        // Set click listener for the "Register Employee" button
        btnRegisterEmployee.setOnClickListener(view -> registerEmployee());
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
            Map<String, String> employeeData = createEmployeeDataMap(firstName, lastName, email, phone, companyId);

            // Upload data to Firebase
            uploadDataToFirebase(employeeData, email);
        }
    }

    // Helper method to create a Map with employee data
    private Map<String, String> createEmployeeDataMap(String firstName, String lastName, String email, String phone, String companyId) {
        Map<String, String> employeeData = new HashMap<>();
        employeeData.put("first_name", firstName);
        employeeData.put("last_name", lastName);
        employeeData.put("email", email);
        employeeData.put("phone", phone);
        employeeData.put("company_id", companyId);
        return employeeData;
    }

    // Helper method to upload data to Firebase
    private void uploadDataToFirebase(Map<String, String> data, String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        // Create a new user with the provided email and password
        // As default, the new user's password is set to "password"
        auth.createUserWithEmailAndPassword(Objects.requireNonNull(data.get("email")), Objects.requireNonNull(data.get("phone"))).addOnCompleteListener(this,
                task -> {
                    if (task.isSuccessful()) {
                        // User registration successful
                        Toast.makeText(RegisterEmployeeActivity.this, "Employee registered successfully", Toast.LENGTH_SHORT).show();
                        FirebaseUser firebaseUser = auth.getCurrentUser();

                        // Sign out the current user (employee) and sign in as the company manager
                        auth.signOut();
                        auth.signInWithEmailAndPassword(companyEmail, companyPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign-in as company manager successful
                                    Log.d(TAG, "Signed in as company manager");

                                    // Proceed with Firestore operations
                                    addEmployeeToFirestore(data, email);
                                } else {
                                    // Sign-in as company manager failed
                                    Toast.makeText(RegisterEmployeeActivity.this, "Failed to sign in as company manager", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        // User registration failed
                        handleRegistrationFailure(task.getException());
                    }
                });
    }

    // Helper method to add employee data to Firestore
    private void addEmployeeToFirestore(Map<String, String> data, String email) {
        // Access Firestore instance
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Add employee data to Firestore
        firestore.collection("Companies")
                .document(companyId)
                .collection("Employees")
                .document(email)
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    // Data successfully uploaded
                    // Create the "history" subCollection
                    createHistorySubCollection(email);

                    // Add the employee mail to the company's employees list
                    addToUsersList(email);

                    // Navigate to the CompanyActivity
                    navigateToActivity(CompanyActivity.class);
                })
                .addOnFailureListener(e -> {
                    // Error uploading data
                    Toast.makeText(RegisterEmployeeActivity.this, "Employee registration failed", Toast.LENGTH_SHORT).show();
                });
    }


    private void addToUsersList(String email) {
        // Update the "User-ManagerId" data
        Map<String, Object> userData = new HashMap<>();
        userData.put("company_id", companyId);
        userData.put("is_manager", false);
        userData.put("first_name", etFirstName.getText().toString());
        userData.put("last_name", etLastName.getText().toString());

        firestore.collection("Users")
                .document(email)
                .set(userData);
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
        finish();
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


    // Helper method to create a subCollection for "history"
    private void createHistorySubCollection(String documentID) {
        // Create a subCollection reference for "history"
        CollectionReference historySubCollectionRef = firestore.collection("Companies")
                .document(companyId).collection("Employees").document(documentID).collection("history");

        // Create an empty document for "refueling" in the "history" subCollection
        historySubCollectionRef.document("refuels").set(new HashMap<>());

        // Create an empty document for "vehicle_care" in the "history" subCollection
        historySubCollectionRef.document("treatments").set(new HashMap<>());

        // Create an empty document for "start-stop" in the "history" subCollection
        historySubCollectionRef.document("start-stop").set(new HashMap<>());
    }


}
