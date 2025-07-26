package com.clearance.tracker.dto;

import java.util.List;

public class CombinedCaseResponse {
    private List<CaseHistoryItem> caseHistory;
    private CurrentStatus currentStatus;
    private List<StatusHistoryItem> statusHistory;
    private String selectedCaseId;

    public CombinedCaseResponse() {}

    public CombinedCaseResponse(List<CaseHistoryItem> caseHistory, CurrentStatus currentStatus, 
                               List<StatusHistoryItem> statusHistory, String selectedCaseId) {
        this.caseHistory = caseHistory;
        this.currentStatus = currentStatus;
        this.statusHistory = statusHistory;
        this.selectedCaseId = selectedCaseId;
    }

    public List<CaseHistoryItem> getCaseHistory() {
        return caseHistory;
    }

    public void setCaseHistory(List<CaseHistoryItem> caseHistory) {
        this.caseHistory = caseHistory;
    }

    public CurrentStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(CurrentStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public List<StatusHistoryItem> getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(List<StatusHistoryItem> statusHistory) {
        this.statusHistory = statusHistory;
    }

    public String getSelectedCaseId() {
        return selectedCaseId;
    }

    public void setSelectedCaseId(String selectedCaseId) {
        this.selectedCaseId = selectedCaseId;
    }
}