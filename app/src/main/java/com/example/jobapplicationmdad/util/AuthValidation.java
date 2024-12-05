package com.example.jobapplicationmdad.util;

import android.content.Context;

import com.example.jobapplicationmdad.R;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.Array;

public class AuthValidation extends Validation {
    /*
     * Alphanumeric regex
     * At least 1 uppercase
     * At least 1 lowercase
     * At least 8 characters
     * */
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";

    public AuthValidation() {
        super();
    }

    public static boolean validateName(TextInputLayout layout, String name) {
        if (!isNotEmpty(name)) {
            layout.setError("Name is required");
            return false;
        } else if (!isValidCharacterLength(name, 5)) {
            layout.setError("Name is too short");
            return false;
        } else if (name.matches(".*\\d.*")) {
            layout.setError("Name cannot have numbers");
            return false;
        } else {
            layout.setError(null);
            return true;
        }
    }

    public static boolean validateEmail(TextInputLayout layout, String email) {
        if (!isNotEmpty(email)) {
            layout.setError("Email is required");
            return false;
        } else if (!isValidEmail(email)) {
            layout.setError("Email is invalid");
            return false;
        } else {
            layout.setError(null);
            return true;
        }
    }

    public static boolean validatePhoneNumber(TextInputLayout layout, String phoneNumber) {
        if (!isNotEmpty(phoneNumber)) {
            layout.setError("Phone number is required");
            return false;
        }
        if (!isValidPhoneNumber(phoneNumber)) {
            layout.setError("Phone Number is invalid");
            return false;
        } else {
            layout.setError(null);
            return true;
        }
    }

    public static boolean validateRole(TextInputLayout layout, String role) {
        if (!isNotEmpty(role)) {
            layout.setError("Role is required");
            return false;
        } else if (!role.equals("Job Seeker") && !role.equals("Admin")) {
            layout.setError("Role is invalid");
            return false;
        } else {
            layout.setError(null);
            return true;
        }
    }

    public static boolean validatePassword(TextInputLayout layout, String password) {
        if (!isNotEmpty(password)) {
            layout.setError("Password is required");
            return false;
        }
        if (!isNoWhiteSpace(password)) {
            layout.setError("No spaces allowed");
            return false;
        } else if (!password.matches(PASSWORD_REGEX)) {
            layout.setError("Password must be at least 8 characters long, with at least 1 uppercase and 1 lowercase letter, and at least one number");
            return false;
        } else {
            layout.setError(null);
            return true;
        }
    }

    public static boolean validateConfirmPassword(TextInputLayout layout, String password, String confirmPassword) {
        if (!confirmPassword.isEmpty() && !password.isEmpty() && !confirmPassword.equals(password)) {
            layout.setError("Passwords do not match");
            return false;
        } else {
            layout.setError(null);
            return true;
        }
    }

}
