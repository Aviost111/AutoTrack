package com.example.autotrack.Model;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.autotrack.R;
import com.example.autotrack.controler.FirestoreAppData;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EmployeesListActivity extends AppCompatActivity {


private ListView listviewEmployees;
private String companyId;
private TextView textName;
private Button btnLogout;

protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_employees_list);

    listviewEmployees = findViewById(R.id.listviewEmployees);

    textName = findViewById(R.id.userName);

    // Get the company's ID for the manager name.
    companyId = getIntent().getStringExtra("company_uid");
    // send this company_uid also to the firestoreAppData class:
    FirestoreAppData.handleCompanyUid(companyId);

    CompletableFuture<CompanyObj> managerFuture = FirestoreAppData.returnCompany(companyId);

    // Set the text of textName when the manager data is retrieved
    managerFuture.thenAccept(company -> {
        Log.d(TAG, "CompanyObj received: " + company);
        if (company != null) {
            // Use the company name to set the text
            String companyName = company.getName();
            runOnUiThread(() -> textName.setText(companyName));
        } else {
            Log.e(TAG, "CompanyObj is null.");
            // Handle the null case appropriately
        }
    }).exceptionally(e -> {
        // Handle exceptions if any
        e.printStackTrace();
        Log.e(TAG, "Exception while fetching and updating company data: " + e.getMessage());
        return null;
    });

    Button backButton = findViewById(R.id.btnBack);

    // Get a reference to the Firestore database
    new FirestoreAppData();
    CollectionReference emp  = FirestoreAppData.getEmployeesCollection();
    emp.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<EmployeeObj> employeelist = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("Blalala", document.getData().toString());
                            String documentID = document.getId();

                            //convert document to EmployeeObj class
                            EmployeeObj employeeObj = document.toObject(EmployeeObj.class);

                            //add employeeObj object to list
                            employeelist.add(employeeObj);
                        }
                    updatListView(employeelist);
                    }

                });
    backButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
                    // Finish the current activity to go back
                    finish();
        }});
}


    private void updatListView(List<EmployeeObj> employeelist) {

    ArrayAdapter<EmployeeObj> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, employeelist);

        listviewEmployees.setAdapter((adapter));

        //set the item click listener:
        listviewEmployees.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // get the clicked activity_employee_history.xml object
                EmployeeObj clickedEmployeeObj = (EmployeeObj)  parent.getItemAtPosition(position);

                //Access information from clicked EmployeeObj
                String employeeId = clickedEmployeeObj.getEmail();
                Log.d("EmployeeListActivity", "employeeId: " + employeeId);
                //create an intent to start the EmployeeActivity
                Intent intent = new Intent(EmployeesListActivity.this, EmployeeHistoryActivity.class);

//                pass necessary information as extras to the the activity_employee_details.xml activity.
                intent.putExtra("employeeId" , employeeId);
                intent.putExtra("company_uid", companyId);

                startActivity(intent);            }
        });
    }

    public static class CompanyActivity extends AppCompatActivity {

        private FirebaseFirestore db;
        private TextView tvProfileInfo;
        private String company_uid;

        @SuppressLint("UseSupportActionBar")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_company);

            // Set up click listeners
            setupClickListeners();

            // Initialize Firebase Authentication and Firestore
            db = FirebaseFirestore.getInstance();

            // Initialize UI element for displaying welcome message
            tvProfileInfo = findViewById(R.id.tvProfileInfo);

            // Get the company ID from the intent extra
            company_uid = getIntent().getStringExtra("company_uid");

            if (company_uid != null) {
                retrieveCompanyInfo(company_uid);
            } else {
                // Pop an error message using toast and go back to the login screen
                Toast.makeText(this, "Error sign in company ", Toast.LENGTH_SHORT).show();
                navigateToActivity(LoginActivity.class);
            }
        }

        private void setupClickListeners() {
            setupClickListener(R.id.btnRegisterTool, RegisterVehicleActivity.class);
            setupClickListener(R.id.btnRegisterEmployee, RegisterEmployeeActivity.class);
            setupClickListener(R.id.btnToolsList, VehicleListActivity.class);
            setupClickListener(R.id.btnEmployeesList, EmployeesListActivity.class);

            // Set up click listener for the "Logout" button and navigate to the login activity + finish the current activity
            findViewById(R.id.btnLogout).setOnClickListener(v -> {
                Intent intent = new Intent(CompanyActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            });

            // Add click listener for the "Delete Employee" button
            Button btnDeleteEmployee = findViewById(R.id.btnDeleteEmployee);
            btnDeleteEmployee.setOnClickListener(v -> showDeleteDialog("Employees"));

            // Add click listener for the "Delete Vehicle" button
            Button btnDeleteVehicle = findViewById(R.id.btnDeleteVehicle);
            btnDeleteVehicle.setOnClickListener(v -> showDeleteDialog("Vehicles"));

        }

        private void retrieveCompanyInfo(String uid) {
            db.collection("Companies")
                    .document(uid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Document found, retrieve first and last name
                                String firstName = document.getString("first_name");
                                String lastName = document.getString("last_name");
                                displayManagerInfo(firstName, lastName);
                            } else {
                                // Document does not exist
                                // Display an error message
                                Toast.makeText(this, "Manager not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Task failed with an exception
                            handleTaskFailure(task.getException());
                        }
                    });
        }

        private void displayManagerInfo(String firstName, String lastName) {
            String welcomeMessage = "Welcome, " + firstName + " " + lastName;
            tvProfileInfo.setText(welcomeMessage);
        }

        private void handleTaskFailure(Exception exception) {
            // Display an error message
            Toast.makeText(this, "Task failed with exception: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // Helper method to set up click listeners for buttons
        private void setupClickListener(int buttonId, Class<?> destinationClass) {
            Button button = findViewById(buttonId);
            button.setOnClickListener(v -> navigateToActivity(destinationClass));
        }

        // Helper method to navigate to another activity and finish the current activity
        private void navigateToActivity(Class<?> destinationClass) {
            Intent intent = new Intent(CompanyActivity.this, destinationClass);
            intent.putExtra("company_uid", company_uid);

            //get the email of the current company using company_uid and pass it to the next activity
            DocumentReference companyRef = db.document("Companies/" + company_uid);

            companyRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String email = documentSnapshot.getString("email");
                        intent.putExtra("email", email);
                        startActivity(intent);
                    })
                    .addOnFailureListener(this::handleTaskFailure);
        }

        // Method to show the delete dialog
        private void showDeleteDialog(String type) {
            DeleteDialogHelper.showDialog(this, db, company_uid, type);
        }
    }
}