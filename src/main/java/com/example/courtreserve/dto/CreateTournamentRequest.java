package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class CreateTournamentRequest {
    private String name;
    private Long courtId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer prize;
    private String eliminationType; // "SINGLE" or "DOUBLE"
    private Boolean isAutoMode; // true for automatic, false for manual
}