package com.clearance.tracker.dto;

import java.time.LocalDateTime;

public class CurrentStatus {
    private String caseId;
    private String currentStage;
    private String status;
    private LocalDateTime lastUpdated;
    private String description;

    public CurrentStatus() {}

    public CurrentStatus(String caseId, String currentStage, String status, LocalDateTime lastUpdated, String description) {
        this.caseId = caseId;
        this.currentStage = currentStage;
        this.status = status;
        this.lastUpdated = lastUpdated;
        this.description = description;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(String currentStage) {
        this.currentStage = currentStage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}