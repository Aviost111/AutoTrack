package com.example.autotrack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VehicleHistoryActivity extends AppCompatActivity {

    private ListView listViewTreatments;
    private ListView listViewRefuels;

    private FirebaseFirestore db;
    private String vehicleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_history);

        // Get the vehicle ID from the intent
        Intent intent = getIntent();
        vehicleId = intent.getStringExtra("vehicleId");

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Find ListViews in the layout
        listViewTreatments = findViewById(R.id.listViewTreatments);
        listViewRefuels = findViewById(R.id.listViewRefuels);

        // Retrieve and populate treatments and refuels data
        retrieveAndPopulateData("treatments", listViewTreatments);
        retrieveAndPopulateData("refuels", listViewRefuels);
    }

    private void retrieveAndPopulateData(String subcollectionName, ListView listView) {
        // Reference to the "history" subcollection for the specific vehicle
        CollectionReference historySubcollectionRef = db.collection("Vehicles")
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
                            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                                String field = entry.getKey();
                                Object value = entry.getValue();
                                dataList.add(field + ": " + value.toString());
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
