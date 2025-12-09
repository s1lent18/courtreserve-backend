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
public class AddMatchRequestRequest {
    private Long courtId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long teamId; // Optional, for TEAM_SEEKING_PLAYER
    private String requestType; // USER_SEEKING_TEAM, TEAM_SEEKING_PLAYER
    private String message;
}
