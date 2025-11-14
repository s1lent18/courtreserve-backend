package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class GetBookingResponse {
    Long id;
    Long courtId;
    String courtName;
    LocalDateTime startTime;
    LocalDateTime endTime;
    String status;
    Integer price;
    Integer advance;
    Integer toBePaid;
    LocalDateTime created;
}