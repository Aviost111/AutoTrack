package com.example.autotrack;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.Toast;

public class DeleteDialogHelper {
    public static void showDialog(Context context, String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete " + type);
        builder.setMessage("Enter the ID of the " + type + " to delete:");

        // Set up the input
        final EditText input = new EditText(context);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            String id = input.getText().toString();
            // Call a method to delete the entity with the provided ID
            deleteEntity(context, type, id);
        });

        // Set up the cancel button
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Method to delete the entity from the database
    private static void deleteEntity(Context context, String type, String id) {
        // Implement the logic to delete the entity (employee/vehicle) with the provided ID from the database
        // You can show a toast message to indicate success or failure
        Toast.makeText(context, type + " deleted successfully", Toast.LENGTH_SHORT).show();
    }
}
