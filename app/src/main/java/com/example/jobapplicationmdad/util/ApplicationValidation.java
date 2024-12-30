package com.example.jobapplicationmdad.util;

import com.google.android.material.textfield.TextInputLayout;

public class ApplicationValidation extends Validation{
    public ApplicationValidation(){
        super();
    }
    public static boolean validateAddress(TextInputLayout layout, String address) {
        if (!isNotEmpty(address)) {
            layout.setError("Address is required");
            return false;
        }
        if (!isValidMinCharacterLength(address, 2)) {
            layout.setError("Address is too short");
            return false;
        }
        if(!isValidMaxCharacterLength(address,100)){
            layout.setError("Address is too long");
            return false;
        }
        layout.setError(null);
        return true;
    }
}
