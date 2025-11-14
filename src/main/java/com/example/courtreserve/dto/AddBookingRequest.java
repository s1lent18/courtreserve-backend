package com.example.courtreserve.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
public class AddBookingRequest {
    Long userId;
    Long facilityId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    LocalDateTime endTime;
    Integer price;
    Integer advance;
}