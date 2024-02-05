package com.example.autotrack;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteDialogHelper {
    public static void showDialog(Context context,FirebaseFirestore db, String companyUid, String type) {
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
            deleteEntity(context,db, companyUid, type, id);
        });

        // Set up the cancel button
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Method to delete the entity from the database
    private static void deleteEntity(Context context,FirebaseFirestore db,String companyUid, String type, String id) {
        // Remove the entity from the database
//        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (type.equals("Employees")) {
            // Delete the employee document from the "Employees" collection
            db.collection("Companies").document(companyUid).collection("Employees").document(id)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // Employee successfully deleted
                        Toast.makeText(context, "Employee deleted successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Failed to delete employee
                        Toast.makeText(context, "Failed to delete employee: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else if (type.equals("Vehicles")) {
            // Delete the vehicle document from the "Vehicles" collection
            db.collection("Companies").document(companyUid).collection("Vehicles").document(id)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // Vehicle successfully deleted
                        Toast.makeText(context, "Vehicle deleted successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Failed to delete vehicle
                        Toast.makeText(context, "Failed to delete vehicle: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Invalid entity type
            Toast.makeText(context, "Invalid entity type", Toast.LENGTH_SHORT).show();
        }
    }

}
