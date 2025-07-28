package com.clearance.tracker.dto;

public class CombinedCaseResponse {
    private CaseListResponseDto casesList;
    private CaseDetailsDto selectedCaseDetails;
    private CaseHistoryResponseDto caseHistory;
    private String selectedCaseId;

    public CombinedCaseResponse() {}

    public CombinedCaseResponse(CaseListResponseDto casesList, CaseDetailsDto selectedCaseDetails, 
                               CaseHistoryResponseDto caseHistory, String selectedCaseId) {
        this.casesList = casesList;
        this.selectedCaseDetails = selectedCaseDetails;
        this.caseHistory = caseHistory;
        this.selectedCaseId = selectedCaseId;
    }

    public CaseListResponseDto getCasesList() {
        return casesList;
    }

    public void setCasesList(CaseListResponseDto casesList) {
        this.casesList = casesList;
    }

    public CaseDetailsDto getSelectedCaseDetails() {
        return selectedCaseDetails;
    }

    public void setSelectedCaseDetails(CaseDetailsDto selectedCaseDetails) {
        this.selectedCaseDetails = selectedCaseDetails;
    }

    public CaseHistoryResponseDto getCaseHistory() {
        return caseHistory;
    }

    public void setCaseHistory(CaseHistoryResponseDto caseHistory) {
        this.caseHistory = caseHistory;
    }

    public String getSelectedCaseId() {
        return selectedCaseId;
    }

    public void setSelectedCaseId(String selectedCaseId) {
        this.selectedCaseId = selectedCaseId;
    }
}