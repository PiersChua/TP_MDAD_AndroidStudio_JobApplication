package com.example.jobapplicationmdad.model;

import java.io.Serializable;

public class Job implements Serializable {
    private String jobId;
    private String position;
    private String responsibilities;
    private String description;
    private String location;
    private String schedule;
    private String organisation;
    private double partTimeSalary;
    private double fullTimeSalary;
    private String userId;
    private String createdAt;
    private String updatedAt;
    public Job(){

    }

    public Job(String jobId, String position, String responsibilities, String location, double partTimeSalary, double fullTimeSalary, String updatedAt) {
        this.jobId = jobId;
        this.position = position;
        this.responsibilities = responsibilities;
        this.location = location;
        this.partTimeSalary = partTimeSalary;
        this.fullTimeSalary = fullTimeSalary;
        this.updatedAt = updatedAt;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(String responsibilities) {
        this.responsibilities = responsibilities;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public double getPartTimeSalary() {
        return partTimeSalary;
    }

    public void setPartTimeSalary(double partTimeSalary) {
        this.partTimeSalary = partTimeSalary;
    }

    public double getFullTimeSalary() {
        return fullTimeSalary;
    }

    public void setFullTimeSalary(double fullTimeSalary) {
        this.fullTimeSalary = fullTimeSalary;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
