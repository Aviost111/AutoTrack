package com.example.autotrack.Viewmodel;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.autotrack.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class DeleteDialogHelper {

    public static void showDialog(Context context, FirebaseFirestore db, String companyUid, String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.RoundedDialog);

        // Inflate custom layout for dialog content
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_delete_entity, null);

        builder.setView(view);

        // Get references to dialog views
        TextView textViewTitle = view.findViewById(R.id.textViewDialogTitle);
        EditText editTextId = view.findViewById(R.id.editTextId);
        Button buttonCancel = view.findViewById(R.id.buttonCancel);
        Button buttonDelete = view.findViewById(R.id.buttonOk);

        // Set the dialog title and message based on the entity type
        if (type.equals("Employees")) {
            textViewTitle.setText("Remove Employee");
            editTextId.setHint("Enter Email");
        } else if (type.equals("Vehicles")) {
            textViewTitle.setText("Remove Vehicle");
            editTextId.setHint("Enter ID");
        } else {
            Toast.makeText(context, "Invalid entity type", Toast.LENGTH_SHORT).show();
            return; // Exit if type is invalid
        }

        // Create the dialog
        AlertDialog dialog = builder.create();

        // Set button click listener for "OK" button
        buttonDelete.setOnClickListener(v -> {
            String id = editTextId.getText().toString().trim();
            // Check if input is empty
            if (!id.isEmpty()) {
                // Call method to delete entity
                deleteEntity(context, db, companyUid, type, id);
                dialog.dismiss(); // Dismiss the dialog after successful deletion
            } else {
                Toast.makeText(context, "Please enter ID", Toast.LENGTH_SHORT).show();
                // Do not dismiss the dialog in this case
            }
        });

        // Set button click listener for "Cancel" button
        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        // Show the dialog
        dialog.show();
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