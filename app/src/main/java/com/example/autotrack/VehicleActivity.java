package com.example.autotrack;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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


public class VehicleActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String userMail;
    private String companyId;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);

        // Get references to UI elements
        TextView textViewEmployeeDetails = findViewById(R.id.textViewEmployeeDetails);
        TextView textViewVehicleDetails = findViewById(R.id.textViewVehicleDetails);
        TextView textViewAvailability = findViewById(R.id.textViewAvailability);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnStartStop = findViewById(R.id.btnStartStop);
        Button btnVehicleHistory = findViewById(R.id.btnVehicleHistory);
        Button btnReportRefuel = findViewById(R.id.btnReportRefuel);
        Button btnReportTreatment = findViewById(R.id.btnReportTreatment);

        // Retrieve data from Intent
        Intent intent = getIntent();
        String vehicleId = intent.getStringExtra("vehicleId");
        String vehicleType = intent.getStringExtra("vehicleType");
        double hoursTillTreatment = intent.getDoubleExtra("hoursTillTreatment", 0);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get user data
        if (user != null) {
            // Get the user's UID
            userMail = user.getEmail();
        }

        // Get companyId (=ManagerId)
        db.collection("Users")
                .document(userMail)
                .get()
                .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Toast.makeText(VehicleActivity.this,"hi from exists", Toast.LENGTH_SHORT).show();

                                    // Access the document data
                                    companyId = document.getString("company_id");
                                    firstName = document.getString("first_name");
                                    lastName = document.getString("last_name");
                                    Toast.makeText(VehicleActivity.this,"id->"+companyId, Toast.LENGTH_SHORT).show();

                                    // Set text for employee details
                                    textViewEmployeeDetails.setText(firstName + " " + lastName);
                                }else{
                                    Toast.makeText(VehicleActivity.this,"hi from else", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
        // Set text for vehicle details
        textViewVehicleDetails.setText("VehicleType: " + vehicleType + " (ID: " + vehicleId + ")");

        //Set text for availability
        if(hoursTillTreatment <= 0) {
            textViewAvailability.setText("Unavailable");
            textViewAvailability.setTextColor(Color.RED);
        } else {
            textViewAvailability.setText("Available");
            textViewAvailability.setTextColor(Color.GREEN);
        }
        //todo here
        // See if last action was start or stop and set button string by that
        assert vehicleId != null;
        Toast.makeText(VehicleActivity.this,"company id:"+companyId, Toast.LENGTH_SHORT).show();
        companyId="1" ;

        db.collection("Companies")
                .document(companyId).
                collection("Vehicles")
                .document(vehicleId)
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
                                String latestKey = sortedKeys.get(0);

                                // Get the action field from the latest entry
                                String latestAction = (String) entries.get(latestKey);


                                // Now, we can check if the latest action was "start" or "stop"
                                if ("start".equals(latestAction)) {
                                    btnStartStop.setText("Stop");
                                } else if ("stop".equals(latestAction)) {
                                    btnStartStop.setText("Start");
                                }
                            } else {btnStartStop.setText("Start");}
                        }
                    } else {
                    }
                });
        //////////////////////////////////////////////////////////////////////////////

        // History button click listener
        btnVehicleHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the VehicleActivity
                Intent intent = new Intent(VehicleActivity.this, VehicleHistoryActivity.class);

                // Pass necessary information as extras to the VehicleActivity
                intent.putExtra("vehicleId", vehicleId);

                // Start the VehicleActivity
                startActivity(intent);            }
        });

        // Back button click listener
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity to go back
                finish();

                // Create an Intent to start the EmployeeActivity and clear the activity stack
                Intent intent = new Intent(VehicleActivity.this, EmployeeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        // Button click listeners
        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String now = String.valueOf(System.currentTimeMillis());
                Map<String, Object> startStopData = new HashMap<>();

                // Check current vehicle status (started or not)
                if (btnStartStop.getText().equals("Start")) {
                    startStopData.put(now, "start");
                    btnStartStop.setText("Stop");
                }else{
                    startStopData.put(now, "stop");
                    btnStartStop.setText("Start");
                    updateHoursTillTreatment(now);
                }

                // Update data on start-stop
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Companies")
                        .document(companyId).
                        collection("Vehicles")
                        .document(vehicleId)
                        .collection("history")
                        .document("start-stop")
                        .update(startStopData)
                        .addOnSuccessListener(aVoid -> {
                            // Data successfully uploaded
                            // Show a success message
                            Toast.makeText(VehicleActivity.this, "Vehicle status changed", Toast.LENGTH_LONG).show();
                        });
            }
        });

        btnReportRefuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement logic to navigate to the report car care / refueling page
                showNameInputPopup();
            }
        });

        btnReportTreatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement logic to navigate to the report car care / refueling page
            }
        });
    }

//    //avi
private PopupWindow popupWindow;

    private void showNameInputPopup() {
        if (popupWindow == null) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.trying, null);
            Button submitButton = popupView.findViewById(R.id.submit_button);
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = auth.getCurrentUser();
            assert firebaseUser != null;

            submitButton.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText nameInput = popupView.findViewById(R.id.name_input);
                    String name = nameInput.getText().toString();
                    Toast.makeText(VehicleActivity.this, name, Toast.LENGTH_LONG).show();
                    String now = String.valueOf(System.currentTimeMillis());
                    Map<String,Object> fuel =new HashMap<>();
                    fuel.put(now,name);
                    Log.d("VehicleActivity", "Retrieved name: " + name);
                    String path = "Companies/"+companyId+"/Employees/"+userMail+"/history/refuels";
                    DocumentReference docRef = db.collection(path).document();
                    docRef.set(fuel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(VehicleActivity.this, "refuel saved", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure to save user data
                                    Toast.makeText(VehicleActivity.this, "not saved", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, e.toString());
                                }
                            });

                    // Do something with the entered name
                    dismissPopupWindow(); // Close the popup
                }
            });


            popupWindow = new PopupWindow(popupView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        // Use a suitable anchor view in your layout
    }

    private void dismissPopupWindow() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }
//    //avi

    private void updateHoursTillTreatment(String now){
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

                                double deltaHours = ((double) (nowLong-latestKeyLong)) / (60 * 60 * 1000);

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