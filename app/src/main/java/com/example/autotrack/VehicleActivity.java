package com.example.autotrack;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VehicleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        // Get references to UI elements
        TextView textViewEmployeeDetails = findViewById(R.id.textViewEmployeeDetails);
        TextView textViewVehicleDetails = findViewById(R.id.textViewVehicleDetails);
        TextView textViewAvailability = findViewById(R.id.textViewAvailability);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnStartStop = findViewById(R.id.btnStartStop);
        Button btnVehicleHistory = findViewById(R.id.btnVehicleHistory);
        Button btnReportRefuel = findViewById(R.id.btnReportRefuel);
        Button btnReportTreatment = findViewById(R.id.btnReportTreatment);

        // Retrieve data from Intent
        Intent intent = getIntent();

        //
        String employeeName = "Avi Ostroff";  // Replace with actual employee name
        String employeeId = "12345";       // Replace with actual employee ID
        String vehicleId = intent.getStringExtra("vehicleId");
        String vehicleType = intent.getStringExtra("vehicleType");
        double hoursTillTreatment = intent.getDoubleExtra("hoursTillTreatment", 0);

        // Set text for employee and vehicle details
        textViewEmployeeDetails.setText("Employee: " + employeeName + " (ID: " + employeeId + ")");
        textViewVehicleDetails.setText("VehicleType: " + vehicleType + " (ID: " + vehicleId + ")");

        //Set text for availability
        if(hoursTillTreatment <= 0) {
            textViewAvailability.setText("Unavailable");
            textViewAvailability.setTextColor(Color.RED);
        } else {
            textViewAvailability.setText("Available");
            textViewAvailability.setTextColor(Color.GREEN);
        }

        //Set start-stop button text-
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("Vehicles")
                .document(vehicleId)
                .collection("history")
                .document("start-stop")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot startStopDocument = task.getResult();

                        if (startStopDocument.exists()) {
                            // Get all the entries from the document
                            Map<String, Object> entries = startStopDocument.getData();

                            // Find the latest entry by sorting keys in descending order
                            List<String> sortedKeys = new ArrayList<>(entries.keySet());
                            Collections.sort(sortedKeys, Collections.reverseOrder());

                            if (!sortedKeys.isEmpty()) {
                                // Get the key of the latest entry
                                String latestKey = sortedKeys.get(0);

                                // Get the action field from the latest entry
                                String latestAction = (String) entries.get(latestKey);


                                // Now, you can check if the latest action was "start" or "stop"
                                if ("start".equals(latestAction)) {
                                    btnStartStop.setText("Stop");
                                } else if ("stop".equals(latestAction)) {
                                    btnStartStop.setText("Start");
                                }
                            } else {btnStartStop.setText("Start");}
                        }
                    } else {
                        // Handle errors
                    }
                });
        //////////////////////////////////////////////////////////////////////////////

        // History button click listener
        btnVehicleHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the VehicleActivity
                Intent intent = new Intent(VehicleActivity.this, VehicleHistoryActivity.class);

                // Pass necessary information as extras to the VehicleActivity
                intent.putExtra("vehicleId", vehicleId);

                // Start the VehicleActivity
                startActivity(intent);            }
        });

        // Back button click listener
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity to go back
                finish();

                // Create an Intent to start the EmployeeActivity and clear the activity stack
                Intent intent = new Intent(VehicleActivity.this, EmployeeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        // Button click listeners
        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String now = String.valueOf(System.currentTimeMillis());
                Map<String, Object> startStopData = new HashMap<>();

                // Check current vehicle status (started or not)
                if (btnStartStop.getText().equals("Start")) {
                    startStopData.put(now, "start");
                    btnStartStop.setText("Stop");
                }else{
                    startStopData.put(now, "stop");
                    btnStartStop.setText("Start");
                    updateHoursTillTreatment(now);
                }

                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("Vehicles")
                        .document(vehicleId)
                        .collection("history")
                        .document("start-stop")
                        .update(startStopData)
                        .addOnSuccessListener(aVoid -> {
                            // Data successfully uploaded
                            // Show a success message
                            Toast.makeText(VehicleActivity.this, "Vehicle status changed", Toast.LENGTH_LONG).show();
                        });
            }
        });

        btnReportRefuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement logic to navigate to the report car care / refueling page
            }
        });

        btnReportTreatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement logic to navigate to the report car care / refueling page
            }
        });
    }

    private void updateHoursTillTreatment(String now){
        //parse "now" sting timestamp into long
        Long nowLong = Long.parseLong(now);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("Vehicles")
                .document(getIntent().getStringExtra("vehicleId"))
                .collection("history")
                .document("start-stop")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot startStopDocument = task.getResult();

                        if (startStopDocument.exists()) {
                            // Get all the entries from the document
                            Map<String, Object> entries = startStopDocument.getData();

                            // Find the latest entry by sorting keys in descending order
                            List<String> sortedKeys = new ArrayList<>(entries.keySet());
                            Collections.sort(sortedKeys, Collections.reverseOrder());

                            if (!sortedKeys.isEmpty()) {
                                // Get the key of the latest entry
                                String latestKey = sortedKeys.get(1);
                                Long latestKeyLong = Long.parseLong(latestKey);

                                double deltaHours = ((double) (nowLong-latestKeyLong)) / (60 * 60 * 1000);

                                //update hours till treatment
                                DocumentReference docRef = firestore.collection("Vehicles")
                                        .document(getIntent().getStringExtra("vehicleId"));

                                docRef.get().addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        // Retrieve the current value
                                        double currentValue = documentSnapshot.getDouble("hours_till_treatment");
                                        // Perform the subtraction
                                        double newValue = currentValue - deltaHours;

                                        Map<String, Object> updates = new HashMap<>();
                                        updates.put("hours_till_treatment", newValue);
                                        docRef.update(updates).addOnSuccessListener(aVoid -> {
                                            // Update successful
                                            // Handle success if needed
                                            Log.d("FirestoreUpdate", "hours_till_treatment updated successfully");

                                            // For example, notify the user or perform another action
                                            Toast.makeText(VehicleActivity.this, "hours_till_treatment updated successfully", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                });
                            }
                        }
                    }
                });

    }
}