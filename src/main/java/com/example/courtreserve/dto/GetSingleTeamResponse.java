package com.example.courtreserve.dto;

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
    List<GetTeamMembers> members;
}
