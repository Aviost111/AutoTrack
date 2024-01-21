package com.example.autotrack;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;


public class employeeAdapter extends FirestoreRecyclerAdapter<
        Employee, employeeAdapter.employeesViewHolder> {

    public employeeAdapter(

            @NonNull FirestoreRecyclerOptions<Employee> options)
    {
        super(options);
    }


    @Override
    protected void
    onBindViewHolder(@NonNull employeesViewHolder holder,
                     int position, @NonNull Employee model)
    {
        Log.d("EmployeeAdapter", "onBindViewHolder: " + model.getFirstname());

        try{

        // Add firstname from model class (here
        // "person.class")to appropriate view in Card
        // view (here "person.xml")
        holder.firstname.setText(model.getFirstname());

        // Add lastname from model class (here
        // "person.class")to appropriate view in Card
        // view (here "person.xml")
        holder.lastname.setText(model.getLastname());

        // Add phone from model class (here
        // "person.class")to appropriate view in Card
        // view (here "person.xml")
        holder.phone.setText(model.getPhone());

        // Add company_ID from model class (here
        // "person.class")to appropriate view in Card
        // view (here "person.xml")
        holder.company_ID.setText(model.getCompany_ID());

        // Add manager_ID from model class (here
        // "person.class")to appropriate view in Card
        // view (here "person.xml")
        holder.manager_ID.setText(model.getManager_ID());
    }
    catch (Exception e) {
        Log.e("EmployeeAdapter", "Error onBindViewHolder", e);
    }
    }

    // Function to tell the class about the Card view (here
    // "person.xml")in
    // which the data will be shown
    @NonNull
    @Override
    public employeesViewHolder
    onCreateViewHolder(@NonNull ViewGroup parent,
                       int viewType)
    {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.employee, parent, false);
        return new employeeAdapter.employeesViewHolder(view);
    }


    // Sub Class to create references of the views in Crad
    // view (here "person.xml")
    class employeesViewHolder
            extends RecyclerView.ViewHolder {
        TextView firstname, lastname, phone, company_ID, manager_ID;
        public employeesViewHolder(@NonNull View itemView)
        {
            super(itemView);

            firstname = itemView.findViewById(R.id.firstname);
            lastname = itemView.findViewById(R.id.lastname);
            phone = itemView.findViewById(R.id.phone);
            company_ID = itemView.findViewById(R.id.company_ID);
            manager_ID = itemView.findViewById(R.id.manager_ID);
        }
    }
}

