package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingResponse {
    Long id;
    Long userId;
    String userName;
    Long courtId;
    String courtName;
    LocalDateTime startTime;
    LocalDateTime endTime;
    String status = "PENDING";
    Integer price;
    Integer advance;
    Integer toBePaid;
    LocalDateTime created;
}
