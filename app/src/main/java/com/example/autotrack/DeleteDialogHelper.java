package com.example.autotrack;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class DeleteDialogHelper {

    public static void showDialog(Context context, FirebaseFirestore db, String companyUid, String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set the dialog title and message based on the entity type
        if (type.equals("Employees")) {
            builder.setTitle("Remove Employee");
            builder.setMessage("Enter the email of the employee to remove:");
        } else if (type.equals("Vehicles")) {
            builder.setTitle("Remove Vehicle");
            builder.setMessage("Enter the ID of the vehicle to remove:");
        } else {
            Toast.makeText(context, "Invalid entity type", Toast.LENGTH_SHORT).show();
            return; // Exit if type is invalid
        }

        // Set up the input field
        final EditText input = new EditText(context);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", null);

        // Set up the cancel button
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create(); // Create the dialog

        // Override the positive button behavior
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
                String id = input.getText().toString().trim(); // Trim to remove leading/trailing whitespace

                // Check if input is empty
                if (id.isEmpty()) {
                    Toast.makeText(context, "Please enter a valid " + type + " " + (type.equals("Employees") ? "email" : "ID"), Toast.LENGTH_SHORT).show();
                } else {
                    // Call a method to delete the entity with the provided ID
                    deleteEntity(context, db, companyUid, type, id);
                    dialog.dismiss(); // Dismiss the dialog after deletion
                }
            });
        });

        dialog.show(); // Show the dialog
    }


    private static void deleteEntity(Context context, FirebaseFirestore db, String companyUid, String type, String id) {
        DocumentReference docRef;
        CollectionReference historyRef;

        if (type.equals("Employees")) {
            // Get the document reference for the employee
            docRef = db.collection("Companies").document(companyUid).collection("Employees").document(id);
            historyRef = docRef.collection("history");
        } else if (type.equals("Vehicles")) {
            // Get the document reference for the vehicle
            docRef = db.collection("Companies").document(companyUid).collection("Vehicles").document(id);
            historyRef = docRef.collection("history");
        } else {
            Toast.makeText(context, "Invalid entity type", Toast.LENGTH_SHORT).show();
            return; // Exit if type is invalid
        }

        // Check if the document exists before attempting to delete
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    // Document exists, proceed with deletion
                    deleteDocumentAndSubCollection(context, docRef, historyRef, type);
                } else {
                    // Document does not exist
                    Toast.makeText(context, type + " with ID/email " + id + " does not exist", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Task failed with an exception
                Toast.makeText(context, "Failed to check existence of " + type + ": " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper method to delete the document
    private static void deleteDocumentAndSubCollection(Context context, DocumentReference docRef, CollectionReference historyRef, String type) {
        docRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, type + " deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete " + type + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Delete documents within the subCollection
        deleteDocumentsInSubCollection(context, historyRef);
    }

    // Helper method to delete documents in a subCollection
    private static void deleteDocumentsInSubCollection(Context context, CollectionReference subCollectionRef) {
        subCollectionRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Delete all documents in the subCollection
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        snapshot.getReference().delete();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete documents in subCollection: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}