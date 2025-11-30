package com.example.courtreserve.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class GetSingleTeamResponse {
    Long id;
    String name;
    String sport;
    Long captainId;
    String captainName;
    @Schema(type = "array", implementation = Object.class)
    List<GetTeamMembers> members;
}
