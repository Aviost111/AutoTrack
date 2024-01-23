package com.example.autotrack;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class RegisterToolActivity extends AppCompatActivity {

    // Tag for logging purposes
    private static final String TAG = "RegisterToolActivity";

    // Firebase Firestore instance
    private FirebaseFirestore firestore;


    // UI elements
    private EditText etType, etCompanyID, etEngineSize, etManufactureYear, etTreatmentHours, etVersion, etID;
    private Button btnRegisterTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_tool);

        // Initialize UI elements
        initializeViews();

        // Set up click listener for the "Back" button
        setupClickListener(R.id.btnBack, ManagerActivity.class);

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance();

        // Set click listener for the "Register Tool" button
        btnRegisterTool.setOnClickListener(view -> registerTool());
    }

    // Helper method to initialize UI elements
    private void initializeViews() {
        etType = findViewById(R.id.etType);
        etID = findViewById(R.id.etID);
        etCompanyID = findViewById(R.id.etCompanyID);
        etEngineSize = findViewById(R.id.etEngineSize);
        etManufactureYear = findViewById(R.id.etManufactureYear);
        etTreatmentHours = findViewById(R.id.etTreatmentHours);
        etVersion = findViewById(R.id.etVersion);
        btnRegisterTool = findViewById(R.id.btnRegisterTool);
    }

    // Helper method to handle the registration of a tool
    private void registerTool() {
        // Get data from EditText fields
        String type = etType.getText().toString();
        String ID = etID.getText().toString();
        String companyID = etCompanyID.getText().toString();
        String engineSize = etEngineSize.getText().toString();
        String manufactureYearStr = etManufactureYear.getText().toString();
        String treatmentHoursStr = etTreatmentHours.getText().toString();
        String version = etVersion.getText().toString();

        // Validate that all fields are filled
        if (areFieldsEmpty(type, ID, companyID, engineSize, manufactureYearStr, treatmentHoursStr, version)) {
            // Show an error message using a Toast
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return; // Exit the method if any field is empty
        }

        // Convert ManufactureYear, HoursForTreatment to integers
        int manufactureYear = Integer.parseInt(manufactureYearStr);
        int treatmentHours = Integer.parseInt(treatmentHoursStr);

        // Create a Map to store the data
        Map<String, Object> toolData = createToolDataMap(type, ID, companyID, engineSize, manufactureYear, treatmentHours, version);

        // Upload data to Firebase
        uploadDataToFirebase(ID, toolData);
    }

    // Helper method to check if any of the provided fields are empty
    private boolean areFieldsEmpty(String... fields) {
        for (String field : fields) {
            if (field.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    // Helper method to create a Map with tool data
    private Map<String, Object> createToolDataMap(String type, String ID, String companyID, String engineSize,
                                                  int manufactureYear, int treatmentHours, String version) {
        Map<String, Object> toolData = new HashMap<>();
        toolData.put("type", type);
        toolData.put("ID", ID);
        toolData.put("company_ID", companyID);
        toolData.put("engine_size", engineSize);
        toolData.put("manufacture_year", manufactureYear);
        toolData.put("treatment_hours", treatmentHours);
        // At the beginning, the hours till treatment is the same as the treatment hours.
        toolData.put("hours_till_treatment", treatmentHours);
        toolData.put("version", version);
        return toolData;
    }

    // Helper method to upload data to Firebase
    private void uploadDataToFirebase(String documentID, Map<String, Object> data) {
        firestore.collection("Vehicles")
                .document(documentID)
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    // Data successfully uploaded
                    // Show a success message
                    Toast.makeText(RegisterToolActivity.this, "Tool registration was successful", Toast.LENGTH_LONG).show();

                    // Finish the activity to go back to the manager homepage
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Error uploading data
                    // Show an error message
                    Toast.makeText(RegisterToolActivity.this, "Tool registration failed", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                });
    }

    // Helper method to set up click listeners for buttons
    private void setupClickListener(int buttonId, Class<?> destinationClass) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> navigateToActivity(destinationClass));
    }

    // Helper method to navigate to another activity and finish the current activity
    private void navigateToActivity(Class<?> destinationClass) {
        Intent intent = new Intent(RegisterToolActivity.this, destinationClass);
        startActivity(intent);
        finish(); // Finish the current activity to prevent going back via backspace button
    }
}
