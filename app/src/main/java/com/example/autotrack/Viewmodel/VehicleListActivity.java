package com.example.autotrack.Viewmodel;

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

import com.example.autotrack.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class VehicleListActivity extends AppCompatActivity {

    private ListView listViewFactoryVehicles;
    private String userMail;
    private boolean isManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);
        TextView textViewEmployeeDetails = findViewById(R.id.textViewEmployeeDetails);
        listViewFactoryVehicles = findViewById(R.id.listViewFactoryVehicles);
        Button btnLogout = findViewById(R.id.btnLogout);

        userMail = getIntent().getStringExtra("email");

        // Access a Cloud Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get companyId (=ManagerId) and retrieve it's Vehicles data
        db.collection("Users")
                .document(userMail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Access the document data
                            String companyId = document.getString("company_id");
                            String firstName = document.getString("first_name");
                            String lastName = document.getString("last_name");
                            isManager = document.getBoolean("is_manager");

                            // Set userName
                            textViewEmployeeDetails.setText(firstName + " " + lastName);

                            // Set logout/ back button
                            if(isManager) btnLogout.setText("Back");
                            else btnLogout.setText("Logout");

                            // Read data from the "vehicles" collection
                            db.collection("Companies")
                                    .document(companyId)
                                    .collection("Vehicles")
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            List<VehicleObj> vehicleList = new ArrayList<>();
                                            for (QueryDocumentSnapshot document1 : task1.getResult()) {
                                                // Convert the document to a Vehicle object
                                                VehicleObj vehicle = document1.toObject(VehicleObj.class);

                                                // Add the Vehicle object to the list
                                                vehicleList.add(vehicle);
                                            }
                                            updateListView(vehicleList);
                                        } else {
                                            // Handle errors

                                        }
                                    });
                        } else {
                            Log.d("Firestore", "No such document");
                        }
                    } else {
                        // Handle errors
                        Exception exception = task.getException();
                        if (exception != null) {
                            System.err.println("Error getting documents: " + exception.getMessage());
                        }
                    }
                });


        // Handle logout button click
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isManager){
                    // Finish the current activity to go back
                    finish();
                } else {
                    Intent intent = new Intent(VehicleListActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish(); // Finish the current activity to prevent going back to it from the login page
                }
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

                // Create an Intent to start the VehicleActivity
                Intent intent = new Intent(VehicleListActivity.this, VehicleActivity.class);


                // Pass necessary information as extras to the VehicleActivity
                intent.putExtra("vehicleId", clickedVehicle.getID());
                intent.putExtra("treatment_hours",clickedVehicle.getTreatment_hours());
                intent.putExtra("vehicleType", clickedVehicle.getType());
                intent.putExtra("hoursTillTreatment",clickedVehicle.getHours_till_treatment());
                intent.putExtra("email", userMail);

                // Start the VehicleActivity
                startActivity(intent);
            }
        });
    }


}
