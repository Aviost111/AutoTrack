package com.example.autotrack;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class RegisterManagerActivity extends AppCompatActivity {

    private static final String TAG = "RegisterManagerActivity";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private EditText editTextRegisterEmail, editTextRegisterConfirmEmail, editTextRegisterPwd,
            editTextRegisterConfirmPwd, editTextRegisterPhoneNumber, editTextRegisterFirstName,
            editTextRegisterLastName, editTextRegisterCompanyID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Set the title for the ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Register");
        } else {
            // Handle the case where the ActionBar is not available
        }

        // Initialize EditText and Button views
        initializeViews();

        Button buttonRegister = findViewById(R.id.RegisterButton);
        buttonRegister.setOnClickListener(v -> handleRegistration());
    }

    // Helper method to initialize EditText views
    private void initializeViews() {
        editTextRegisterConfirmEmail = findViewById(R.id.RegisterConfirmEmail);
        editTextRegisterEmail = findViewById(R.id.registerEmail);
        editTextRegisterPwd = findViewById(R.id.RegisterPassword);
        editTextRegisterConfirmPwd = findViewById(R.id.RegisterConfirmPassword);
        editTextRegisterPhoneNumber = findViewById(R.id.registerPhone);
        editTextRegisterFirstName = findViewById(R.id.registerFirstName);
        editTextRegisterLastName = findViewById(R.id.registerLastName);
        editTextRegisterCompanyID = findViewById(R.id.registerCompanyID);
    }

    // Helper method to handle the registration process
    private void handleRegistration() {
        String textFirstName = editTextRegisterFirstName.getText().toString();
        String textLastName = editTextRegisterLastName.getText().toString();
        String textPwd = editTextRegisterPwd.getText().toString();
        String textConfirmPwd = editTextRegisterConfirmPwd.getText().toString();
        String textEmail = editTextRegisterEmail.getText().toString();
        String textConfirmEmail = editTextRegisterConfirmEmail.getText().toString();
        String textPhoneNumber = editTextRegisterPhoneNumber.getText().toString();
        String textCompanyID = editTextRegisterCompanyID.getText().toString();

        if (validateInputs(textFirstName, textLastName, textCompanyID, textEmail, textConfirmEmail, textPhoneNumber, textPwd, textConfirmPwd)) {
            registerUser(textFirstName, textLastName, textPhoneNumber, textEmail, textPwd, textCompanyID);
        }
    }

    // Helper method to validate user inputs
    private boolean validateInputs(String firstName, String lastName, String companyID, String email, String confirmEmail,
                                   String phoneNumber, String password, String confirmPassword) {
        if (TextUtils.isEmpty(firstName)) {
            showErrorAndFocus(editTextRegisterFirstName, "Please enter your first name");
            return false;
        } else if (TextUtils.isEmpty(lastName)) {
            showErrorAndFocus(editTextRegisterLastName, "Please enter your last name");
            return false;
        } else if (TextUtils.isEmpty(companyID)) {
            showErrorAndFocus(editTextRegisterCompanyID, "Please enter your CompanyID");
            return false;
        } else if (TextUtils.isEmpty(email)) {
            showErrorAndFocus(editTextRegisterEmail, "Please enter your email");
            return false;
        } else if (!email.equals(confirmEmail)) {
            showErrorAndFocus(editTextRegisterConfirmEmail, "Email and Email confirmation are not equal");
            return false;
        } else if (!PhoneNumberValidation(phoneNumber)) {
            showErrorAndFocus(editTextRegisterPhoneNumber, "Please enter a valid phone number");
            return false;
        } else if (TextUtils.isEmpty(password)) {
            showErrorAndFocus(editTextRegisterPwd, "Please enter your password");
            return false;
        } else if (password.length() < 6) {
            showErrorAndFocus(editTextRegisterPwd, "Password should be at least 6 digits");
            return false;
        } else if (!password.equals(confirmPassword)) {
            showErrorAndFocus(editTextRegisterConfirmPwd, "Password and Password confirmation are not equal");
            return false;
        }
        return true;
    }

    // Helper method to show an error message and set focus to an EditText
    private void showErrorAndFocus(EditText editText, String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        editText.setError(errorMessage);
        editText.requestFocus();
    }

    // Helper method to register the user
    private void registerUser(String textFirstName, String textLastName, String textPhoneNumber,
                              String textEmail, String textPwd, String companyID) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail, textPwd).addOnCompleteListener(this,
                task -> {
                    if (task.isSuccessful()) {
                        handleSuccessfulRegistration(textEmail, textFirstName, textLastName, textPhoneNumber, companyID);
                    } else {
                        handleRegistrationFailure(task);
                    }
                });
    }

    // Helper method to handle successful registration
    private void handleSuccessfulRegistration(String email, String firstName, String lastName, String phoneNumber, String companyID) {
        Toast.makeText(RegisterManagerActivity.this, "User Registered", Toast.LENGTH_SHORT).show();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("first_name", firstName);
        user.put("last_name", lastName);
        user.put("phone", phoneNumber);
        user.put("company_id", companyID);

        assert firebaseUser != null;
        db.collection("Managers").document(firebaseUser.getUid()).set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterManagerActivity.this, "User saved", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterManagerActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                });

        firebaseUser.sendEmailVerification();

        Intent intent = new Intent(RegisterManagerActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    // Helper method to handle registration failure
    private void handleRegistrationFailure(Task<AuthResult> task) {
        try {
            throw task.getException();
        } catch (FirebaseAuthWeakPasswordException e) {
            showErrorAndFocus(editTextRegisterPwd, "Your password is too weak");
        } catch (FirebaseAuthInvalidCredentialsException e) {
            showErrorAndFocus(editTextRegisterEmail, "Your email is invalid or already in use. Please re-enter.");
        } catch (FirebaseAuthUserCollisionException e) {
            showErrorAndFocus(editTextRegisterEmail, "User is already registered with this email.");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(RegisterManagerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to validate the phone number format
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
}
