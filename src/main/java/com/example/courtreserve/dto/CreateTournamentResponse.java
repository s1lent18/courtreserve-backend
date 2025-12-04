package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CreateTournamentResponse {
    private Long id;
    private String name;
    private String sport;
    private Long organizerId;
    private Long courtId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Integer prize;
    private LocalDateTime created;
    private String eliminationType;
    private Boolean isAutoMode;
    private String entrance;
}