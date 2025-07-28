package com.clearance.tracker.dto;

import java.util.List;

public class CaseHistoryResponseDto {
    private String NBISCaseID;
    private List<CaseHistoryDto> History;

    public CaseHistoryResponseDto() {}

    public CaseHistoryResponseDto(String NBISCaseID, List<CaseHistoryDto> History) {
        this.NBISCaseID = NBISCaseID;
        this.History = History;
    }

    public String getNBISCaseID() {
        return NBISCaseID;
    }

    public void setNBISCaseID(String NBISCaseID) {
        this.NBISCaseID = NBISCaseID;
    }

    public List<CaseHistoryDto> getHistory() {
        return History;
    }

    public void setHistory(List<CaseHistoryDto> History) {
        this.History = History;
    }
}