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
        if (!isValidCharacterLength(position, 10)) {
            layout.setError("Position is too short");
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
        if (!isValidCharacterLength(responsibilities, 50)) {
            layout.setError("Responsibilities is too short");
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
        if (!isValidCharacterLength(description, 50)) {
            layout.setError("Description is too short");
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
        if (!isValidCharacterLength(schedule, 5)) {
            layout.setError("Schedule is too short");
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
        if (!isValidCharacterLength(location, 2)) {
            layout.setError("Location is too short");
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
        if (!isValidCharacterLength(organisation, 2)) {
            layout.setError("Organisation is too short");
            return false;
        }
        layout.setError(null);
        return true;
    }

    public static boolean validateSalary(CheckBox partTimeCheckbox, CheckBox fullTimeCheckbox,  double partTimeSalary, double fullTimeSalary) {
        if (( !partTimeCheckbox.isChecked() && !fullTimeCheckbox.isChecked()) || (partTimeSalary == 0.0 && fullTimeSalary == 0.0)) {
            partTimeCheckbox.setError("Either part time or full time salary is required");
            fullTimeCheckbox.setError("Either part time or full time salary is required");
            return false;
        }
        partTimeCheckbox.setError(null);
        fullTimeCheckbox.setError(null);
        return true;
    }
}
