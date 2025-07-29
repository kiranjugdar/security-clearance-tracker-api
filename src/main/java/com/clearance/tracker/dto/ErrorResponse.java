package com.clearance.tracker.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ErrorResponse {
    private int errorCode;
    private String errorMessage;
    private LocalDateTime timestamp;
    private String path;

    public ErrorResponse(int errorCode, String errorMessage, String path) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
}