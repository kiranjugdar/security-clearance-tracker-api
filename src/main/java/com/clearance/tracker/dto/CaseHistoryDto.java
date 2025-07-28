package com.clearance.tracker.dto;

public class CaseHistoryDto {
    private String Time;
    private String Description;
    private String PerformedBy;

    public CaseHistoryDto() {}

    public CaseHistoryDto(String Time, String Description, String PerformedBy) {
        this.Time = Time;
        this.Description = Description;
        this.PerformedBy = PerformedBy;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String Time) {
        this.Time = Time;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getPerformedBy() {
        return PerformedBy;
    }

    public void setPerformedBy(String PerformedBy) {
        this.PerformedBy = PerformedBy;
    }
}