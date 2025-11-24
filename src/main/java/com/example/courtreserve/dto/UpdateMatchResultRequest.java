package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMatchResultRequest {
    private Long matchId;
    private Long winnerTeamId;
    private String team1Score;
    private String team2Score;
    private String remarks;
}

