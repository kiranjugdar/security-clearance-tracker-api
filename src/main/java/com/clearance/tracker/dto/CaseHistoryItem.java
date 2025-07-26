package com.clearance.tracker.dto;

public class CaseHistoryItem {
    private Long id;
    private String caseId;
    private String caseStatus;
    private String link;

    public CaseHistoryItem() {}

    public CaseHistoryItem(Long id, String caseId, String caseStatus, String link) {
        this.id = id;
        this.caseId = caseId;
        this.caseStatus = caseStatus;
        this.link = link;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getCaseStatus() {
        return caseStatus;
    }

    public void setCaseStatus(String caseStatus) {
        this.caseStatus = caseStatus;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}