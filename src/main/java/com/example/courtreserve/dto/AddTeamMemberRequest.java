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
    private String userEmail;
    private String role; // "MEMBER", "CO_CAPTAIN", etc. (default: "MEMBER")
}

