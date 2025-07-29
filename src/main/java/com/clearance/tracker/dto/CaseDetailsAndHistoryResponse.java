package com.clearance.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaseDetailsAndHistoryResponse {
    private String caseId;
    private CaseDetailsDto caseDetails;
    private CaseHistoryResponseDto caseHistory;
}