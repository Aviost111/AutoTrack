package com.example.autotrack;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class DeleteDialogHelper {
    public static void showDialog(Context context, FirebaseFirestore db, String companyUid, String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete " + type);
        builder.setMessage("Enter the ID of the " + type + " to remove:");

        // Set up the input field
        final EditText input = new EditText(context);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            String id = input.getText().toString();
            // Call a method to delete the entity with the provided ID
            deleteEntity(context, db, companyUid, type, id);
        });

        // Set up the cancel button
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }


    private static void deleteEntity(Context context, FirebaseFirestore db, String companyUid, String type, String id) {
        DocumentReference docRef;
        if (type.equals("Employees")) {
            docRef = db.collection("Companies").document(companyUid).collection("Employees").document(id);
            db.collection("Users").document(id).delete();
        } else if (type.equals("Vehicles")) {
            docRef = db.collection("Companies").document(companyUid).collection("Vehicles").document(id);
        } else {
            Toast.makeText(context, "Invalid entity type", Toast.LENGTH_SHORT).show();
            return; // Exit if type is invalid
        }

        // Delete the parent document and its subCollection
        deleteDocumentAndSubCollection(docRef, context, type);
    }

    private static void deleteDocumentAndSubCollection(DocumentReference docRef, Context context, String type) {
        if (type.equals("Employees")) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();

            if (user != null) {
                user.delete()
                        .addOnSuccessListener(aVoid -> {
                            // User deleted successfully
                            // Proceed to delete user data from Firestore
                            deleteUserDataFromFirestore(user.getUid());
                        })
                        .addOnFailureListener(e -> {
                            // Failed to delete user
                            // Handle the failure, e.g., show an error message
                        });

            }
        }
        docRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, type + " deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete " + type + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Delete documents within the subCollection
        deleteDocumentsInSubCollection(docRef.collection("history"));
    }

    private static void deleteDocumentsInSubCollection(CollectionReference subCollectionRef) {
        subCollectionRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        snapshot.getReference().delete();
                    }
                });
    }

    private static void deleteUserDataFromFirestore(String uid) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("Users")
                .document(uid)
                .delete()
                .addOnSuccessListener(result -> {
                    // User data in Firestore deleted successfully
                    // Optionally, delete other related data or collections
                })
                .addOnFailureListener(e -> {
                    // Failed to delete user data from Firestore
                    // Handle the failure, e.g., show an error message
                });
    }
}



