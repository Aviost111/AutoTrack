package com.example.autotrack;

public class Vehicle {
    private String ID;
    private String company_ID;
    private String engine_size;
    private int treatment_hours;
    private int hours_till_treatment;
    private int manufacture_year;
    private String type;
    private String version;

    // Required empty public constructor for Firestore
    public Vehicle() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCompany_ID() {
        return company_ID;
    }

    public void setCompany_ID(String company_ID) {
        this.company_ID = company_ID;
    }

    public String getEngine_size() {
        return engine_size;
    }

    public void setEngine_size(String engine_size) {
        this.engine_size = engine_size;
    }

    public int getTreatment_hours() {
        return treatment_hours;
    }

    public void setTreatment_hours(int treatment_hours) {
        this.treatment_hours = treatment_hours;
    }

    public int getHours_till_treatment() {
        return hours_till_treatment;
    }

    public void setHours_till_treatment(int hours_till_treatment) {
        this.hours_till_treatment = hours_till_treatment;
    }

    public int getManufacture_year() {
        return manufacture_year;
    }

    public void setManufacture_year(int manufacture_year) {
        this.manufacture_year = manufacture_year;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "VehicleType: " + type + ",VehicleId " + ID +
                "\nversion: " + version +
                "\nCompany ID: " + company_ID +
                "\nManufacturing Year: " + manufacture_year +
                "\nHours left for treatment" + hours_till_treatment +
                "\n------------------------";
    }
}

