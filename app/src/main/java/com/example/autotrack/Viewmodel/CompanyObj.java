package com.example.autotrack.Viewmodel;

public class CompanyObj {
    private String companyId;
    private String first_name;
    private String last_name;
    private  String Name;
    private String email;
    private String phone;

    public CompanyObj() {
    }
    public CompanyObj(String first_name,String last_name,String email,String phone) {
        this.email=email;
        this.first_name=first_name;
        this.last_name=last_name;
        this.phone=phone;
        this.Name=first_name +" "+ last_name;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

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
    public String getName() {
        return Name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
