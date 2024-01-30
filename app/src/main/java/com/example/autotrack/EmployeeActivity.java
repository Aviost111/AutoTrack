package com.example.autotrack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

// EmployeeActivity.java
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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
                        List<Vehicle> vehicleList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("Firestore Data", document.getData().toString());
                            // Convert the document to a Vehicle object
                            Vehicle vehicle = document.toObject(Vehicle.class);

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


    private void updateListView(List<Vehicle> vehicleList) {
        ArrayAdapter<Vehicle> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, vehicleList);

        listViewFactoryVehicles.setAdapter(adapter);

        // Set the item click listener
        listViewFactoryVehicles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the clicked Vehicle object
                Vehicle clickedVehicle = (Vehicle) parent.getItemAtPosition(position);

                // Create an Intent to start the VehicleActivity
                Intent intent = new Intent(EmployeeActivity.this, VehicleActivity.class);

                // Pass necessary information as extras to the VehicleActivity
                intent.putExtra("vehicleId", clickedVehicle.getID());
                intent.putExtra("vehicleType", clickedVehicle.getType());
                intent.putExtra("hoursTillTreatment",clickedVehicle.getHours_till_treatment());

                // Start the VehicleActivity
                startActivity(intent);
            }
        });
    }


}

