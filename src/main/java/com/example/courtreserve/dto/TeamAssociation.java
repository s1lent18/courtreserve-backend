package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeamAssociation {
    Long captainTeamId;
    Long memberTeamId;
}
