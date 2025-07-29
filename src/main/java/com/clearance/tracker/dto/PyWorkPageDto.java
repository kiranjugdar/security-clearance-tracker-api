package com.clearance.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}