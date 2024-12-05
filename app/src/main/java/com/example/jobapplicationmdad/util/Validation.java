package com.example.jobapplicationmdad.util;

import android.util.Patterns;

public class Validation {

    protected static boolean isNotEmpty(String field) {
        return (field != null) && !field.trim().isEmpty();
    }
    protected static boolean isValidCharacterLength(String field, int minCharacter) {
        return field.length() >= minCharacter;
    }
    protected static boolean isNoWhiteSpace(String field){
        return !field.contains(" ");
    }
    protected static boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    protected static boolean isValidPhoneNumber(String phoneNumber){
        return phoneNumber.matches("[689]\\d{7}$");
    }

}
