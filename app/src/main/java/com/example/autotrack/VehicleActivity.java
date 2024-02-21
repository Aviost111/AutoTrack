package com.example.autotrack;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class VehicleActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String userMail;
    private String companyId;
    private String firstName;
    private String lastName;
    private boolean isManager;
    private String vehicleId;
    private int treatmentHours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);

        // Get references to UI elements
        TextView textViewEmployeeDetails = findViewById(R.id.textViewEmployeeDetails);
        TextView textViewVehicleDetails = findViewById(R.id.textViewVehicleDetails);
        TextView textViewAvailability = findViewById(R.id.textViewAvailability);
        Button btnBack = findViewById(R.id.btnBack);
        CardView btnStartStop = findViewById(R.id.btnStartStop);
        CardView btnVehicleHistory = findViewById(R.id.btnVehicleHistory);
        CardView btnReportRefuel = findViewById(R.id.btnReportRefuel);
        CardView btnReportTreatment = findViewById(R.id.btnReportTreatment);

        // Retrieve data from Intent
        Intent intent = getIntent();
        vehicleId = intent.getStringExtra("vehicleId");
        String vehicleType = intent.getStringExtra("vehicleType");
        double hoursTillTreatment = intent.getDoubleExtra("hoursTillTreatment", 0);
        treatmentHours = intent.getIntExtra("treatment_hours", 0);
        Toast.makeText(VehicleActivity.this, Integer.toString(treatmentHours), Toast.LENGTH_SHORT).show();


        // Get user mail
        userMail = getIntent().getStringExtra("email");

        // Access a Cloud Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get companyId (=ManagerId)
        db.collection("Users")
                .document(userMail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Access the document data
                            companyId = document.getString("company_id");
                            firstName = document.getString("first_name");
                            lastName = document.getString("last_name");
                            isManager = document.getBoolean("is_manager");
                            // Set text for employee details
                            textViewEmployeeDetails.setText(firstName + " " + lastName);
                        } else {
                            // Document doesn't exist
                            Log.d("Firestore", "No such document");
                        }
                    } else {
                        // Task failed with an exception
                        Log.e("Firestore", "Error getting document: ", task.getException());
                    }

                    TextView textViewStartStop = btnStartStop.findViewById(R.id.textViewStartStop);
                    // See if last action was start or stop and set button string by that
                    db.collection("Companies")
                            .document(companyId).
                            collection("Vehicles")
                            .document(vehicleId)
                            .collection("history")
                            .document("start-stop")
                            .get()
                            .addOnCompleteListener(innerTask -> {
                                if (innerTask.isSuccessful()) {
                                    DocumentSnapshot startStopDocument = innerTask.getResult();

                                    if (startStopDocument.exists()) {
                                        // Get all the entries from the document
                                        Map<String, Object> entries = startStopDocument.getData();

                                        // Find the latest entry by sorting keys in descending order
                                        assert entries != null;
                                        List<String> sortedKeys = new ArrayList<>(entries.keySet());
                                        Collections.sort(sortedKeys, Collections.reverseOrder());

                                        if (!sortedKeys.isEmpty()) {
                                            // Get the key of the latest entry
                                            String latestKey = sortedKeys.get(0);

                                            // Get the map corresponding to the latest key
                                            Map<String, Object> latestEntry = (Map<String, Object>) entries.get(latestKey);


                                            // Check if the latest entry is not null and contains the "action" field
                                            if (latestEntry != null && latestEntry.containsKey("action")) {
                                                // Get the value of the "action" field
                                                String latestAction = (String) latestEntry.get("action");

                                                // Now, we can check if the latest action was "start" or "stop"
                                                if ("start".equals(latestAction)) {
                                                    textViewStartStop.setText("Stop");
                                                } else if ("stop".equals(latestAction)) {
                                                    textViewStartStop.setText("Start");
                                                }
                                            }
                                        } else {
                                            textViewStartStop.setText("Start");
                                        }
                                    }
                                } else {
                                    // Handle errors
                                }
                            });
                });

        // Set text for vehicle details
        textViewVehicleDetails.setText("VehicleType: " + vehicleType + " (ID: " + vehicleId + ")");

        //Set text for availability
        if (hoursTillTreatment <= 0) {
            textViewAvailability.setText("Unavailable");
            textViewAvailability.setTextColor(Color.RED);
        } else {
            textViewAvailability.setText("Available");
            textViewAvailability.setTextColor(ContextCompat.getColor(VehicleActivity.this, R.color.green));
        }


        //////////////////////////////////////////////////////////////////////////////

        // History button click listener
        btnVehicleHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the VehicleActivity
                Intent intent = new Intent(VehicleActivity.this, VehicleHistoryActivity.class);

                // Pass necessary information as extras to the VehicleActivity
                intent.putExtra("vehicleId", vehicleId);
                intent.putExtra("userMail", userMail);

                // Start the VehicleActivity
                startActivity(intent);
            }
        });

        // Back button click listener
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity to go back
                finish();

                // Create an Intent to start the EmployeeActivity and clear the activity stack
                Intent intent = new Intent(VehicleActivity.this, VehicleListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("email", userMail);
                startActivity(intent);
            }
        });

        // Button click listeners
        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            ////////////////
            public void onClick(View v) {
                String now = String.valueOf(System.currentTimeMillis());
                Map<String, Object> startStop = new HashMap<>();
                Map<String, Object> startStopInfo = new HashMap<>();

                TextView textViewStartStop = btnStartStop.findViewById(R.id.textViewStartStop);

                // Check current vehicle status (started or not)
                if (textViewStartStop.getText().equals("Start")) {
                    startStopInfo.put("action", "start");
                    textViewStartStop.setText("Stop");
                } else {
                    startStopInfo.put("action", "stop");
                    textViewStartStop.setText("Start");
                    updateHoursTillTreatment(now);
                }

                // Update data on start-stop
                startStopInfo.put("first_name", firstName);
                startStopInfo.put("last_name", lastName);
                startStopInfo.put("vehicle_id", vehicleId);
                startStop.put(now, startStopInfo);

                // Update start/stop history for Only employee in Employees
                if (!isManager) {
                    String path = "Companies/" + companyId + "/Employees/" + userMail + "/history/start-stop";
                    DocumentReference docRef = db.document(path);
                    docRef.update(startStop).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(VehicleActivity.this, "start/stop saved in employees", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure to save user data
                                    Toast.makeText(VehicleActivity.this, "not saved in employees", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, e.toString());
                                }
                            });
                }

                // Update start/stop history for manager or employee in Vehicles
                String vPath = "Companies/" + companyId + "/Vehicles/" + vehicleId + "/history/start-stop";
                DocumentReference vDocRef = db.document(vPath);
                vDocRef.update(startStop).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(VehicleActivity.this, "start/stop saved in vehicles", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure to save user data
                                Toast.makeText(VehicleActivity.this, "not saved in vehicles", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, e.toString());
                            }
                        });
                ///////////////////
            }
        });

        btnReportRefuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement logic to navigate to the report car care / refueling page
                findViewById(R.id.overlayView).setVisibility(View.VISIBLE);
                refuelWindow();
            }
        });

        btnReportTreatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.overlayView).setVisibility(View.VISIBLE);
                treatmentWindow();
                // Implement logic to navigate to the report car care / refueling page
            }
        });
    }

    //    //avi
    private PopupWindow popupWindow;
    private PopupWindow popupWindowT;

    private void refuelWindow() {
        if (popupWindow == null) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.refuel_window, null);
            Button submitButton = popupView.findViewById(R.id.submit_button);
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = auth.getCurrentUser();
            assert firebaseUser != null;

            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText nameInput = popupView.findViewById(R.id.name_input);
                    String nameString = nameInput.getText().toString();
                    double nameDouble = 0;
                    try {
                        nameDouble = Double.parseDouble(nameString);
                        // Now you can use nameDouble for calculations or other operations
                    } catch (NumberFormatException e) {
                        // Handle the case where the input is not a valid double
                        Log.e(TAG, "Invalid number format: " + e.getMessage());
                        Toast.makeText(VehicleActivity.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                    }
//                    Toast.makeText(VehicleActivity.this, name, Toast.LENGTH_LONG).show();
                    String now = String.valueOf(System.currentTimeMillis());
                    Map<String, Object> fuel = new HashMap<>();
                    Map<String, Object> fuelInfo = new HashMap<>();
                    fuelInfo.put("first_name", firstName);
                    fuelInfo.put("last_name", lastName);
                    fuelInfo.put("refill_amount", nameDouble);
                    fuelInfo.put("vehicle_id", vehicleId);
                    fuel.put(now, fuelInfo);
                    Log.d("VehicleActivity", "Retrieved name: " + nameDouble);
                    String path = "Companies/" + companyId + "/Employees/" + userMail + "/history/refuels";
                    String vPath = "Companies/" + companyId + "/Vehicles/" + vehicleId + "/history/refuels";
                    DocumentReference docRef = db.document(path);
                    DocumentReference vDocRef = db.document(vPath);
                    dismissPopupWindow(); // Close the popup
                    if (!isManager) {
                        docRef.update(fuel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(VehicleActivity.this, "refuel saved in employees", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle failure to save user data
                                        Toast.makeText(VehicleActivity.this, "not saved in employees", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, e.toString());
                                    }
                                });
                    }
                    vDocRef.update(fuel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(VehicleActivity.this, "refuel saved in vehicles", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure to save user data
                                    Toast.makeText(VehicleActivity.this, "not saved in vehicles", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, e.toString());
                                }
                            });

                }
            });


            popupWindow = new PopupWindow(popupView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        }

        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        // Use a suitable anchor view in your layout
    }

    private void treatmentWindow() {
        if (popupWindowT == null) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.treatment_window, null);
            Button submitButton = popupView.findViewById(R.id.treatment_button);
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = auth.getCurrentUser();
            assert firebaseUser != null;

            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissPopupWindow(); // Close the popup
                    String now = String.valueOf(System.currentTimeMillis());
                    Map<String, Object> treatment = new HashMap<>();
                    Map<String, Object> treatmentInfo = new HashMap<>();
                    Map<String, Object> hrsTil = new HashMap<>();
                    hrsTil.put("hours_till_treatment", treatmentHours);
                    treatmentInfo.put("first_name", firstName);
                    treatmentInfo.put("last_name", lastName);
                    treatmentInfo.put("vehicle_id", vehicleId);
                    treatment.put(now, treatmentInfo);
                    String path = "Companies/" + companyId + "/Employees/" + userMail + "/history/treatments";
                    String vPath = "Companies/" + companyId + "/Vehicles/" + vehicleId;

                    DocumentReference vDocRef = db.document(vPath);
                    DocumentReference vHDocRef = db.document(vPath + "/history/treatments");
                    DocumentReference docRef = db.document(path);

