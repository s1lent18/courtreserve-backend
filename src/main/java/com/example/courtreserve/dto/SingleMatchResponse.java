package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class SingleMatchResponse {
    Long Id;
    Long team1Id;
    String team1Name;
    Long team2Id;
    String team2Name;
    LocalDateTime startTime;
    LocalDateTime endTime;
    String status;
    String bracketType;
    Integer matchPosition;
    Long winnerTeam;
    Long nextWinnerMatch;
    Long nextLoserMatch;
}