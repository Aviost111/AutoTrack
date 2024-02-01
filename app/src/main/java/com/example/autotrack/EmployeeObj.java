package com.example.autotrack;

class EmployeeObj {
    // Mandatory empty constructor
    // for use of FirebaseUI

    private String first_name;


    private String last_name;


    private String phone;


    private String company_ID;


    private String manager_ID;

    private String email;

    public EmployeeObj() { }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompany_ID() {
        return company_ID;
    }

    public void setCompany_ID(String company_ID) {
        this.company_ID = company_ID;
    }

    public String getManager_ID() {
        return manager_ID;
    }

    public void setManager_ID(String manager_ID) {
        this.manager_ID = manager_ID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "first_name: " + first_name + "last name: " + last_name
                +"\n email: "+ email +
                "\n phone: " + phone;
    }
}

