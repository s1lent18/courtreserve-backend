package com.example.courtreserve.dto;

import com.example.courtreserve.database.models.Booking;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetSingleCourtVendorResponse {
    Long id;
    LocalTime closeTime;
    LocalDateTime createdAt;
    String description;
    String location;
    String name;
    LocalTime openTime;
    Integer price;
    String type;
    @Schema(type = "array", implementation = Object.class)
    List<BookingResponse> bookings;
}