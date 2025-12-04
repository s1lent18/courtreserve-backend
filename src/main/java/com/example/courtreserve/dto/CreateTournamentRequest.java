package com.example.courtreserve.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

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
    @Schema(type = "array", implementation = Long.class)
    private List<Long> teamIds; // List of team IDs to register for the tournament
    private String entrance;
}