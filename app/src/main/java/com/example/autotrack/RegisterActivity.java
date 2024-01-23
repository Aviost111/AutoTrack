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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextRegisterEmail, editTextRegisterConfirmEmail, editTextRegisterPwd,
            editTextRegisterConfirmPwd, editTextRegisterPhoneNumber, editTextRegisterFirstName,
            editTextRegisterLastName;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        editTextRegisterConfirmEmail = findViewById(R.id.RegisterConfirmEmail);
        editTextRegisterEmail = findViewById(R.id.registerEmail);
        editTextRegisterPwd = findViewById(R.id.RegisterPassword);
        editTextRegisterConfirmPwd = findViewById(R.id.RegisterConfirmPassword);
        editTextRegisterPhoneNumber = findViewById(R.id.registerPhone);
        editTextRegisterFirstName = findViewById(R.id.registerFirstName);
        editTextRegisterLastName = findViewById(R.id.registerLastName);

        Button buttonRegister = findViewById(R.id.RegisterButton);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Extracting user input from EditText fields
                String textFirstname = editTextRegisterFirstName.getText().toString();
                String textLastname = editTextRegisterLastName.getText().toString();
                String textPwd = editTextRegisterPwd.getText().toString();
                String textConfirmPwd = editTextRegisterConfirmPwd.getText().toString();
                String textEmail = editTextRegisterEmail.getText().toString();
                String textConfirmEmail = editTextRegisterConfirmEmail.getText().toString();
                String textPhoneNumber = editTextRegisterPhoneNumber.getText().toString();

                // Input validation checks
                if (TextUtils.isEmpty(textFirstname)) {
                    // Display error message for first name
                    Toast.makeText(RegisterActivity.this, "Please enter your first name", Toast.LENGTH_SHORT).show();
                    editTextRegisterFirstName.setError("First name required");
                    editTextRegisterFirstName.requestFocus();
                } else if (TextUtils.isEmpty(textLastname)) {
                    // Display error message for last name
                    Toast.makeText(RegisterActivity.this, "Please enter your last name", Toast.LENGTH_SHORT).show();
                    editTextRegisterLastName.setError("Last name required");
                    editTextRegisterLastName.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    // Display error message for email
                    Toast.makeText(RegisterActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    editTextRegisterEmail.setError("Email required");
                    editTextRegisterEmail.requestFocus();
                } else if (!textEmail.equals(textConfirmEmail)) {
                    // Display error message if email and email confirmation are not equal
                    Toast.makeText(RegisterActivity.this, "Email and Email confirmation are not equal", Toast.LENGTH_SHORT).show();
                    editTextRegisterConfirmEmail.setError("Email and Email confirmation are not equal");
                    editTextRegisterConfirmEmail.requestFocus();
                } else if (!PhoneNumberValidation(textPhoneNumber)) {
                    // Display error message for invalid phone number
                    Toast.makeText(RegisterActivity.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
                    editTextRegisterPhoneNumber.setError("Please enter a valid phone number");
                    editTextRegisterPhoneNumber.requestFocus();
                } else if (TextUtils.isEmpty(textPwd)) {
                    // Display error message for password
                    Toast.makeText(RegisterActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    editTextRegisterPwd.setError("Password required");
                    editTextRegisterPwd.requestFocus();
                } else if (textPwd.length() < 6) {
                    // Display error message for weak password
                    Toast.makeText(RegisterActivity.this, "Password should be at least 6 digits", Toast.LENGTH_SHORT).show();
                    editTextRegisterPwd.setError("Password too weak");
                    editTextRegisterPwd.requestFocus();
                } else if (!textPwd.equals(textConfirmPwd)) {
                    // Display error message if password and password confirmation are not equal
                    Toast.makeText(RegisterActivity.this, "Password and Password confirmation are not equal", Toast.LENGTH_SHORT).show();
                    editTextRegisterConfirmPwd.setError("Password and Password confirmation are not equal");
                    editTextRegisterConfirmPwd.requestFocus();
                    editTextRegisterConfirmPwd.clearComposingText();
                    editTextRegisterPwd.clearComposingText();
                } else {
                    // If all checks pass, register the user
                    registerUser(textFirstname, textLastname, textPhoneNumber, textEmail, textPwd);
                }
            }
        });
    }

    private void registerUser(String textFirstName, String textLastName, String textPhoneNumber,
                              String textEmail, String textPwd) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        // Create user
        auth.createUserWithEmailAndPassword(textEmail, textPwd).addOnCompleteListener(RegisterActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User registration successful
                            Toast.makeText(RegisterActivity.this, "User Registered", Toast.LENGTH_SHORT).show();
                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            // Create a user data map
                            Map<String, Object> user = new HashMap<>();
                            user.put("email", textEmail);
                            user.put("first_name", textFirstName);
                            user.put("last_name", textLastName);
                            user.put("phone", textPhoneNumber);

                            assert firebaseUser != null;
                            // Save user data to Firestore
                            db.collection("Managers").document(firebaseUser.getUid()).set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(RegisterActivity.this, "User saved", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle failure to save user data
                                            Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, e.toString());
                                        }
                                    });

                            // Send email verification to the user
                            firebaseUser.sendEmailVerification();

                            // Navigate to LoginActivity after successful registration
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            // Clear the activity stack to prevent going back via the backspace button
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            // Handle registration failure
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                editTextRegisterPwd.setError("Your password is too weak");
                                editTextRegisterPwd.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                editTextRegisterEmail.setError("Your email is invalid or already in use. Please re-enter.");
                                editTextRegisterEmail.requestFocus();
                            } catch (FirebaseAuthUserCollisionException e) {
                                editTextRegisterEmail.setError("User is already registered with this email.");
                                editTextRegisterEmail.requestFocus();
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private boolean PhoneNumberValidation(String number) {
        // Validate the phone number format
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