// Update hours_till_treatment in the vehicle document
                    vDocRef.update(hrsTil)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> vTask) {
                                    if (vTask.isSuccessful()) {
                                        // Set treatment data in employee history
                                        if (!isManager) {
                                            docRef.update(treatment)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                // All operations are successful
                                                                Toast.makeText(VehicleActivity.this, "Treatment saved", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                // Handle failure to save treatment data
                                                                Toast.makeText(VehicleActivity.this, "Failed to save treatment data", Toast.LENGTH_SHORT).show();
                                                                Log.d(TAG, Objects.requireNonNull(task.getException()).toString());
                                                            }
                                                        }
                                                    });
                                        }
                                        vHDocRef.update(treatment)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            // All operations are successful
                                                            Toast.makeText(VehicleActivity.this, "Treatment saved", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            // Handle failure to save treatment data
                                                            Toast.makeText(VehicleActivity.this, "Failed to save treatment data", Toast.LENGTH_SHORT).show();
                                                            Log.d(TAG, Objects.requireNonNull(task.getException()).toString());
                                                        }
                                                    }
                                                });
                                    } else {
                                        // Handle failure to update hours_till_treatment in vehicle document
                                        Toast.makeText(VehicleActivity.this, "Failed to update hours till treatment", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, Objects.requireNonNull(vTask.getException()).toString());
                                    }
                                }
                            });
                }
            });

            popupWindowT = new PopupWindow(popupView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
            popupWindowT.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        popupWindowT.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        // Use a suitable anchor view in your layout
    }


    private void dismissPopupWindow() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
        if (popupWindowT != null) {
            popupWindowT.dismiss();
        }
        findViewById(R.id.overlayView).setVisibility(View.INVISIBLE);
    }
