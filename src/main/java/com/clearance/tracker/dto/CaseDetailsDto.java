package com.clearance.tracker.dto;

public class CaseDetailsDto {
    private PyWorkPageDto pyWorkPage;

    public CaseDetailsDto() {}

    public CaseDetailsDto(PyWorkPageDto pyWorkPage) {
        this.pyWorkPage = pyWorkPage;
    }

    public PyWorkPageDto getPyWorkPage() {
        return pyWorkPage;
    }

    public void setPyWorkPage(PyWorkPageDto pyWorkPage) {
        this.pyWorkPage = pyWorkPage;
    }
}