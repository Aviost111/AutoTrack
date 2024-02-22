
package com.example.autotrack.Model;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.autotrack.R;
import com.example.autotrack.controler.FirestoreController;
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
import java.util.Objects;

public class RegisterCompanyActivity extends AppCompatActivity {

    private EditText editTextRegisterEmail, editTextRegisterConfirmEmail, editTextRegisterPwd,
            editTextRegisterConfirmPwd, editTextRegisterPhoneNumber, editTextRegisterFirstName,
            editTextRegisterLastName;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_manager);

        // Set the title for the ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Register");
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
        TextView signIn = findViewById(R.id.SignIn);
        buttonRegister.setOnClickListener(v -> {
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
                Toast.makeText(RegisterCompanyActivity.this, "Please enter your first name", Toast.LENGTH_SHORT).show();
                editTextRegisterFirstName.setError("First name required");
                editTextRegisterFirstName.requestFocus();
            } else if (TextUtils.isEmpty(textLastname)) {
                // Display error message for last name
                Toast.makeText(RegisterCompanyActivity.this, "Please enter your last name", Toast.LENGTH_SHORT).show();
                editTextRegisterLastName.setError("Last name required");
                editTextRegisterLastName.requestFocus();
            } else if (TextUtils.isEmpty(textEmail)) {
                // Display error message for email
                Toast.makeText(RegisterCompanyActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                editTextRegisterEmail.setError("Email required");
                editTextRegisterEmail.requestFocus();
            } else if (!textEmail.equals(textConfirmEmail)) {
                // Display error message if email and email confirmation are not equal
                Toast.makeText(RegisterCompanyActivity.this, "Email and Email confirmation are not equal", Toast.LENGTH_SHORT).show();
                editTextRegisterConfirmEmail.setError("Email and Email confirmation are not equal");
                editTextRegisterConfirmEmail.requestFocus();
            } else if (!PhoneNumberValidation(textPhoneNumber)) {
                // Display error message for invalid phone number
                Toast.makeText(RegisterCompanyActivity.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
                editTextRegisterPhoneNumber.setError("Please enter a valid phone number");
                editTextRegisterPhoneNumber.requestFocus();
            } else if (TextUtils.isEmpty(textPwd)) {
                // Display error message for password
                Toast.makeText(RegisterCompanyActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                editTextRegisterPwd.setError("Password required");
                editTextRegisterPwd.requestFocus();
            } else if (textPwd.length() < 6) {
                // Display error message for weak password
                Toast.makeText(RegisterCompanyActivity.this, "Password should be at least 6 digits", Toast.LENGTH_SHORT).show();
                editTextRegisterPwd.setError("Password too weak");
                editTextRegisterPwd.requestFocus();
            } else if (!textPwd.equals(textConfirmPwd)) {
                // Display error message if password and password confirmation are not equal
                Toast.makeText(RegisterCompanyActivity.this, "Password and Password confirmation are not equal", Toast.LENGTH_SHORT).show();
                editTextRegisterConfirmPwd.setError("Password and Password confirmation are not equal");
                editTextRegisterConfirmPwd.requestFocus();
                editTextRegisterConfirmPwd.clearComposingText();
                editTextRegisterPwd.clearComposingText();
            } else {
                // If all checks pass, register the user
                CompanyObj newCompany = new CompanyObj(textFirstname,textLastname,textEmail,textPhoneNumber);

                registerUser(newCompany,textPwd);
            }
        });

        signIn.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterCompanyActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void registerUser(CompanyObj newCompany,String textPwd) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        // Create user
        auth.createUserWithEmailAndPassword(newCompany.getEmail(), textPwd).addOnCompleteListener(RegisterCompanyActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            // User registration successful
                            Toast.makeText(RegisterCompanyActivity.this, "User Registered", Toast.LENGTH_SHORT).show();
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            FirestoreController controller =new FirestoreController(RegisterCompanyActivity.this);
                            controller.saveCompanyData(newCompany,firebaseUser);
                            controller.saveUserData(newCompany,firebaseUser);
//
//                            // Create a user data map company
//                            Map<String, Object> company = new HashMap<>();
//                            company.put("email", newCompany.getEmail());
//                            company.put("first_name", newCompany.getFirst_name());
//                            company.put("last_name", newCompany.getLast_name());
//                            company.put("phone", newCompany.getPhone());
//                            company.put("company_id", firebaseUser.getUid());
//
//                            //Create a user data map for user
//                            Map<String,Object> users =new HashMap<>();
//                            users.put("company_id",firebaseUser.getUid());
//                            users.put("first_name", newCompany.getFirst_name());
//                            users.put("last_name", newCompany.getLast_name());
//                            users.put("is_manager",true);
//
//                            // Save user data to FireStore
//                            db.collection("Companies").document(firebaseUser.getUid()).set(company)
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//                                            Toast.makeText(RegisterCompanyActivity.this, "User saved", Toast.LENGTH_SHORT).show();
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            // Handle failure to save user data
//                                            Toast.makeText(RegisterCompanyActivity.this, "Error in company", Toast.LENGTH_SHORT).show();
//                                            Log.d(TAG, e.toString());
//                                        }
//                                    });
//                            db.collection("Users").document(Objects.requireNonNull(firebaseUser.getEmail())).set(users)
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            // Handle failure to save user data
//                                            Toast.makeText(RegisterCompanyActivity.this, "Error in user", Toast.LENGTH_SHORT).show();
//                                            Log.d(TAG, e.toString());
//                                        }
//                                    });
//
//
                            // Send email verification to the user
                            firebaseUser.sendEmailVerification();

                            // Navigate to LoginActivity after successful registration
                            Intent intent = new Intent(RegisterCompanyActivity.this, LoginActivity.class);
                            // Clear the activity stack to prevent going back via the backspace button
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            // Handle registration failure
                            try {
                                throw Objects.requireNonNull(task.getException());
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
                                Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                                Toast.makeText(RegisterCompanyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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