package com.diploma.sporthalls.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SportHalls")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SportsHall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String address;

    @Column(name = "sport_type", nullable = false)
    private String sportType;

    @Column(name = "price_per_hour", nullable = false)
    private Double pricePerHour;


    @Transient
    @ManyToOne
    private User owner;

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved = false;

    // Новите полета от твоя списък
    private String imageUrl;
    private String workingHoursFrom; // напр. "08:00"
    private String workingHoursTo;   // напр. "22:00"
    private Double rating = 0.0;     // По подразбиране започва от 0.0

    @Column(name = "status", nullable = false)
    private String status = "PENDING";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public Double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(Double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Boolean getApproved() {
        return isApproved;
    }

    public void setApproved(Boolean approved) {
        isApproved = approved;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getWorkingHoursFrom() {
        return workingHoursFrom;
    }

    public void setWorkingHoursFrom(String workingHoursFrom) {
        this.workingHoursFrom = workingHoursFrom;
    }

    public String getWorkingHoursTo() {
        return workingHoursTo;
    }

    public void setWorkingHoursTo(String workingHoursTo) {
        this.workingHoursTo = workingHoursTo;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
