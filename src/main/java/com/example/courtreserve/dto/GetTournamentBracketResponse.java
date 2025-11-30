package com.example.courtreserve.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetTournamentBracketResponse {
    private Long tournamentId;
    private String tournamentName;
    private String eliminationType;
    private Boolean isAutoMode;
    private String status;
    @Schema(type = "array", implementation = Object.class)
    private List<GetMatchResponse> winnerBracketMatches;
    @Schema(type = "array", implementation = Object.class)
    private List<GetMatchResponse> loserBracketMatches; // Only for double elimination
}

