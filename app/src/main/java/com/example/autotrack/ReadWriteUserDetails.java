package com.example.autotrack;

public class ReadWriteUserDetails {
    String firstName;
    String lastName;
    String phoneNumber;
    String email;

    String companyID;

    public ReadWriteUserDetails(String firstName,String lastName,String phoneNumber,String email, String companyID){
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email=email;
        this.companyID=companyID;
    }
}
