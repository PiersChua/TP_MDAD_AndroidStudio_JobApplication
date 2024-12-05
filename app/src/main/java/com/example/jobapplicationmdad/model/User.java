package com.example.jobapplicationmdad.model;

public class User {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String role;
    private String password;
    private String confirmPassword;
    //private String dateOfBirth; // Nullable
    //private String gender;  // Nullable
    //private String resume;  // Nullable
    //private String agencyId;  // Nullable (Foreign Key)
    //private String createdAt;  // Nullable (Timestamp)
    //private String updatedAt;  // Nullable (Timestamp)

 // Register
    public User(String fullName, String email, String phoneNumber, String role, String password, String confirmPassword) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

}
