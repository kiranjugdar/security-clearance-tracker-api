package com.clearance.tracker.dto;

import java.time.LocalDateTime;

public class StatusHistoryItem {
    private Long id;
    private String name;
    private LocalDateTime date;
    private String status;
    private String description;

    public StatusHistoryItem() {}

    public StatusHistoryItem(Long id, String name, LocalDateTime date, String status, String description) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.status = status;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}