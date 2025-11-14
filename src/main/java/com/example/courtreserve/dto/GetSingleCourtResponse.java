package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetSingleCourtResponse {
    Long id;
    String name;
    String description;
    String location;
    String type;
    Double price;
    String open;
    String close;
    Long vendorId;
    Long bookingCount;
    Double avgRating;
    List<BookingTimeProjection> bookedTimes;
}
