package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddTeamMemberRequest {
    private Long teamId;
    private Long userId;
    private String role; // "MEMBER", "CO_CAPTAIN", etc. (default: "MEMBER")
}

