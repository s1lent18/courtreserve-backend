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
public class TeamMemberResponse {
    private Long teamId;
    private String teamName;
    private Long userId;
    private String userName;
    private String role;
    private LocalDateTime joinedAt;
}

