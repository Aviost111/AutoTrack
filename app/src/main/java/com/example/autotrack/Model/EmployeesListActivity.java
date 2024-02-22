package com.example.autotrack.Model;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.autotrack.Controller.FirestoreAppData;
import com.example.autotrack.Controller.FirestoreController;
import com.example.autotrack.R;
import com.google.firebase.firestore.CollectionReference;
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
    FirestoreController.handleCompanyUid(companyId);

    CompletableFuture<CompanyObj> managerFuture = FirestoreController.returnCompany(companyId);

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
    new FirestoreController(EmployeesListActivity.this);
    CollectionReference emp  = FirestoreController.getEmployeesCollection();
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

}