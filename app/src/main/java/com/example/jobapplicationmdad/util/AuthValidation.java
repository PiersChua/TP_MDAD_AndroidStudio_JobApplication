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

    public static boolean validateNull(TextInputLayout layout, String field, String value) {
        if (!isNotEmpty(value)) {
            layout.setError(String.format("%s is required", field));
            return false;
        }
        layout.setError(null);
        return true;
    }

    public static boolean validateName(TextInputLayout layout, String name) {
        if (!isNotEmpty(name)) {
            layout.setError("Name is required");
            return false;
        }
        if (!isValidCharacterLength(name, 5)) {
            layout.setError("Name is too short");
            return false;
        }
        if (name.matches(".*\\d.*")) {
            layout.setError("Name cannot have numbers");
            return false;
        }
        layout.setError(null);
        return true;

    }

    public static boolean validateEmail(TextInputLayout layout, String email) {
        if (!isNotEmpty(email)) {
            layout.setError("Email is required");
            return false;
        }
        if (!isValidEmail(email)) {
            layout.setError("Email is invalid");
            return false;
        }
        layout.setError(null);
        return true;
    }

    public static boolean validatePhoneNumber(TextInputLayout layout, String phoneNumber) {
        if (!isNotEmpty(phoneNumber)) {
            layout.setError("Phone number is required");
            return false;
        }
        if (!isValidPhoneNumber(phoneNumber)) {
            layout.setError("Phone Number is invalid");
            return false;
        }
        layout.setError(null);
        return true;
    }

    public static boolean validateEnum(TextInputLayout layout, String field, String enumValue, String[] array) {
        if (!isNotEmpty(enumValue)) {
            layout.setError(String.format("%s is required", field));
            return false;
        }
        for (String acceptableEnumValue : array) {
            if (enumValue.equals(acceptableEnumValue)) {
                layout.setError(null);
                return true;
            }
        }
        layout.setError(String.format("%s is invalid", field));
        return false;
    }


    public static boolean validatePassword(TextInputLayout layout, String password, boolean isLogin) {
        if (!isNotEmpty(password)) {
            layout.setError("Password is required");
            return false;
        }
        if (!isNoWhiteSpace(password)) {
            layout.setError("No spaces allowed");
            return false;
        }
        if (!password.matches(PASSWORD_REGEX) && !isLogin) {
            layout.setError("Password must be at least 8 characters long, with at least 1 uppercase and 1 lowercase letter, and at least one number");
            return false;
        }
        layout.setError(null);
        return true;

    }

    public static boolean validateConfirmPassword(TextInputLayout layout, String password, String confirmPassword) {
        if (!isNotEmpty(confirmPassword)) {
            layout.setError("Confirm password is required");
            return false;
        }
        if (!confirmPassword.equals(password)) {
            layout.setError("Passwords do not match");
            return false;
        }
        layout.setError(null);
        return true;

    }

}
