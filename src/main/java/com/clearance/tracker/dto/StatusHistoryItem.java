package com.clearance.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusHistoryItem {
    private Long id;
    private String name;
    private LocalDateTime date;
    private String status;
    private String description;
}