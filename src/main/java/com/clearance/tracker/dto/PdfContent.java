package com.clearance.tracker.dto;

import java.time.LocalDateTime;

public class PdfContent {
    private Long id;
    private String caseId;
    private String documentName;
    private String documentType;
    private String fileName;
    private String content;
    private LocalDateTime uploadDate;
    private String uploadedBy;
    private String status;

    public PdfContent() {}

    public PdfContent(Long id, String caseId, String documentName, String documentType, 
                     String fileName, String content, LocalDateTime uploadDate, 
                     String uploadedBy, String status) {
        this.id = id;
        this.caseId = caseId;
        this.documentName = documentName;
        this.documentType = documentType;
        this.fileName = fileName;
        this.content = content;
        this.uploadDate = uploadDate;
        this.uploadedBy = uploadedBy;
        this.status = status;
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

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}