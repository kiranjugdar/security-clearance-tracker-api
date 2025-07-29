package com.clearance.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CombinedCaseResponse {
    private CaseListResponseDto casesList;
    private CaseDetailsDto selectedCaseDetails;
    private CaseHistoryResponseDto caseHistory;
    private String selectedCaseId;
}