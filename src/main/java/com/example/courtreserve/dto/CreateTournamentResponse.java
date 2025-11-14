package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private Integer prize;
    private LocalDateTime created;
}