package com.example.autotrack;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

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
        CollectionReference historyRef;

        if (type.equals("Employees")) {
            docRef = db.collection("Companies").document(companyUid).collection("Employees").document(id);
            historyRef = docRef.collection("history");
        } else if (type.equals("Vehicles")) {
            docRef = db.collection("Companies").document(companyUid).collection("Vehicles").document(id);
            historyRef = docRef.collection("history");
        } else {
            Toast.makeText(context, "Invalid entity type", Toast.LENGTH_SHORT).show();
            return; // Exit if type is invalid
        }

        // Delete the parent document and its subCollection
        deleteDocumentAndSubCollection(context, docRef, historyRef, type);
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
                    Toast.makeText(context, "Failed to delete documents in subcollection: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
