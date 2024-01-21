package com.example.autotrack;
import com.google.firebase.firestore.PropertyName;
class Employee {
    // Mandatory empty constructor
    // for use of FirebaseUI
    public Employee() {

    }

    @PropertyName("firstname")
    private String firstname;

    @PropertyName("lastname")
    private String lastname;

    @PropertyName("phone")
    private String phone;

    @PropertyName("company_ID")
    private String company_ID;

    @PropertyName("manager_ID")
    private String manager_ID;
    @PropertyName("email")
    private String email;



    // Getter and setter method
    public String getFirstname()
    {
        return firstname;
    }
    public void setFirstname(String firstname)
    {
        this.firstname = firstname;
    }
    public String getLastname()
    {
        return lastname;
    }
    public void setLastname(String lastname)
    {
        this.lastname = lastname;
    }
    public String getEmail()
    {
        return email;
    }
    public void setEmail(String email)
    {
        this.email = email;
    }
    public String getPhone()
    {
        return phone;
    }
    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getCompany_ID()
    {
        return company_ID;
    }
    public void setCompany_ID(String c_ID)
    {
        this.company_ID = c_ID;
    }

    public String getManager_ID()
    {
        return manager_ID;
    }
    public void setManager_ID(String M_ID)
    {
        this.manager_ID = M_ID;
    }
}

