package com.clearance.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaseHistoryResponseDto {
    private String NBISCaseID;
    private List<CaseHistoryDto> History;
}