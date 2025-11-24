package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class GetSingleTournamentResponse {
    Long id;
    String name;
    String sport;
    Long userId;
    String userName;
    Long courtId;
    String courtName;
    LocalDate startDate;
    LocalDate endDate;
    String status;
    Integer prize;
    List<GetTournamentTeam> tournamentTeams;
    String eliminationType;
    Boolean isAutoMode;
}