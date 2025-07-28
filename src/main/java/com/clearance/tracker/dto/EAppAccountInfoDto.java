package com.clearance.tracker.dto;

public class EAppAccountInfoDto {
    private String PreviousStatus;
    private String Status;

    public EAppAccountInfoDto() {}

    public EAppAccountInfoDto(String PreviousStatus, String Status) {
        this.PreviousStatus = PreviousStatus;
        this.Status = Status;
    }

    public String getPreviousStatus() {
        return PreviousStatus;
    }

    public void setPreviousStatus(String PreviousStatus) {
        this.PreviousStatus = PreviousStatus;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }
}