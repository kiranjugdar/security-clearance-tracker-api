package com.clearance.tracker.dto;

public class CurrentStatusDto {
    private String code2;
    private String name2;

    public CurrentStatusDto() {}

    public CurrentStatusDto(String code2, String name2) {
        this.code2 = code2;
        this.name2 = name2;
    }

    public String getCode2() {
        return code2;
    }

    public void setCode2(String code2) {
        this.code2 = code2;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }
}