//    //avi

    private void updateHoursTillTreatment(String now) {
        //parse "now" string timestamp into long
        Long nowLong = Long.parseLong(now);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Companies")
                .document(companyId).
                collection("Vehicles")
                .document(getIntent().getStringExtra("vehicleId"))
                .collection("history")
                .document("start-stop")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot startStopDocument = task.getResult();

                        if (startStopDocument.exists()) {
                            // Get all the entries from the document
                            Map<String, Object> entries = startStopDocument.getData();

                            // Find the latest entry by sorting keys in descending order
                            List<String> sortedKeys = new ArrayList<>(entries.keySet());
                            Collections.sort(sortedKeys, Collections.reverseOrder());

                            if (!sortedKeys.isEmpty()) {
                                // Get the key of the latest entry
                                String latestKey = sortedKeys.get(1);
                                Long latestKeyLong = Long.parseLong(latestKey);

                                double deltaHours = ((double) (nowLong - latestKeyLong)) / (60 * 60 * 1000);

                                //update hours till treatment
                                DocumentReference docRef = db.collection("Companies")
                                        .document(companyId).
                                        collection("Vehicles")
                                        .document(getIntent().getStringExtra("vehicleId"));

                                docRef.get().addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        // Retrieve the current value
                                        double currentValue = documentSnapshot.getDouble("hours_till_treatment");
                                        // Perform the subtraction
                                        double newValue = currentValue - deltaHours;

                                        Map<String, Object> updates = new HashMap<>();
                                        updates.put("hours_till_treatment", newValue);
                                        docRef.update(updates).addOnSuccessListener(aVoid -> {
                                            // Update successful
                                            // Handle success if needed
                                            Log.d("FirestoreUpdate", "hours_till_treatment updated successfully");

                                            // For example, notify the user or perform another action
                                            Toast.makeText(VehicleActivity.this, "hours_till_treatment updated successfully", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                });
                            }
                        }
                    }
                });

    }
}