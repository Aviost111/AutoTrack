package com.example.autotrack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class VehicleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);

        // Get references to UI elements
        TextView textViewEmployeeDetails = findViewById(R.id.textViewEmployeeDetails);
        TextView textViewVehicleDetails = findViewById(R.id.textViewVehicleDetails);
        TextView textViewAvailability = findViewById(R.id.textViewAvailability);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnStartStop = findViewById(R.id.btnStartStop);
        Button btnVehicleHistory = findViewById(R.id.btnVehicleHistory);
        Button btnReportCarCare = findViewById(R.id.btnReportCarCare);

        // Retrieve data from Intent
        Intent intent = getIntent();
        String employeeName = "John Doe";  // Replace with actual employee name
        String employeeId = "12345";       // Replace with actual employee ID
        String vehicleId = intent.getStringExtra("vehicleId");  // Replace with the key used in the previous activity

        //////////////////////////////////////////////////////
        // Access a Cloud Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get the reference to the document
        DocumentReference documentReference = db.collection("Vehicles").document(vehicleId);

        // Fetch the document
        documentReference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Document found, now cast it to a Vehicle object
                            Vehicle vehicle = document.toObject(Vehicle.class);
//                            vehicle.setTreatment_hours(document.getLong("Hours_for_treatment").intValue());
//                            vehicle.setHours_till_treatment(document.getLong("Hours_lf_treatment").intValue());
//                            vehicle.setManufacture_year(document.getLong("manufactor_year").intValue());


                            // Use the vehicle object as needed
                            // Set text for availability
                            if(vehicle.getHours_till_treatment() <= 0) {
                                Log.e("error", vehicle.toString());
                                textViewAvailability.setText("Unavailable");
                                textViewAvailability.setTextColor(Color.RED);
                            } else {
                                textViewAvailability.setText("Available");
                                textViewAvailability.setTextColor(Color.GREEN);
                            }


                            // Set text for employee and vehicle details
                            textViewEmployeeDetails.setText("Employee: " + employeeName + " (ID: " + employeeId + ")");
                            textViewVehicleDetails.setText("VehicleType: " + vehicle.getType() + " (ID: " + vehicle.getID() + ")");

                        } else {
                            // Document does not exist
                            Log.d("Firestore", "No such document");
                        }
                    } else {
                        // Handle errors
                        Log.e("Firestore", "Error getting document", task.getException());
                    }
                });
        //////////////////////////////////////////////////////


        // Back button click listener
        Button backButton = findViewById(R.id.btnBack); // Replace with the actual ID of your back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity to go back
                finish();
            }
        });

        // Button click listeners
        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement start/stop logic
                // For example, update availability and perform relevant actions
            }
        });

        btnVehicleHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement logic to navigate to the vehicle history page
            }
        });

        btnReportCarCare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement logic to navigate to the report car care / refueling page
            }
        });

    }
}