package com.clearance.tracker.dto;

public class PyWorkPageDto {
    private String spPersonaObjectID;
    private String DISAStatus;
    private String SubjectID;
    private String NBISCaseID;
    private String pxCreateDateTime;
    private String pxCreateOperator;
    private String pxUpdateDateTime;
    private String pxUpdateOperator;
    private String AddOrganization;
    private String AddOrgPath;
    private String AddInvestigationType;
    private String AddFormType;
    private String AddFormVersion;
    private EAppAccountInfoDto EAppAccountInfo;
    private PIPSStatusCheckResponseDto PIPSStatusCheckResponse;
    private String MiradorStatus;
    private String ReleaseDate;
    private String SFArchivalPDFExist;

    public PyWorkPageDto() {}

    public PyWorkPageDto(String spPersonaObjectID, String DISAStatus, String SubjectID, String NBISCaseID,
                        String pxCreateDateTime, String pxCreateOperator, String pxUpdateDateTime, String pxUpdateOperator,
                        String AddOrganization, String AddOrgPath, String AddInvestigationType, String AddFormType,
                        String AddFormVersion, EAppAccountInfoDto EAppAccountInfo, PIPSStatusCheckResponseDto PIPSStatusCheckResponse,
                        String MiradorStatus, String ReleaseDate, String SFArchivalPDFExist) {
        this.spPersonaObjectID = spPersonaObjectID;
        this.DISAStatus = DISAStatus;
        this.SubjectID = SubjectID;
        this.NBISCaseID = NBISCaseID;
        this.pxCreateDateTime = pxCreateDateTime;
        this.pxCreateOperator = pxCreateOperator;
        this.pxUpdateDateTime = pxUpdateDateTime;
        this.pxUpdateOperator = pxUpdateOperator;
        this.AddOrganization = AddOrganization;
        this.AddOrgPath = AddOrgPath;
        this.AddInvestigationType = AddInvestigationType;
        this.AddFormType = AddFormType;
        this.AddFormVersion = AddFormVersion;
        this.EAppAccountInfo = EAppAccountInfo;
        this.PIPSStatusCheckResponse = PIPSStatusCheckResponse;
        this.MiradorStatus = MiradorStatus;
        this.ReleaseDate = ReleaseDate;
        this.SFArchivalPDFExist = SFArchivalPDFExist;
    }

    public String getSpPersonaObjectID() {
        return spPersonaObjectID;
    }

    public void setSpPersonaObjectID(String spPersonaObjectID) {
        this.spPersonaObjectID = spPersonaObjectID;
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

    public String getNBISCaseID() {
        return NBISCaseID;
    }

    public void setNBISCaseID(String NBISCaseID) {
        this.NBISCaseID = NBISCaseID;
    }

    public String getPxCreateDateTime() {
        return pxCreateDateTime;
    }

    public void setPxCreateDateTime(String pxCreateDateTime) {
        this.pxCreateDateTime = pxCreateDateTime;
    }

    public String getPxCreateOperator() {
        return pxCreateOperator;
    }

    public void setPxCreateOperator(String pxCreateOperator) {
        this.pxCreateOperator = pxCreateOperator;
    }

    public String getPxUpdateDateTime() {
        return pxUpdateDateTime;
    }

    public void setPxUpdateDateTime(String pxUpdateDateTime) {
        this.pxUpdateDateTime = pxUpdateDateTime;
    }

    public String getPxUpdateOperator() {
        return pxUpdateOperator;
    }

    public void setPxUpdateOperator(String pxUpdateOperator) {
        this.pxUpdateOperator = pxUpdateOperator;
    }

    public String getAddOrganization() {
        return AddOrganization;
    }

    public void setAddOrganization(String AddOrganization) {
        this.AddOrganization = AddOrganization;
    }

    public String getAddOrgPath() {
        return AddOrgPath;
    }

    public void setAddOrgPath(String AddOrgPath) {
        this.AddOrgPath = AddOrgPath;
    }

    public String getAddInvestigationType() {
        return AddInvestigationType;
    }

    public void setAddInvestigationType(String AddInvestigationType) {
        this.AddInvestigationType = AddInvestigationType;
    }

    public String getAddFormType() {
        return AddFormType;
    }

    public void setAddFormType(String AddFormType) {
        this.AddFormType = AddFormType;
    }

    public String getAddFormVersion() {
        return AddFormVersion;
    }

    public void setAddFormVersion(String AddFormVersion) {
        this.AddFormVersion = AddFormVersion;
    }

    public EAppAccountInfoDto getEAppAccountInfo() {
        return EAppAccountInfo;
    }

    public void setEAppAccountInfo(EAppAccountInfoDto EAppAccountInfo) {
        this.EAppAccountInfo = EAppAccountInfo;
    }

    public PIPSStatusCheckResponseDto getPIPSStatusCheckResponse() {
        return PIPSStatusCheckResponse;
    }

    public void setPIPSStatusCheckResponse(PIPSStatusCheckResponseDto PIPSStatusCheckResponse) {
        this.PIPSStatusCheckResponse = PIPSStatusCheckResponse;
    }

    public String getMiradorStatus() {
        return MiradorStatus;
    }

    public void setMiradorStatus(String MiradorStatus) {
        this.MiradorStatus = MiradorStatus;
    }

    public String getReleaseDate() {
        return ReleaseDate;
    }

    public void setReleaseDate(String ReleaseDate) {
        this.ReleaseDate = ReleaseDate;
    }

    public String getSFArchivalPDFExist() {
        return SFArchivalPDFExist;
    }

    public void setSFArchivalPDFExist(String SFArchivalPDFExist) {
        this.SFArchivalPDFExist = SFArchivalPDFExist;
    }
}