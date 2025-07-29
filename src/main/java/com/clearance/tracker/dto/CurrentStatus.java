package com.clearance.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentStatus {
    private String caseId;
    private String currentStage;
    private String status;
    private LocalDateTime lastUpdated;
    private String description;
}