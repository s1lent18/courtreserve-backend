package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchRequestResponse {
    private Long id;
    private Long requesterId;
    private String requesterName;
    private Long teamId;
    private String teamName;
    private Long courtId;
    private String courtName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String requestType;
    private String message;
}
