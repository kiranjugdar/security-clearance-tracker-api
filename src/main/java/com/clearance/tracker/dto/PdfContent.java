package com.clearance.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdfContent {
    private Long id;
    private String caseId;
    private String documentName;
    private String documentType;
    private String fileName;
    private String content;
    private LocalDateTime uploadDate;
    private String uploadedBy;
    private String status;
}