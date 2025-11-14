package com.example.courtreserve.dto;

public interface GetPopularCourts {
    Long getId();
    String getName();
    String getDescription();
    String getLocation();
    String getType();
    Double getPrice();
    String getOpen();
    String getClose();
    Long getVendorId();
    Long getBookingCount();
    Double getAvgRating();
}