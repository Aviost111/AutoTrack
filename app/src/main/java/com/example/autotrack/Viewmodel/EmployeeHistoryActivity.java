package com.example.autotrack.Viewmodel;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.autotrack.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class EmployeeHistoryActivity extends AppCompatActivity {


    private String employeeId;
    private String companyId;

    private Button startStopButton;
    private Button treatmentsButton;
    private Button refuelsButton;

    private ListView listViewStartStop;
    private ListView listViewTreatments;
    private ListView listViewRefuels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_history);

        //find the back button in the layout
        findViewById(R.id.btnBack).setOnClickListener(view -> {
            // Finish the current activity to go back
            finish();
        });

        // Get the vehicle ID from the intent
        Intent intent = getIntent();
        employeeId = intent.getStringExtra("employeeId");
        companyId = intent.getStringExtra("company_uid");
        Log.d("EmployeeHistoryActivity", "companyId: " + companyId);
        Log.d("EmployeeHistoryActivity", "employeeId: " + employeeId);


        // Access a Cloud Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Initialize ListViews
        listViewStartStop = findViewById(R.id.listViewStartStop);
        listViewTreatments = findViewById(R.id.listViewTreatments);
        listViewRefuels = findViewById(R.id.listViewRefuels);

        // Initialize TextViews
        startStopButton = findViewById(R.id.textViewStartStop);
        treatmentsButton = findViewById(R.id.textViewTreatments);
        refuelsButton = findViewById(R.id.textViewRefuels);

        // Set initial visibility to GONE
        listViewStartStop.setVisibility(View.VISIBLE);
        listViewTreatments.setVisibility(View.GONE);
        listViewRefuels.setVisibility(View.GONE);


        // Retrieve and populate treatments, refuels and start-stop data
        retrieveAndPopulateData("start-stop", listViewStartStop);
        retrieveAndPopulateData("treatments", listViewTreatments);
        retrieveAndPopulateData("refuels", listViewRefuels);

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
                .collection("Employees")
                .document(employeeId)
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
                                dataList.add("Date: " + formattedDateTime + " \n" + data);
                                //remove the last \n
                                data.deleteCharAt(data.length() - 1); // TODO check if this is needed
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


