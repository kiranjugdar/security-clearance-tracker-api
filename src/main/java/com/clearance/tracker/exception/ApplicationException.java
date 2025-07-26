package com.clearance.tracker.exception;

public class ApplicationException extends Exception {
    
    private final int errorCode;

    public ApplicationException(String message) {
        super(message);
        this.errorCode = 9999; // Default error code for external service failures
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = 9999; // Default error code for external service failures
    }

    public ApplicationException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ApplicationException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}