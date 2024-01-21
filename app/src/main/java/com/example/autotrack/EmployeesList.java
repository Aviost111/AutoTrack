package com.example.autotrack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import android.os.Bundle;

public class EmployeesList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private employeeAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employees_list);

        // Get a reference to the Firestore database
        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recycler1);
        recyclerView.setHasFixedSize(true);
        // To display the Recycler view linearly
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this));


        // Use a query to get data from Firestore
        CollectionReference query = db.collection("Employees");

        // Configure the adapter options
        FirestoreRecyclerOptions<Employee> options = new FirestoreRecyclerOptions.Builder<Employee>()
                .setQuery(query, Employee.class)
                .build();

        // Create the adapter
        adapter = new employeeAdapter(options);

        // Set the adapter to the RecyclerView
        recyclerView.setAdapter(adapter);
    }

        @Override
        protected void onStart () {
            super.onStart();
            // Start listening for changes in the Firestore database
            adapter.startListening();
        }

        @Override
        protected void onStop () {
            super.onStop();
            // Stop listening when the activity is stopped
            adapter.stopListening();
        }
    }
