package com.example.autotrack.Viewmodel;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.autotrack.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class RegisterVehicleActivity extends AppCompatActivity {

    // Tag for logging purposes
    private static final String TAG = "RegisterVehicleActivity";

    // Firebase instance
    private FirebaseFirestore firestore;

    // UI elements
    private EditText etType, etEngineSize, etManufactureYear, etTreatmentHours, etVersion, etID;
    private Button btnRegisterTool;

    private String company_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_vehicle);

        // Initialize UI elements
        initializeViews();

        // Set up click listener for the "Back" button
        setupClickListener(R.id.btnBack, CompanyActivity.class);

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance();

        // Get the company ID from the intent extra
        company_uid = getIntent().getStringExtra("company_uid");

        // Set click listener for the "Register Tool" button
        btnRegisterTool.setOnClickListener(view -> registerTool());
    }

    // Helper method to initialize UI elements
    private void initializeViews() {
        etType = findViewById(R.id.etType);
        etID = findViewById(R.id.etID);
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
        String engineSize = etEngineSize.getText().toString();
        String manufactureYearStr = etManufactureYear.getText().toString();
        String treatmentHoursStr = etTreatmentHours.getText().toString();
        String version = etVersion.getText().toString();

        // Validate all input fields
        if (validateInput(type, ID, engineSize, manufactureYearStr, treatmentHoursStr, version)) {
            // Continue with the registration process if all validations pass

            // Convert ManufactureYear, HoursForTreatment to integers
            int manufactureYear = Integer.parseInt(manufactureYearStr);
            double treatmentHours = Double.parseDouble(treatmentHoursStr);

            // Create a Map to store the data
            Map<String, Object> toolData = createToolDataMap(type, ID, engineSize, manufactureYear, treatmentHours, version);

            // Upload data to Firebase
            uploadDataToFirebase(ID, toolData);
        }
    }

    // Helper method to create a Map with tool data
    private Map<String, Object> createToolDataMap(String type, String ID, String engineSize,
                                                  int manufactureYear, double treatmentHours, String version) {
        Map<String, Object> toolData = new HashMap<>();
        toolData.put("type", type);
        toolData.put("ID", ID);
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
        firestore.collection("Companies")
                .document(company_uid).collection("Vehicles")
                .document(documentID)
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    // Data successfully uploaded

                    // Create the "history" subCollection
                    createHistorySubCollection(documentID);

                    // Show a success message
                    Toast.makeText(RegisterVehicleActivity.this, "Vehicle registered successfully", Toast.LENGTH_LONG).show();

                    // Navigate to ManagerActivity
                    navigateToActivity(CompanyActivity.class);
                })
                .addOnFailureListener(e -> {
                    // Error uploading data
                    // Show an error message
                    Toast.makeText(RegisterVehicleActivity.this, "Vehicle registration failed", Toast.LENGTH_SHORT).show();
                });
    }

    // Helper method to set up click listeners for buttons
    private void setupClickListener(int buttonId, Class<?> destinationClass) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> navigateToActivity(destinationClass));
    }

    // Helper method to navigate to another activity and finish the current activity
    private void navigateToActivity(Class<?> destinationClass) {
        Intent intent = new Intent(RegisterVehicleActivity.this, destinationClass);
        intent.putExtra("company_uid", company_uid);
        startActivity(intent);
        finish();
    }

    // Helper method to create a subCollection for "history"
    private void createHistorySubCollection(String documentID) {
        // Create a subCollection reference for "history"
        CollectionReference historySubCollectionRef = firestore.collection("Companies")
                .document(company_uid).collection("Vehicles").document(documentID).collection("history");

        // Create an empty document for "refueling" in the "history" subCollection
        historySubCollectionRef.document("refuels").set(new HashMap<>());

        // Create an empty document for "vehicle_care" in the "history" subCollection
        historySubCollectionRef.document("treatments").set(new HashMap<>());

        // Create an empty document for "start-stop" in the "history" subCollection
        historySubCollectionRef.document("start-stop").set(new HashMap<>());
    }

    // Validate input fields
    private boolean validateInput(String type, String ID, String engineSize, String manufactureYearStr, String treatmentHoursStr, String version) {
        if (InputValidator.areFieldsEmpty(type, ID, engineSize, manufactureYearStr, treatmentHoursStr, version)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!InputValidator.isValidType(type)) {
            Toast.makeText(this, "Invalid type", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!InputValidator.containsOnlyNumbers(ID)) {
            Toast.makeText(this, "ID should contain only numbers", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!InputValidator.isValidEngineSize(engineSize)) {
            Toast.makeText(this, "Invalid engine size", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!InputValidator.isValidVersion(version)) {
            Toast.makeText(this, "Invalid version", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!InputValidator.isValidManufactureYear(manufactureYearStr)) {
            Toast.makeText(this, "Invalid manufacture year", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!InputValidator.isValidTreatmentHours(treatmentHoursStr)) {
            Toast.makeText(this, "Invalid treatment hours", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
