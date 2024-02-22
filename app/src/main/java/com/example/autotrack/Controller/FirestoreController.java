package com.example.autotrack.Controller;

import static androidx.fragment.app.FragmentManager.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.autotrack.Model.CompanyObj;
import com.example.autotrack.Model.RegisterCompanyActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FirestoreController {
    final private FirebaseFirestore firestore;
    private Context context;

    public FirestoreController(Context context) {
        this.firestore = FirebaseFirestore.getInstance();
        this.context = context;
    }

    public void saveCompanyData(CompanyObj newCompany, FirebaseUser firebaseUser) {
        // Create a user data map for the company
        Map<String, Object> company = new HashMap<>();
        company.put("email", newCompany.getEmail());
        company.put("first_name", newCompany.getFirst_name());
        company.put("last_name", newCompany.getLast_name());
        company.put("phone", newCompany.getPhone());
        company.put("company_id", firebaseUser.getUid());

        // Save company data to Firestore
        firestore.collection("Companies").document(firebaseUser.getUid()).set(company)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Company data saved", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    // Handle failure to save company data
                    Toast.makeText(context, "Error in company data", Toast.LENGTH_SHORT).show();
                });
    }

    public void saveUserData(CompanyObj newCompany, FirebaseUser firebaseUser) {
        // Create a user data map for the user
        Map<String, Object> users = new HashMap<>();
        users.put("company_id", firebaseUser.getUid());
        users.put("first_name", newCompany.getFirst_name());
        users.put("last_name", newCompany.getLast_name());
        users.put("is_manager", true);

        // Save user data to Firestore
        firestore.collection("Users").document(Objects.requireNonNull(firebaseUser.getEmail())).set(users)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "User data saved", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    // Handle failure to save user data
                    Toast.makeText(context, "Error in user data", Toast.LENGTH_SHORT).show();
                });
    }
}