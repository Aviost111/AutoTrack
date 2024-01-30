package com.example.autotrack;
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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EmployeesListActivity extends AppCompatActivity {


private ListView listviewEmployees;

private TextView textName;
private Button btnLogout;

protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_employees_list);

    listviewEmployees = findViewById(R.id.listviewEmployees);

    textName = findViewById(R.id.userName);
    textName.setText("hello");

    Button backButton = findViewById(R.id.btnBack);

    // Get a reference to the Firestore database
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    db.collection("Employees")
            .get()
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
            // Perform any additional actions before finishing the activity if needed
            finish(); // This will close the current activity and go back to the "manager Activity"
        }});
}


    private void updatListView(List<EmployeeObj> employeelist) {

    ArrayAdapter<EmployeeObj> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, employeelist);

        listviewEmployees.setAdapter((adapter));

        //set the item click listener:

        listviewEmployees.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // get the clicked activity_employee_details.xml object
                EmployeeObj clickedEmployeeObj = (EmployeeObj)  parent.getItemAtPosition(position);

                //Access information from clicked EmployeeObj
                String employeeId = clickedEmployeeObj.getEmail();

                //create an intent to start the EmployeeActivity
                Intent intent = new Intent(EmployeesListActivity.this, EmployeeDetailsActivity.class);

                //pass necessary information as extras to the the activity_employee_details.xml activity.
//                intent.putExtra("EmployeeId" , EmployeeId);
                startActivity(intent);            }
        });
    }

}