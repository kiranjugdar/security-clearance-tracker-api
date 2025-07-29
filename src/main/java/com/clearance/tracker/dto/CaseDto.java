package com.clearance.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaseDto {
    private String NBISCaseID;
    private String DISAStatus;
    private String SubjectID;
    private String pxCreateDateTime;
    private String pxUpdateDateTime;
    private String SFArchivalPDFExist;
}