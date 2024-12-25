package com.example.jobapplicationmdad.util;

import android.widget.CheckBox;

import com.google.android.material.textfield.TextInputLayout;

public class JobValidation extends Validation {
    public JobValidation() {
        super();
    }

    public static boolean validatePosition(TextInputLayout layout, String position) {
        if (!isNotEmpty(position)) {
            layout.setError("Position is required");
            return false;
        }
        if (!isValidMinCharacterLength(position, 10)) {
            layout.setError("Position is too short");
            return false;
        }
        if(!isValidMaxCharacterLength(position,100)){
            layout.setError("Position is too long");
            return false;
        }
        layout.setError(null);
        return true;
    }

    public static boolean validateResponsibilities(TextInputLayout layout, String responsibilities) {
        if (!isNotEmpty(responsibilities)) {
            layout.setError("Responsibilities is required");
            return false;
        }
        if (!isValidMinCharacterLength(responsibilities, 50)) {
            layout.setError("Responsibilities is too short, at least 50 characters required");
            return false;
        }
        if(!isValidMaxCharacterLength(responsibilities,250)){
            layout.setError("Responsibilities is too long");
            return false;
        }
        layout.setError(null);
        return true;
    }

    public static boolean validateDescription(TextInputLayout layout, String description) {
        if (!isNotEmpty(description)) {
            layout.setError("Description is required");
            return false;
        }
        if (!isValidMinCharacterLength(description, 50)) {
            layout.setError("Description is too short, at least 50 characters required");
            return false;
        }
        if(!isValidMaxCharacterLength(description,250)){
            layout.setError("Description is too long");
            return false;
        }
        layout.setError(null);
        return true;
    }

    public static boolean validateSchedule(TextInputLayout layout, String schedule) {
        if (!isNotEmpty(schedule)) {
            layout.setError("Schedule is required");
            return false;
        }
        if (!isValidMinCharacterLength(schedule, 5)) {
            layout.setError("Schedule is too short");
            return false;
        }
        if(!isValidMaxCharacterLength(schedule,50)){
            layout.setError("Schedule is too long");
            return false;
        }
        layout.setError(null);
        return true;
    }

    public static boolean validateLocation(TextInputLayout layout, String location) {
        if (!isNotEmpty(location)) {
            layout.setError("Location is required");
            return false;
        }
        if (!isValidMinCharacterLength(location, 2)) {
            layout.setError("Location is too short");
            return false;
        }
        if(!isValidMaxCharacterLength(location,100)){
            layout.setError("Location is too long");
            return false;
        }
        layout.setError(null);
        return true;
    }

    public static boolean validateOrganisation(TextInputLayout layout, String organisation) {
        if (!isNotEmpty(organisation)) {
            layout.setError("Organisation is required");
            return false;
        }
        if (!isValidMinCharacterLength(organisation, 2)) {
            layout.setError("Organisation is too short");
            return false;
        }
        if(!isValidMaxCharacterLength(organisation,100)){
            layout.setError("Organisation is too long");
            return false;
        }
        layout.setError(null);
        return true;
    }

    public static boolean validateSalary(CheckBox partTimeCheckbox, CheckBox fullTimeCheckbox,  double partTimeSalary, double fullTimeSalary) {
        if (( !partTimeCheckbox.isChecked() && !fullTimeCheckbox.isChecked()) || (partTimeSalary == 0.0 && fullTimeSalary == 0.0)) {
            partTimeCheckbox.setError("Either part time or full time salary is required");
            fullTimeCheckbox.setError("Either part time or full time salary is required");
            partTimeCheckbox.requestFocus();
            return false;
        }
        partTimeCheckbox.setError(null);
        fullTimeCheckbox.setError(null);
        return true;
    }
}
