package com.example.autotrack;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreAppData {
    private FirebaseFirestore firestore;
    private CollectionReference managersCollection;
    private CollectionReference employeesCollection;
    private CollectionReference vehiclesCollection;

    public FirestoreAppData() {
        firestore = FirebaseFirestore.getInstance();
        managersCollection = firestore.collection("Managers");
        employeesCollection = firestore.collection("Employees");
        vehiclesCollection = firestore.collection("Vehicles");
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

    // Interface to handle callbacks for reading data
    public interface OnGetDataListener<T> {
        void onSuccess(T data);
        void onFailure(Exception e);
    }
}
