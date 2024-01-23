package com.example.autotrack;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private TextView signUp;
    private EditText editTextemail, editTextpwd;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextemail = findViewById(R.id.emailLogin);
        editTextpwd = findViewById(R.id.passwordLogin);


        Button loginButton = findViewById(R.id.loginButton);
        signUp = findViewById(R.id.signUp);
        loginButton.setOnClickListener(new View.OnClickListener() {
            //log into app
            @Override
            public void onClick(View v) {

                String email = editTextemail.getText().toString();
                String pwd = editTextpwd.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    // Display error message for first name
                    Toast.makeText(LoginActivity.this, "Please enter an email", Toast.LENGTH_SHORT).show();
                    editTextemail.setError("Email required");
                    editTextemail.requestFocus();
                    return;
                }else if(TextUtils.isEmpty(pwd)) {
                    Toast.makeText(LoginActivity.this, "Please enter your Password", Toast.LENGTH_SHORT).show();
                    editTextpwd.setError("Password required");
                    editTextpwd.requestFocus();
                    return;
                }
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            db.collection("Managers").document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().exists()) {
                                            Toast.makeText(LoginActivity.this, "manager", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(LoginActivity.this, ManagerActivity.class);
                                            editTextemail.setText("");
                                            editTextpwd.setText("");
                                            startActivity(intent);
                                        } else {
                                            //TODO goes to register if its an employee
                                            Toast.makeText(LoginActivity.this, "employee", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(LoginActivity.this, EmployeeActivity.class);
                                            startActivity(intent);
                                        }
                                    } else {
                                        Toast.makeText(LoginActivity.this, "User doesn't exist", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                editTextpwd.setError("Incorrect Email or Password");
                                editTextpwd.requestFocus();
                            }catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();


                            }

                        }
                    }
                });
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler(); // Create a Handler for delayed actions

                underlineText(signUp); // Apply underline animation
                underlineText(signUp);

                // Start RegisterActivity after animation
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                        startActivity(intent);
                    }
                }, 200); // Adjust delay as needed
            }
        });
    }

    private void

    underlineText(TextView textView) {
        String text = textView.getText().toString();
        SpannableString spannableString = new SpannableString(text);
        UnderlineSpan underlineSpan = new UnderlineSpan();
        spannableString.setSpan(underlineSpan, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);

        // Add a slight delay before removing underline
        textView.postDelayed(new Runnable() {
            @Override
            public void run() {
                spannableString.removeSpan(underlineSpan);
                textView.setText(spannableString);
            }
        }, 100); // Adjust delay as needed
    }

}
