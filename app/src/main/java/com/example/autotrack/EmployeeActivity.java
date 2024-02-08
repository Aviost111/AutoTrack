package com.example.autotrack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

// EmployeeActivity.java
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EmployeeActivity extends AppCompatActivity {

    private TextView textViewEmployeeDetails;
    private ListView listViewFactoryVehicles;
    private Button btnLogout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        textViewEmployeeDetails = findViewById(R.id.textViewEmployeeDetails);
        listViewFactoryVehicles = findViewById(R.id.listViewFactoryVehicles);
        btnLogout = findViewById(R.id.btnLogout);

        // Access a Cloud Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Read data from the "vehicles" collection
        db.collection("Vehicles")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("HHHHHHHHHHHHHHH", "hekkooko");
                        List<VehicleObj> vehicleList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("Firestore Data", document.getData().toString());
                            // Convert the document to a Vehicle object
                            VehicleObj vehicle = document.toObject(VehicleObj.class);

//                            // Set longs (firebase numeric values)
//                            vehicle.setTreatment_hours(document.getLong("Hours_for_treatment").intValue());
//                            vehicle.setHours_till_treatment(document.getLong("Hours_lf_treatment").intValue());
//                            vehicle.setManufacture_year(document.getLong("manufactor_year").intValue());


                            // Add the Vehicle object to the list
                            vehicleList.add(vehicle);
                        }
                        updateListView(vehicleList);
                    } else {
                        // Handle errors

                    }
                });


        // Handle logout button click
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmployeeActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish(); // Finish the current activity to prevent going back to it from the login page
            }
        });
    }


    private void updateListView(List<VehicleObj> vehicleList) {
        ArrayAdapter<VehicleObj> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, vehicleList);

        listViewFactoryVehicles.setAdapter(adapter);

        // Set the item click listener
        listViewFactoryVehicles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the clicked Vehicle object
                VehicleObj clickedVehicle = (VehicleObj) parent.getItemAtPosition(position);

                // Access information from the clicked Vehicle
                String vehicleId = clickedVehicle.getID();

                // Create an Intent to start the VehicleActivity
                Intent intent = new Intent(EmployeeActivity.this, VehicleActivity.class);

                // Pass necessary information as extras to the VehicleActivity
                intent.putExtra("vehicleId", vehicleId);

                // Start the VehicleActivity
                startActivity(intent);
            }
        });
    }


}

