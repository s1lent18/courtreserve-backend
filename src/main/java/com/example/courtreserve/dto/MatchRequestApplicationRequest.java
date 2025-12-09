package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchRequestApplicationRequest {
    private Long matchRequestId;
    private Long teamId; // Optional, if a team is applying/inviting
    private String message;
}
