package com.example.courtreserve.dto;

import com.example.courtreserve.database.models.TeamMemberId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class GetTeamMembers {
    TeamMemberId teamId;
    Long id;
    String name;
    String role;
    String coverImage;
}