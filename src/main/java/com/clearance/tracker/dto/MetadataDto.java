package com.clearance.tracker.dto;

public class MetadataDto {
    private int totalCases;

    public MetadataDto() {}

    public MetadataDto(int totalCases) {
        this.totalCases = totalCases;
    }

    public int getTotalCases() {
        return totalCases;
    }

    public void setTotalCases(int totalCases) {
        this.totalCases = totalCases;
    }
}