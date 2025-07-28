package com.clearance.tracker.dto;

import java.util.List;

public class CaseListResponseDto {
    private List<CaseDto> cases;
    private MetadataDto metadata;

    public CaseListResponseDto() {}

    public CaseListResponseDto(List<CaseDto> cases, MetadataDto metadata) {
        this.cases = cases;
        this.metadata = metadata;
    }

    public List<CaseDto> getCases() {
        return cases;
    }

    public void setCases(List<CaseDto> cases) {
        this.cases = cases;
    }

    public MetadataDto getMetadata() {
        return metadata;
    }

    public void setMetadata(MetadataDto metadata) {
        this.metadata = metadata;
    }
}