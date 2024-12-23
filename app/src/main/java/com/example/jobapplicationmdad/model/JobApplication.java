package com.example.jobapplicationmdad.model;

public class JobApplication {
    public JobApplication(Status status, String createdAt, String updatedAt, Job job) {
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.job = job;
    }

    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    private String createdAt;
    private String updatedAt;
    private Status status;
    private Job job;

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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Job getJob() {
        return job;
    }
}
