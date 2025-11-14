package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class JoinTournamentResponse {
    private Long tournamentId;
    private Long teamId;
    private String teamName;
    private LocalDateTime registeredAt;
}