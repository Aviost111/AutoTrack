package com.example.autotrack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class VehicleHistoryActivity extends AppCompatActivity {
    private String vehicleId;
    private String userMail;
    private String companyId;


    private ListView listViewStartStop;
    private ListView listViewTreatments;
    private ListView listViewRefuels;

    private Button startStopButton;
    private Button treatmentsButton;
    private Button refuelsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_history);

        // Get the vehicle ID from the intent
        Intent intent = getIntent();
        vehicleId = intent.getStringExtra("vehicleId");
        userMail = intent.getStringExtra("userMail");

        // Access a Cloud Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Find ListViews in the layout
        listViewStartStop = findViewById(R.id.listViewStartStop);
        listViewTreatments = findViewById(R.id.listViewTreatments);
        listViewRefuels = findViewById(R.id.listViewRefuels);

        // Initialize Buttons
        startStopButton = findViewById(R.id.StartStopButton);
        treatmentsButton = findViewById(R.id.TreatmentButton);
        refuelsButton = findViewById(R.id.RefuelsButton);

        // Set initial visibility to GONE
        listViewStartStop.setVisibility(View.VISIBLE);
        listViewTreatments.setVisibility(View.GONE);
        listViewRefuels.setVisibility(View.GONE);

        Button backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(view -> {
            // Finish the current activity to go back
            finish();
        });


        // Get companyId (=ManagerId)
        db.collection("Users")
                .document(userMail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Access the document data
                            companyId = document.getString("company_id");

                            // Retrieve and populate treatments, refuels and start-stop data
                            retrieveAndPopulateData("start-stop", listViewStartStop);
                            retrieveAndPopulateData("treatments", listViewTreatments);
                            retrieveAndPopulateData("refuels", listViewRefuels);
                        } else {
                            // Document doesn't exist
                            Log.d("Firestore", "No such document");
                        }
                    } else {
                        // Task failed with an exception
                        Log.e("Firestore", "Error getting document: ", task.getException());
                    }
                });
    }
    // Click handler for Start-Stop TextView
    public void onStartStopClicked(View view) {
        resetButtonColors(); // Reset all button colors
        startStopButton.setBackgroundColor(ContextCompat.getColor(this, R.color.red)); // Change background color
        startStopButton.setTextColor(Color.WHITE); // Change text color
        toggleListViewVisibility(listViewStartStop);

    }

    // Click handler for Treatments TextView
    public void onTreatmentsClicked(View view) {
        resetButtonColors(); // Reset all button colors
        treatmentsButton.setBackgroundColor(ContextCompat.getColor(this, R.color.red)); // Change background color
        treatmentsButton.setTextColor(Color.WHITE); // Change text color
        toggleListViewVisibility(listViewTreatments);
    }

    // Click handler for Refuels TextView
    public void onRefuelsClicked(View view) {
        resetButtonColors(); // Reset all button colors
        refuelsButton.setBackgroundColor(ContextCompat.getColor(this, R.color.red)); // Change background color
        refuelsButton.setTextColor(Color.WHITE); // Change text color
        toggleListViewVisibility(listViewRefuels);
    }

    // Method to reset all button colors to default
    private void resetButtonColors() {
        startStopButton.setBackgroundColor(Color.WHITE);
        startStopButton.setTextColor(Color.RED);
        treatmentsButton.setBackgroundColor(Color.WHITE);
        treatmentsButton.setTextColor(Color.RED);
        refuelsButton.setBackgroundColor(Color.WHITE);
        refuelsButton.setTextColor(Color.RED);
    }

    private void toggleListViewVisibility(ListView listView) {
        //set all listViews to GONE except the one that was clicked
        if (listView != listViewStartStop) {
            listViewStartStop.setVisibility(View.GONE);
        }
        if (listView != listViewTreatments) {
            listViewTreatments.setVisibility(View.GONE);
        }
        if (listView != listViewRefuels) {
            listViewRefuels.setVisibility(View.GONE);
        }
        listView.setVisibility(View.VISIBLE);

    }

    private void retrieveAndPopulateData(String subcollectionName, ListView listView) {
        // Access a Cloud Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to the "history" subcollection for the specific vehicle
        CollectionReference historySubcollectionRef = db.collection("Companies")
                .document(companyId)
                .collection("Vehicles")
                .document(vehicleId)
                .collection("history");

        // Query to get all documents in the subcollection (either "treatments" or "refuels")
        historySubcollectionRef.document(subcollectionName).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Retrieve fields from the document and add them to the list
                            Map<String, Object> dataMap = document.getData();
                            List<String> dataList = new ArrayList<>();

                            // StringBuilder to construct the result string
                            StringBuilder data = new StringBuilder();

                            // Iterate over the entries of the top-level map
                            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                                String key = entry.getKey();

                                // Parse the epoch timestamp (key) string to a long integer
                                long epoch = Long.parseLong(key);

                                // Create a Date object from the epoch time
                                Date date = new Date(epoch);

                                // Define the date and hour format
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                // Format the Date object to a string in the desired format
                                String formattedDateTime = dateFormat.format(date);

                                Map<String, Object> innerMap = (Map<String, Object>) entry.getValue();

                                // Iterate over the entries of the inner map
                                for (Map.Entry<String, Object> innerEntry : innerMap.entrySet()) {
                                    // Append the field and value with appropriate formatting
                                    data.append(innerEntry.getKey().substring(0, 1).toUpperCase() + innerEntry.getKey().substring(1).replace("_", " ")).append(": ").append(innerEntry.getValue()).append("\n");
                                }

                                dataList.add("Date: " + formattedDateTime + "\n" + data);
                                // Clear the StringBuilder for the next iteration
                                data.setLength(0);
                            }

                            // Populate the ListView with the retrieved data
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                    android.R.layout.simple_list_item_1, dataList);
                            listView.setAdapter(adapter);
                        } else {
                            // Handle the case when the document doesn't exist
                            Toast.makeText(this, "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle errors
                        Toast.makeText(this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
