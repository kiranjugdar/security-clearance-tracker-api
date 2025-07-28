package com.clearance.tracker.dto;

public class CaseDto {
    private String NBISCaseID;
    private String DISAStatus;
    private String SubjectID;
    private String pxCreateDateTime;
    private String pxUpdateDateTime;

    public CaseDto() {}

    public CaseDto(String NBISCaseID, String DISAStatus, String SubjectID, 
                   String pxCreateDateTime, String pxUpdateDateTime) {
        this.NBISCaseID = NBISCaseID;
        this.DISAStatus = DISAStatus;
        this.SubjectID = SubjectID;
        this.pxCreateDateTime = pxCreateDateTime;
        this.pxUpdateDateTime = pxUpdateDateTime;
    }

    public String getNBISCaseID() {
        return NBISCaseID;
    }

    public void setNBISCaseID(String NBISCaseID) {
        this.NBISCaseID = NBISCaseID;
    }

    public String getDISAStatus() {
        return DISAStatus;
    }

    public void setDISAStatus(String DISAStatus) {
        this.DISAStatus = DISAStatus;
    }

    public String getSubjectID() {
        return SubjectID;
    }

    public void setSubjectID(String SubjectID) {
        this.SubjectID = SubjectID;
    }

    public String getPxCreateDateTime() {
        return pxCreateDateTime;
    }

    public void setPxCreateDateTime(String pxCreateDateTime) {
        this.pxCreateDateTime = pxCreateDateTime;
    }

    public String getPxUpdateDateTime() {
        return pxUpdateDateTime;
    }

    public void setPxUpdateDateTime(String pxUpdateDateTime) {
        this.pxUpdateDateTime = pxUpdateDateTime;
    }
}