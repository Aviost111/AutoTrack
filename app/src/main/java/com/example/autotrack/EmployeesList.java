package com.example.autotrack;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.common.subtyping.qual.Bottom;

import java.util.ArrayList;
import java.util.List;

public class EmployeesList extends AppCompatActivity {

private TextView textViewEmployeeList;
private ListView listviewEmployees;
private Button btnLogout;

protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_employees_list);

    textViewEmployeeList = findViewById(R.id.textViewEmployeeList);
    listviewEmployees = findViewById(R.id.listviewEmployees);
    btnLogout = findViewById(R.id.btnLogout);

    // Get a reference to the Firestore database
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    db.collection("Employees")
            .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Employee> employeelist = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String documentID = document.getId();

                            //convert document to Employee class
                            Employee employee = document.toObject(Employee.class);

                            //setters for this employee
                            employee.setEmail(document.get("email").toString());
                            employee.setFirstname(document.get("first_name").toString());
                            employee.setLastname(document.get("last_name").toString());
                            employee.setPhone(document.getLong("phone").toString());
                            employee.setCompany_ID(document.get("company_ID").toString());
                            employee.setManager_ID(document.get("manager_ID").toString());

                            //add employee object to list
                            employeelist.add(employee);
                        }
                    updatListView(employeelist);
                    }
                });

}

    private void updatListView(List<Employee> employeelist) {
        ArrayAdapter<Employee> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, employeelist);

        listviewEmployees.setAdapter((adapter));

        //set the item click listener:

        listviewEmployees.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // get the clicked employee object
                Employee clickedEmployee = (Employee)  parent.getItemAtPosition(position);

                //Access information from clicked Employee
                String employeeId = clickedEmployee.getEmail();

                //create an intent to start the EmployeeActivity
                Intent intent = new Intent(EmployeesList.this , Employee.class);

                //pass necessary information as extras to the the employee activity.
//                intent.putExtra("EmployeeId" , EmployeeId);
                startActivity(intent);            }
        });
    }

}