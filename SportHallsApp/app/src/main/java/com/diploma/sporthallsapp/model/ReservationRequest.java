package com.diploma.sporthallsapp.model;

public class ReservationRequest {

    private SportsHallRequest sportsHall;
    private String startTime; // Трябва да съвпада с формата в Spring

    public ReservationRequest(SportsHallRequest sportsHall, String dateTime) {
        this.sportsHall = sportsHall;
        this.startTime = dateTime;
    }

    // Вграден помощен клас, който да генерира структурата { "id": X }
    public static class SportsHallRequest {
        private Long id;

        public SportsHallRequest(Long id) {
            this.id = id;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }

    public SportsHallRequest getSportsHallId() {
        return sportsHall;
    }

    public void setSportsHallId(SportsHallRequest sportsHall) {
        this.sportsHall = sportsHall;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setDateTime(String startTime) {
        this.startTime = startTime;
    }
}
