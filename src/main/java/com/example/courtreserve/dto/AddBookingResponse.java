package com.example.courtreserve.dto;

import com.example.courtreserve.database.models.Court;
import com.example.courtreserve.database.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
public class AddBookingResponse {
    Long id;
    User user;
    Court court;
    LocalDateTime startTime;
    LocalDateTime endTime;
    String status;
    Integer price;
    Integer advance;
    Integer toBePaid;
    LocalDateTime created;
}