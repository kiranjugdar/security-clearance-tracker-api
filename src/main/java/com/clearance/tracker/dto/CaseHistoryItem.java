package com.clearance.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaseHistoryItem {
    private Long id;
    private String caseId;
    private String caseStatus;
    private String link;
}