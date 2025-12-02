package com.example.courtreserve.service.impl;

import com.example.courtreserve.database.models.*;
import com.example.courtreserve.database.repository.CourtRepository;
import com.example.courtreserve.database.repository.TeamRepository;
import com.example.courtreserve.database.repository.TournamentRepository;
import com.example.courtreserve.database.repository.TournamentTeamRepository;
import com.example.courtreserve.database.repository.UserRepository;
import com.example.courtreserve.dto.*;
import com.example.courtreserve.service.MatchService;
import com.example.courtreserve.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TournamentServiceImpl implements TournamentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourtRepository courtRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TournamentTeamRepository tournamentTeamRepository;

    @Autowired
    private MatchService matchService;

    @Override
    @Transactional
    public CreateTournamentResponse createTournament(Long organizerId, CreateTournamentRequest request) {
        // Find the organizer
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));

        // Find the court
        Court court = courtRepository.findById(request.getCourtId())
                .orElseThrow(() -> new RuntimeException("Court not found"));

        // Validate elimination type
        if (request.getEliminationType() != null && 
            !request.getEliminationType().equals("SINGLE") && 
            !request.getEliminationType().equals("DOUBLE")) {
            throw new RuntimeException("Invalid elimination type. Must be 'SINGLE' or 'DOUBLE'");
        }

        // Create tournament with PENDING APPROVAL status
        Tournament tournament = Tournament.builder()
                .name(request.getName())
                .sport(court.getType()) // Sport is identified by court type
                .organizer(organizer)
                .court(court)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status("PENDING")
                .prize(request.getPrize())
                .created(LocalDateTime.now())
                .eliminationType(request.getEliminationType())
                .isAutoMode(request.getIsAutoMode() != null ? request.getIsAutoMode() : false)
                .build();

        Tournament savedTournament = tournamentRepository.save(tournament);

        // Register teams if provided
        if (request.getTeamIds() != null && !request.getTeamIds().isEmpty()) {
            registerTeamsForTournament(savedTournament, request.getTeamIds());
            // Refresh tournament to get registered teams
            savedTournament = tournamentRepository.findById(savedTournament.getId()).orElseThrow();
        }

        return new CreateTournamentResponse(
                savedTournament.getId(),
                savedTournament.getName(),
                savedTournament.getSport(),
                savedTournament.getOrganizer().getId(),
                savedTournament.getCourt().getId(),
                savedTournament.getStartDate(),
                savedTournament.getEndDate(),
                savedTournament.getStatus(),
                savedTournament.getPrize(),
                savedTournament.getCreated(),
                savedTournament.getEliminationType(),
                savedTournament.getIsAutoMode()
        );
    }

    @Override
    public List<Tournament> getPendingTournaments(Long vendorId) {
        userRepository.findById(vendorId).orElseThrow(() -> new RuntimeException("Vendor Not Found"));

        List<Long> courtIds = courtRepository.findCourtIdsByVendorId(vendorId);

        return tournamentRepository.findPendingTournaments(courtIds);
    }

    @Override
    public Tournament confirmTournament(Long tournamentId) {
        Tournament pendingTournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament Not Found"));

        LocalDate now = LocalDate.now();

        if (pendingTournament.getStartDate().isAfter(now)) {
            pendingTournament.setStatus("CONFIRMED");
        } else {
            pendingTournament.setStatus("EXPIRED");
        }

        tournamentRepository.save(pendingTournament);

        return pendingTournament;
    }

    @Override
    public Tournament rejectTournament(Long TournamentId) {
        Tournament pendingTournament = tournamentRepository.findById(TournamentId).orElseThrow(() -> new RuntimeException("Tournament Not Found"));

        pendingTournament.setStatus("REJECTED");

        tournamentRepository.save(pendingTournament);

        return pendingTournament;
    }

    @Override
    public Tournament cancelTournament(Long TournamentId) {
        Tournament pendingTournament = tournamentRepository.findById(TournamentId).orElseThrow(() -> new RuntimeException("Tournament Not Found"));

        pendingTournament.setStatus("CANCELED");

        tournamentRepository.save(pendingTournament);

        return pendingTournament;
    }

    @Override
    public Page<GetTournamentResponse> getAllTournaments(String location, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Tournament> tournaments =
                tournamentRepository.findAllByCourt_Location(location, pageable);

        return tournaments.map(tournament -> {
            if (!tournament.getStatus().equals("CONFIRMED")) return null;

            return new GetTournamentResponse(
                    tournament.getId(),
                    tournament.getName(),
                    tournament.getSport(),
                    tournament.getOrganizer().getId(),
                    tournament.getOrganizer().getName(),
                    tournament.getCourt().getId(),
                    tournament.getCourt().getName(),
                    tournament.getStartDate(),
                    tournament.getEndDate(),
                    tournament.getStatus(),
                    tournament.getPrize(),
                    tournament.getCreated(),
                    tournament.getRegisteredTeams(),
                    tournament.getEliminationType(),
                    tournament.getIsAutoMode()
            );
        });
    }

    @Override
    public GetSingleTournamentResponse getSingleTournament(Long Id) {

        Tournament tournament = tournamentRepository.findById(Id).orElseThrow(() -> new RuntimeException("Tournament Not Found"));

        List<GetTournamentTeam> teams = new ArrayList<>();

        // Query tournament teams directly from repository
        List<TournamentTeam> tournamentTeams = tournamentTeamRepository.findByTournament(tournament);
        for (TournamentTeam tournamentTeam : tournamentTeams) {
            GetTournamentTeam team = new GetTournamentTeam(
                    tournamentTeam.getTeam().getId(),
                    tournamentTeam.getTeam().getName()
            );
            teams.add(team);
        }

        return new GetSingleTournamentResponse(
                tournament.getId(),
                tournament.getName(),
                tournament.getSport(),
                tournament.getOrganizer().getId(),
                tournament.getOrganizer().getName(),
                tournament.getCourt().getId(),
                tournament.getCourt().getName(),
                tournament.getStartDate(),
                tournament.getEndDate(),
                tournament.getStatus(),
                tournament.getPrize(),
                teams,
                tournament.getEliminationType(),
                tournament.getIsAutoMode()
        );
    }

    @Override
    public Tournament startTournament(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        if (!"CONFIRMED".equals(tournament.getStatus())) {
            throw new RuntimeException("Tournament must be confirmed before starting");
        }

        // Generate bracket
        matchService.generateBracket(tournamentId);

        // Tournament status is updated to IN_PROGRESS by generateBracket
        return tournamentRepository.findById(tournamentId).orElseThrow();
    }

    /**
     * Helper method to register teams for a tournament
     */
    private void registerTeamsForTournament(Tournament tournament, List<Long> teamIds) {
        for (Long teamId : teamIds) {
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new RuntimeException("Team with ID " + teamId + " not found"));

            // Check if team sport matches tournament sport
            if (!team.getSport().equalsIgnoreCase(tournament.getSport())) {
                throw new RuntimeException("Team '" + team.getName() + "' sport does not match tournament sport");
            }

            // Check if team is already registered
            if (tournamentTeamRepository.existsByTournamentAndTeam(tournament, team)) {
                throw new RuntimeException("Team '" + team.getName() + "' is already registered for this tournament");
            }

            // Register team for tournament
            TournamentTeam tournamentTeam = TournamentTeam.builder()
                    .id(new TournamentTeamId(tournament.getId(), teamId))
                    .tournament(tournament)
                    .team(team)
                    .registeredAt(LocalDateTime.now())
                    .build();

            tournamentTeamRepository.save(tournamentTeam);
        }
    }
}