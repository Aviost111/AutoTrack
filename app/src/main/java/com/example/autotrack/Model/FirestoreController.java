package com.example.autotrack.Model;

import android.content.Context;
import android.widget.Toast;

import com.example.autotrack.Viewmodel.CompanyObj;
import com.example.autotrack.Viewmodel.EmployeeObj;
import com.example.autotrack.Viewmodel.VehicleObj;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class FirestoreController {
    final private FirebaseFirestore firestore;
    private Context context;
    private static String current_company_id;
    private static CollectionReference employeesCollection;
    private static CollectionReference vehiclesCollection;
    private static CollectionReference companiesCollection;

    public static CollectionReference getEmployeesCollection() {
        return employeesCollection;
    }

    public CollectionReference getVehiclesCollection() {
        return vehiclesCollection;
    }

    public static CollectionReference getCompaniesCollection() {
        return companiesCollection;
    }

    public static void handleCompanyUid(String companyUid) {
        current_company_id = companyUid;
    }

    public FirestoreController(Context context) {
        this.firestore = FirebaseFirestore.getInstance();
        this.context = context;
        companiesCollection = firestore.collection("Companies");
        DocumentReference companyDocument = companiesCollection.document(current_company_id);
        //        get the "employees" and "vehicle" data of this company
        employeesCollection = companyDocument.collection("Employees");
        vehiclesCollection = companyDocument.collection("Vehicles");
    }

    public void saveCompanyData(CompanyObj newCompany, FirebaseUser firebaseUser) {
        // Create a user data map for the company
        Map<String, Object> company = new HashMap<>();
        company.put("email", newCompany.getEmail());
        company.put("first_name", newCompany.getFirst_name());
        company.put("last_name", newCompany.getLast_name());
        company.put("phone", newCompany.getPhone());
        company.put("company_id", firebaseUser.getUid());

        // Save company data to Firestore
        firestore.collection("Companies").document(firebaseUser.getUid()).set(company)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Company data saved", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    // Handle failure to save company data
                    Toast.makeText(context, "Error in company data", Toast.LENGTH_SHORT).show();
                });
    }

    public void saveUserData(CompanyObj newCompany, FirebaseUser firebaseUser) {
        // Create a user data map for the user
        Map<String, Object> users = new HashMap<>();
        users.put("company_id", firebaseUser.getUid());
        users.put("first_name", newCompany.getFirst_name());
        users.put("last_name", newCompany.getLast_name());
        users.put("is_manager", true);

        // Save user data to Firestore
        firestore.collection("Users").document(Objects.requireNonNull(firebaseUser.getEmail())).set(users)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "User data saved", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    // Handle failure to save user data
                    Toast.makeText(context, "Error in user data", Toast.LENGTH_SHORT).show();
                });
    }

    // Write Employee data to Firestore
    public void writeEmployee(EmployeeObj employee) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DocumentReference employeeDocument = employeesCollection.document(uid);
            employeeDocument.set(employee);
        }
    }

    // Read Employee data from Firestore
    public void readEmployee(final FirestoreAppData.OnGetDataListener<EmployeeObj> listener) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DocumentReference employeeDocument = employeesCollection.document(uid);
            employeeDocument.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    EmployeeObj employee = task.getResult().toObject(EmployeeObj.class);
                    if (listener != null) {
                        listener.onSuccess(employee);
                    }
                } else {
                    if (listener != null) {
                        listener.onFailure(task.getException());
                    }
                }
            });
        }
    }



    // Write Vehicle data to Firestore
    public void writeVehicle(VehicleObj vehicle) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DocumentReference vehicleDocument = vehiclesCollection.document(uid);
            vehicleDocument.set(vehicle);
        }
    }

    // Read Vehicle data from Firestore
    public void readVehicle(final FirestoreAppData.OnGetDataListener<VehicleObj> listener) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DocumentReference vehicleDocument = vehiclesCollection.document(uid);
            vehicleDocument.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    VehicleObj vehicle = task.getResult().toObject(VehicleObj.class);
                    if (listener != null) {
                        listener.onSuccess(vehicle);
                    }
                } else {
                    if (listener != null) {
                        listener.onFailure(task.getException());
                    }
                }
            });
        }
    }


    // add a function which return an object to use his functions...

    // Read Employee data from Firestore and return CompletableFuture with Employee
    public static CompletableFuture<EmployeeObj> returnEmployee() {
        CompletableFuture<EmployeeObj> future = new CompletableFuture<>();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            if (employeesCollection != null) {
                DocumentReference employeeDocument = employeesCollection.document(uid);
                employeeDocument.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        EmployeeObj manager = task.getResult().toObject(EmployeeObj.class);
                        future.complete(manager); // Complete the future with the result
                    } else {
                        future.completeExceptionally(task.getException()); // Complete the future exceptionally in case of failure
                    }
                });
            } }else {
            // Handle the case where collectionRef is null
            return CompletableFuture.completedFuture(null);
//                future.completeExceptionally(new IllegalStateException("User not authenticated"));
        }


        return future;
    }


    // Read company data from Firestore and return CompletableFuture with specified company details
    public static CompletableFuture<CompanyObj> returnCompany(String company_id) {
        CompletableFuture<CompanyObj> future = new CompletableFuture<>();

        // Access a Cloud Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (company_id != null) {
            // Get companyId (=ManagerId)
            DocumentReference companyDocument = db.collection("Companies").document(company_id);
            companyDocument.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    CompanyObj company = task.getResult().toObject(CompanyObj.class);
                    future.complete(company); // Complete the future with the result
                } else {
                    future.completeExceptionally(task.getException()); // Complete the future exceptionally in case of failure
                }
            });
        } else {
            // Handle the case where company_id is null
            future.completeExceptionally(new IllegalArgumentException("Company ID is null"));
        }

        return future;
    }




    // Interface to handle callbacks for reading data
    public interface OnGetDataListener<T> {
        void onSuccess(T data);
        void onFailure(Exception e);
    }
}