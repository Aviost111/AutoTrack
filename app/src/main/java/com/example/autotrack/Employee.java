package com.example.autotrack;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;
class Employee {
    // Mandatory empty constructor
    // for use of FirebaseUI
    public Employee() {

    }
    @DocumentId
    private String documentId;



    @PropertyName("first_name")
    private String first_name;

    @PropertyName("last_name")
    private String last_name;

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
        return first_name;
    }
    public void setFirstname(String firstname)
    {
        this.first_name = firstname;
    }
    public String getLastname()
    {
        return last_name;
    }
    public void setLastname(String lastname)
    {
        this.last_name = lastname;
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

