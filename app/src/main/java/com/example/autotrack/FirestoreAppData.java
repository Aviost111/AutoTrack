package com.example.autotrack;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.concurrent.CompletableFuture;

public class FirestoreAppData {
    private FirebaseFirestore firestore;
//    private static CollectionReference managersCollection;
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


    public FirestoreAppData() {
        firestore = FirebaseFirestore.getInstance();
        //        get the data of all companies
        companiesCollection = firestore.collection("Companies");

        //       get the current user's companies details
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();
        DocumentReference companyDocument = companiesCollection.document(uid);

        //        get the "employees" and "vehicle" data of this company
        employeesCollection = companyDocument.collection("Employees");
        vehiclesCollection = companyDocument.collection("Vehicles");
    }


    // Write Manager data to Firestore
    // waiting for the manager class
//    public void writeManager(Manager manager) {
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            String uid = currentUser.getUid();
//            DocumentReference managerDocument = managersCollection.document(uid);
//            managerDocument.set(manager);
//        }
//    }
//
//    // Read Manager data from Firestore
//    public void readManager(final OnGetDataListener<Manager> listener) {
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            String uid = currentUser.getUid();
//            DocumentReference managerDocument = managersCollection.document(uid);
//            managerDocument.get().addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    Manager manager = task.getResult().toObject(Manager.class);
//                    if (listener != null) {
//                        listener.onSuccess(manager);
//                    }
//                } else {
//                    if (listener != null) {
//                        listener.onFailure(task.getException());
//                    }
//                }
//            });
//        }
//    }

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
    public void readEmployee(final OnGetDataListener<EmployeeObj> listener) {
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
    public void writeVehicle(Vehicle vehicle) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DocumentReference vehicleDocument = vehiclesCollection.document(uid);
            vehicleDocument.set(vehicle);
        }
    }

    // Read Vehicle data from Firestore
    public void readVehicle(final OnGetDataListener<Vehicle> listener) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DocumentReference vehicleDocument = vehiclesCollection.document(uid);
            vehicleDocument.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Vehicle vehicle = task.getResult().toObject(Vehicle.class);
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


    // Read company data from Firestore and return CompletableFuture with specify company details
    public static CompletableFuture<CompanyObj> returnCompany() {
        CompletableFuture<CompanyObj> future = new CompletableFuture<>();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            if (companiesCollection != null) {
                DocumentReference companyDocument = companiesCollection.document(uid);
                companyDocument.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        CompanyObj company = task.getResult().toObject(CompanyObj.class);
                        future.complete(company); // Complete the future with the result
                    } else {
                        future.completeExceptionally(task.getException()); // Complete the future exceptionally in case of failure
                    }
                });
            }
        } else {
            // Handle the case where collectionRef is null
            return CompletableFuture.completedFuture(null);
             }

        return future;
    }

    // Interface to handle callbacks for reading data
    public interface OnGetDataListener<T> {
        void onSuccess(T data);
        void onFailure(Exception e);
    }
}


