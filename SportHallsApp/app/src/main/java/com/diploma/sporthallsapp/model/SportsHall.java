package com.diploma.sporthallsapp.model;

import java.io.Serializable;

public class SportsHall implements Serializable {

    private Long id;
    private String name;
    private String description;
    private String type;
    private String location;
    private double pricePerHour;
    private String status;           // "PENDING", "APPROVED"
    private String imageUrl;         // Линк към снимката
    private String workingHoursFrom; // "08:00"
    private String workingHoursTo;   // "22:00"
    private Double rating;

    // Гетъри, за да можем да четем данните в екраните
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getLocation() {
        return location;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public String getStatus() {
        return status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getWorkingHoursFrom() {
        return workingHoursFrom;
    }

    public String getWorkingHoursTo() {
        return workingHoursTo;
    }

    public Double getRating() {
        return rating;
    }
}
