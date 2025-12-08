package com.example.courtreserve.service.impl;

import com.example.courtreserve.database.models.*;
import com.example.courtreserve.database.repository.*;
import com.example.courtreserve.dto.*;
import com.example.courtreserve.exception.BadRequestException;
import com.example.courtreserve.exception.ConflictException;
import com.example.courtreserve.exception.ResourceNotFoundException;
import com.example.courtreserve.service.MatchService;
import com.example.courtreserve.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private MatchRepository matchRepository;

    @Autowired
    private TournamentTeamRepository tournamentTeamRepository;

    @Autowired
    private MatchService matchService;

    @Override
    @Transactional
    public CreateTournamentResponse createTournament(Long organizerId, CreateTournamentRequest request) {
        // Find the organizer
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer", "id", organizerId));

        // Find the court
        Court court = courtRepository.findById(request.getCourtId())
                .orElseThrow(() -> new ResourceNotFoundException("Court", "id", request.getCourtId()));

        // Validate elimination type
        if (request.getEliminationType() != null && 
            !request.getEliminationType().equals("SINGLE") && 
            !request.getEliminationType().equals("DOUBLE")) {
            throw new BadRequestException("Invalid elimination type. Must be 'SINGLE' or 'DOUBLE'");
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
                .entrance(request.getEntrance())
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
                savedTournament.getIsAutoMode(),
                savedTournament.getEntrance()
        );
    }

    @Override
    public List<TournamentResponse> getPendingTournaments(Long vendorId) {
        userRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", vendorId));

        List<Long> courtIds = courtRepository.findCourtIdsByVendorId(vendorId);

        List<Tournament> tournaments = tournamentRepository.findPendingTournaments(courtIds);

        return tournaments.stream()
                .map(tournament -> new TournamentResponse(
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
                        tournament.getEliminationType(),
                        tournament.getIsAutoMode(),
                        tournament.getEntrance()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public TournamentResponse confirmTournament(Long tournamentId) {
        Tournament pendingTournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", "id", tournamentId));

        LocalDate now = LocalDate.now();

        if (pendingTournament.getStartDate().isAfter(now)) {
            pendingTournament.setStatus("CONFIRMED");
        } else {
            pendingTournament.setStatus("EXPIRED");
        }

        Tournament response = tournamentRepository.save(pendingTournament);

        return new TournamentResponse(
                response.getId(),
                response.getName(),
                response.getSport(),
                response.getOrganizer().getId(),
                response.getOrganizer().getName(),
                response.getCourt().getId(),
                response.getCourt().getName(),
                response.getStartDate(),
                response.getEndDate(),
                response.getStatus(),
                response.getPrize(),
                response.getCreated(),
                response.getEliminationType(),
                response.getIsAutoMode(),
                response.getEntrance()
        );
    }

    @Override
    public TournamentResponse rejectTournament(Long TournamentId) {
        Tournament pendingTournament = tournamentRepository.findById(TournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", "id", TournamentId));

        pendingTournament.setStatus("REJECTED");

        Tournament response = tournamentRepository.save(pendingTournament);

        return new TournamentResponse(
                response.getId(),
                response.getName(),
                response.getSport(),
                response.getOrganizer().getId(),
                response.getOrganizer().getName(),
                response.getCourt().getId(),
                response.getCourt().getName(),
                response.getStartDate(),
                response.getEndDate(),
                response.getStatus(),
                response.getPrize(),
                response.getCreated(),
                response.getEliminationType(),
                response.getIsAutoMode(),
                response.getEntrance()
        );
    }

    @Override
    public TournamentResponse cancelTournament(Long TournamentId) {
        Tournament pendingTournament = tournamentRepository.findById(TournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", "id", TournamentId));

        pendingTournament.setStatus("CANCELED");

        Tournament response = tournamentRepository.save(pendingTournament);

        return new TournamentResponse(
                response.getId(),
                response.getName(),
                response.getSport(),
                response.getOrganizer().getId(),
                response.getOrganizer().getName(),
                response.getCourt().getId(),
                response.getCourt().getName(),
                response.getStartDate(),
                response.getEndDate(),
                response.getStatus(),
                response.getPrize(),
                response.getCreated(),
                response.getEliminationType(),
                response.getIsAutoMode(),
                response.getEntrance()
        );
    }

    @Override
    public PaginatedResponse<GetTournamentResponse> getAllTournaments(String location, Pageable pageable) {

        Page<Tournament> tournaments =
                tournamentRepository.findAllByCourt_Location(location, pageable);

        List<GetTournamentResponse> responses = tournaments.getContent()
                .stream()
                .filter(t -> "CONFIRMED".equals(t.getStatus()))
                .map(t -> {
                    List<GetTournamentTeam> teams = t.getRegisteredTeams()
                            .stream()
                            .map(team -> new GetTournamentTeam(
                                    team.getTournament().getId(),
                                    team.getTeam().getName()
                            ))
                            .toList();

                    return new GetTournamentResponse(
                            t.getId(),
                            t.getName(),
                            t.getSport(),
                            t.getOrganizer().getId(),
                            t.getOrganizer().getName(),
                            t.getCourt().getId(),
                            t.getCourt().getName(),
                            t.getStartDate(),
                            t.getEndDate(),
                            t.getStatus(),
                            t.getPrize(),
                            t.getCreated(),
                            teams,
                            t.getEliminationType(),
                            t.getIsAutoMode(),
                            t.getEntrance()
                    );
                })
                .toList();

        return new PaginatedResponse<>(
                tournaments.getNumber(),
                tournaments.getSize(),
                tournaments.getTotalPages(),
                tournaments.getTotalElements(),
                responses
        );
    }

    @Override
    public GetSingleTournamentResponse getSingleTournament(Long Id) {

        Tournament tournament = tournamentRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", "id", Id));

        List<GetTournamentTeam> teams = new ArrayList<>();

        List<Match> matches = matchRepository.findByTournament(tournament);

        List<SingleMatchResponse> matchResponses = matches.stream()
                .map(match -> new SingleMatchResponse(
                        match.getId(),
                        match.getTeam1().getId(),
                        match.getTeam1().getName(),
                        match.getTeam2().getId(),
                        match.getTeam2().getName(),
                        match.getStartTime(),
                        match.getEndTime(),
                        match.getStatus(),
                        match.getBracketType(),
                        match.getMatchPosition(),
                        match.getWinnerTeam().getId(),
                        match.getNextWinnerMatch(),
                        match.getNextLoserMatch()
                ))
                .toList();

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
                tournament.getIsAutoMode(),
                tournament.getEntrance(),
                matchResponses
        );
    }

    @Override
    public TournamentResponse startTournament(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", "id", tournamentId));

        if (!"CONFIRMED".equals(tournament.getStatus())) {
            throw new BadRequestException("Tournament must be confirmed before starting");
        }

        // Generate bracket
        matchService.generateBracket(tournamentId);

        return new TournamentResponse(
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
                tournament.getEliminationType(),
                tournament.getIsAutoMode(),
                tournament.getEntrance()
        );
    }

    /**
     * Helper method to register teams for a tournament
     */
    private void registerTeamsForTournament(Tournament tournament, List<Long> teamIds) {
        for (Long teamId : teamIds) {
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new ResourceNotFoundException("Team", "id", teamId));

            // Check if team sport matches tournament sport
            if (!team.getSport().equalsIgnoreCase(tournament.getSport())) {
                throw new BadRequestException("Team '" + team.getName() + "' sport does not match tournament sport");
            }

            // Check if team is already registered
            if (tournamentTeamRepository.existsByTournamentAndTeam(tournament, team)) {
                throw new ConflictException("Team '" + team.getName() + "' is already registered for this tournament");
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
