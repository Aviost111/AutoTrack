package com.example.autotrack.Model;

import android.util.Patterns;

import java.util.Calendar;

// This class is used to validate fields in the registration forms
// It is used in RegisterEmployeeActivity and RegisterToolActivity to validate the user input
public class InputValidator {

    // Helper method to check if any of the provided fields are empty
    public static boolean areFieldsEmpty(String... fields) {
        for (String field : fields) {
            if (field.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    //Method to check if the phone number is valid
    public static boolean isValidPhoneNumber(String number) {
        if (number.length() != 10) {
            return false;
        }
        for (int i = 0; i < number.length(); i++) {
            if (number.charAt(i) < '0' || number.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }


    public static boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //Method to check if the name is valid (only letters)
    public static boolean isValidName(String name) {
        return name.matches("[a-zA-Z]+");
    }


    //------------- Tool validation methods -------------

    public static boolean isValidManufactureYear(String manufactureYearStr) {
        // Validate if the manufacture year is a valid integer
        try {
            int manufactureYear = Integer.parseInt(manufactureYearStr);
            return manufactureYear >= 1800 && manufactureYear <= Calendar.getInstance().get(Calendar.YEAR);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidTreatmentHours(String treatmentHoursStr) {
        // Validate if the treatment hours is a valid integer
        try {
            int treatmentHours = Integer.parseInt(treatmentHoursStr);
            return treatmentHours > 0; // Assuming treatment hours cannot be negative or zero
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean containsOnlyLettersAndNumbers(String str) {
        // Validate if the string contains only letters and numbers
        return str.matches("[a-zA-Z0-9]+");
    }

    public static boolean isValidType(String type) {
        // Validate if the type is not empty and contains only letters and numbers
        return !type.trim().isEmpty() && containsOnlyLettersAndNumbers(type);
    }

    public static boolean isValidEngineSize(String engineSize) {
        // Validate if the engineSize is not empty and contains only letters and numbers
        return !engineSize.trim().isEmpty() && containsOnlyLettersAndNumbers(engineSize);
    }

    public static boolean isValidVersion(String version) {
        // Validate if the version is not empty and contains only letters and numbers
        return !version.trim().isEmpty() && containsOnlyLettersAndNumbers(version);
    }

    public static boolean containsOnlyNumbers(String str) {
        // Validate if the string contains only numbers
        return str.matches("[0-9]+");
    }

}
