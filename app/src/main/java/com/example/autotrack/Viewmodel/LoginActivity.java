package com.example.autotrack.Viewmodel;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.autotrack.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextView signUp;
    private EditText editTextEmail, editTextPwd;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI elements
        editTextEmail = findViewById(R.id.emailLogin);
        editTextPwd = findViewById(R.id.passwordLogin);
        Button loginButton = findViewById(R.id.loginButton);
        signUp = findViewById(R.id.signUp);

        // Set onClickListener for login button
        loginButton.setOnClickListener(v -> attemptLogin());

        // Set onClickListener for sign-up TextView
        signUp.setOnClickListener(v -> underlineAndStartRegistration());
    }

    private void attemptLogin() {
        String email = editTextEmail.getText().toString();
        String pwd = editTextPwd.getText().toString();

        if (TextUtils.isEmpty(email)) {
            showErrorAndFocus(editTextEmail, "Please enter an email!");
        } else if (TextUtils.isEmpty(pwd)) {
            showErrorAndFocus(editTextPwd, "Please enter your Password!");
        } else {
            authenticateUser(email, pwd);
        }
    }

    private void showErrorAndFocus(EditText editText, String errorMessage) {
        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
        editText.setError(errorMessage);
        editText.requestFocus();
    }

    private void authenticateUser(String email, String pwd) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    handleSuccessfulLogin();
                } else {
                    handleLoginFailure(task);
                }
            }
        });
    }

    private void handleSuccessfulLogin() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        checkUserTypeAndNavigate(firebaseUser.getUid());
    }

    private void handleLoginFailure(Task<AuthResult> task) {
        try {
            throw Objects.requireNonNull(task.getException());
        } catch (FirebaseAuthInvalidCredentialsException e) {
            showErrorAndFocus(editTextPwd, "Incorrect Email or Password");
        } catch (Exception e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUserTypeAndNavigate(String uid) {
        db.collection("Companies").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        navigateToCompanyActivity();
                    } else {
                        navigateToEmployeeActivity();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "User doesn't exist!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void navigateToCompanyActivity() {
        Toast.makeText(LoginActivity.this, "Company", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, CompanyActivity.class);
        intent.putExtra("company_uid", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        editTextEmail.setText("");
        editTextPwd.setText("");
        startActivity(intent);
    }

    private void navigateToEmployeeActivity() {
        Toast.makeText(LoginActivity.this, "Employee", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, VehicleListActivity.class);
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        intent.putExtra("email", email);
        startActivity(intent);
    }

    private void underlineAndStartRegistration() {
        underlineText(signUp);
        new Handler().postDelayed(this::startRegistrationActivity, 200);
    }

    private void startRegistrationActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterCompanyActivity.class);
        startActivity(intent);
    }

    private void underlineText(TextView textView) {
        String text = textView.getText().toString();
        SpannableString spannableString = new SpannableString(text);
        UnderlineSpan underlineSpan = new UnderlineSpan();
        spannableString.setSpan(underlineSpan, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);

        textView.postDelayed(() -> {
            spannableString.removeSpan(underlineSpan);
            textView.setText(spannableString);
        }, 100);
    }
}
