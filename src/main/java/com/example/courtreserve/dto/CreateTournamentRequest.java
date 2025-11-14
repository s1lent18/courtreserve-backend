package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CreateTournamentRequest {
    private String name;
    private Long courtId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer prize;
}