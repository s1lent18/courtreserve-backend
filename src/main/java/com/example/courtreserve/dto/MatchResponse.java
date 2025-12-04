package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MatchResponse {
    Long id;
    Long tournamentId;
    String tournamentName;
    Long courtId;
    String courtName;
    Long team1Id;
    String team1Name;
    Long team2Id;
    String team2Name;
    String status;
    Integer round;
    String bracketType;
    Integer matchPosition;
    Long nextWinnerMatch;
    Long nextLoserMatch;
}
