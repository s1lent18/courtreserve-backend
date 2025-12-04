package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetMatchResponse {
    private Long id;
    private Long tournamentId;
    private String tournamentName;
    private Long courtId;
    private String courtName;
    private Long team1Id;
    private String team1Name;
    private Long team2Id;
    private String team2Name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Integer round;
    private String bracketType;
    private Integer matchPosition;
    private Long winnerTeamId;
    private String winnerTeamName;
}