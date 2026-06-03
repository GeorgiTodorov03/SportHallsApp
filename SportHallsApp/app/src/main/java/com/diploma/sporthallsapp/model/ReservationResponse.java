package com.diploma.sporthallsapp.model;

import java.io.Serializable;

public class ReservationResponse implements Serializable {

    private Long id;
    private String startTime; // Бекендът го праща като String във формат "2026-06-02T18:00:00"
    private String status;

    public ReservationResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
