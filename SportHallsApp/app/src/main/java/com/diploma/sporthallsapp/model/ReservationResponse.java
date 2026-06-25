package com.diploma.sporthallsapp.model;

import java.io.Serializable;

public class ReservationResponse implements Serializable {

    private Long id;
    private String reservationDate; // Формат: YYYY-MM-DD
    private String reservationTime; // Формат: HH:MM
    private String status;
    private SportsHall sportsHall;

    public ReservationResponse() {}

    public ReservationResponse(Long id, String reservationDate, String reservationTime, String status, SportsHall sportsHall) {
        this.id = id;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
        this.status = status;
        this.sportsHall = sportsHall;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(String reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(String reservationTime) {
        this.reservationTime = reservationTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SportsHall getSportsHall() {
        return sportsHall;
    }

    public void setSportsHall(SportsHall sportsHall) {
        this.sportsHall = sportsHall;
    }
}
