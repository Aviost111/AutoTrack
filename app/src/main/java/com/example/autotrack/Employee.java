package com.example.autotrack;

public class Employee {
    // Variable to store data corresponding
    // to firstname keyword in database
    private String firstname;

    // Variable to store data corresponding
    // to lastname keyword in database
    private String lastname;

    // Variable to store data corresponding
    // to phone keyword in database
    private String phone;

    // Variable to store data corresponding
    // to e-mail keyword in database
    private String email;

    // Variable to store data corresponding
    // to ID keyword in database
    private String ID;

    // Variable to store data corresponding
    // to ID keyword in database
    private String company_ID;

    // Variable to store data corresponding
    // to ID keyword in database
    private String manager_ID;

    // Mandatory empty constructor
    // for use of FirebaseUI
    public Employee() {}

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

    public String getID()
    {
        return ID;
    }
    public void setID(String ID)
    {
        this.ID = ID;
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

