package com.clearance.tracker.dto;

public class PIPSStatusCheckResponseDto {
    private String cancelled;
    private String certifiedByApplicant;
    private CurrentStatusDto CurrentStatus;

    public PIPSStatusCheckResponseDto() {}

    public PIPSStatusCheckResponseDto(String cancelled, String certifiedByApplicant, CurrentStatusDto CurrentStatus) {
        this.cancelled = cancelled;
        this.certifiedByApplicant = certifiedByApplicant;
        this.CurrentStatus = CurrentStatus;
    }

    public String getCancelled() {
        return cancelled;
    }

    public void setCancelled(String cancelled) {
        this.cancelled = cancelled;
    }

    public String getCertifiedByApplicant() {
        return certifiedByApplicant;
    }

    public void setCertifiedByApplicant(String certifiedByApplicant) {
        this.certifiedByApplicant = certifiedByApplicant;
    }

    public CurrentStatusDto getCurrentStatus() {
        return CurrentStatus;
    }

    public void setCurrentStatus(CurrentStatusDto CurrentStatus) {
        this.CurrentStatus = CurrentStatus;
    }
}