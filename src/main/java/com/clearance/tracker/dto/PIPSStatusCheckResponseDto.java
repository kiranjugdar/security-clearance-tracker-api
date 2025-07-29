package com.clearance.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PIPSStatusCheckResponseDto {
    private String cancelled;
    private String certifiedByApplicant;
    private CurrentStatusDto CurrentStatus;
}