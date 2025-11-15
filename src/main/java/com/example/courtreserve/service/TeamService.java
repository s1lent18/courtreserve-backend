package com.example.courtreserve.service;


import com.example.courtreserve.dto.CreateTeamRequest;
import com.example.courtreserve.dto.CreateTeamResponse;
import com.example.courtreserve.dto.JoinTournamentRequest;
import com.example.courtreserve.dto.JoinTournamentResponse;

public interface TeamService {

    CreateTeamResponse createTeam(Long captainId, CreateTeamRequest request);

    JoinTournamentResponse joinTournament(JoinTournamentRequest request);

}
