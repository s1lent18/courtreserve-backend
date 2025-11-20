package com.example.courtreserve.service.impl;

import com.example.courtreserve.database.models.*;
import com.example.courtreserve.database.repository.*;
import com.example.courtreserve.dto.*;
import com.example.courtreserve.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TournamentTeamRepository tournamentTeamRepository;

    @Override
    public CreateTeamResponse createTeam(Long captainId, CreateTeamRequest request) {
        // Find the captain
        User captain = userRepository.findById(captainId)
                .orElseThrow(() -> new RuntimeException("Captain not found"));

        // Check if team name already exists
        if (teamRepository.existsByName(request.getName())) {
            throw new RuntimeException("Team name already exists");
        }

        // Create the team
        Team team = Team.builder()
                .name(request.getName())
                .sport(request.getSport())
                .captain(captain)
                .created(LocalDateTime.now())
                .build();

        Team savedTeam = teamRepository.save(team);

        // Add captain as first team member
        TeamMember captainMember = TeamMember.builder()
                .id(new TeamMemberId(savedTeam.getId(), captainId))
                .team(savedTeam)
                .user(captain)
                .role("CAPTAIN")
                .joinedAt(LocalDateTime.now())
                .build();

        teamMemberRepository.save(captainMember);

        // Add other members if provided
        if (request.getMemberIds() != null && !request.getMemberIds().isEmpty()) {
            for (Long memberId : request.getMemberIds()) {
                if (!memberId.equals(captainId)) { // Don't add captain twice
                    User member = userRepository.findById(memberId)
                            .orElseThrow(() -> new RuntimeException("Member with ID " + memberId + " not found"));

                    TeamMember teamMember = TeamMember.builder()
                            .id(new TeamMemberId(savedTeam.getId(), memberId))
                            .team(savedTeam)
                            .user(member)
                            .role("MEMBER")
                            .joinedAt(LocalDateTime.now())
                            .build();

                    teamMemberRepository.save(teamMember);
                }
            }
        }

        return new CreateTeamResponse(
                savedTeam.getId(),
                savedTeam.getName(),
                savedTeam.getSport(),
                savedTeam.getCaptain().getId(),
                savedTeam.getCreated(),
                request.getMemberIds()
        );
    }

    public JoinTournamentResponse joinTournament(JoinTournamentRequest request) {
        // Find the team
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new RuntimeException("Team not found"));

        // Find the tournament
        Tournament tournament = tournamentRepository.findById(request.getTournamentId())
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        // Check if team is already registered for this tournament
        if (tournamentTeamRepository.existsByTournamentAndTeam(tournament, team)) {
            throw new RuntimeException("Team is already registered for this tournament");
        }

        // Check if team sport matches tournament sport
        if (!team.getSport().equalsIgnoreCase(tournament.getSport())) {
            throw new RuntimeException("Team sport does not match tournament sport");
        }

        // Register team for tournament
        TournamentTeam tournamentTeam = TournamentTeam.builder()
                .id(new TournamentTeamId(request.getTournamentId(), request.getTeamId()))
                .tournament(tournament)
                .team(team)
                .registeredAt(LocalDateTime.now())
                .build();

        tournamentTeamRepository.save(tournamentTeam);

        return new JoinTournamentResponse(
                request.getTournamentId(),
                request.getTeamId(),
                team.getName(),
                LocalDateTime.now()
        );
    }

    @Override
    public GetSingleTeamResponse getSingleTeam(Long id) {
        Team team = teamRepository.findById(id).orElseThrow(() -> new RuntimeException("Team not Found"));

        List<GetTeamMembers> members = new ArrayList<>();

        for (TeamMember teamMember : team.getMembers()) {
            GetTeamMembers member = new GetTeamMembers(
                    teamMember.getId(),
                    teamMember.getUser().getId(),
                    teamMember.getUser().getName(),
                    teamMember.getRole()
            );
            members.add(member);
        }

        return new GetSingleTeamResponse(
                team.getId(),
                team.getName(),
                team.getSport(),
                team.getCaptain().getId(),
                team.getCaptain().getName(),
                members
        );
    }
}
