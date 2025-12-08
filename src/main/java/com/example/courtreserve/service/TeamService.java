package com.example.courtreserve.service;


import com.example.courtreserve.dto.*;

public interface TeamService {

    CreateTeamResponse createTeam(Long captainId, CreateTeamRequest request);

    JoinTournamentResponse joinTournament(JoinTournamentRequest request);

    GetSingleTeamResponse getSingleTeam(Long id);

    TeamMemberResponse addTeamMember(AddTeamMemberRequest request);

    void removeTeamMember(RemoveTeamMemberRequest request);

    TeamMemberResponse updateTeamMember(UpdateTeamMemberRequest request);

    String generateCode(String captainEmail);

    GetSingleTeamResponse validateCode(ValidateOTP validateOTP);
}
