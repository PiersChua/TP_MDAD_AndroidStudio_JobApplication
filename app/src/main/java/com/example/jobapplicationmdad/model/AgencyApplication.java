package com.example.jobapplicationmdad.model;

public class AgencyApplication {

    public String getAgencyApplicationId() {
        return agencyApplicationId;
    }

    public void setAgencyApplicationId(String agencyApplicationId) {
        this.agencyApplicationId = agencyApplicationId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }


    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    private String agencyApplicationId;
    private Status status;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String userId;
    private User user;
    private String createdAt;
    private String updatedAt;
    public AgencyApplication(){

    }

    // For job seeker
    public AgencyApplication(String name, String email, String phoneNumber, String address, String userId) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.userId = userId;
    }

}
