package com.example.courtreserve.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TournamentResponse {
    Long id;
    String name;
    String sport;
    Long organizerId;
    String organizerName;
    Long courtId;
    String courtName;
    LocalDate startDate;
    LocalDate endDate;
    String status;
    Integer prize;
    LocalDateTime created;
    String eliminationType;
    Boolean isAutoMode;
    String entrance;
}