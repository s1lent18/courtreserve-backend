package com.example.courtreserve.service.impl;

import com.example.courtreserve.database.models.*;
import com.example.courtreserve.database.repository.CourtRepository;
import com.example.courtreserve.database.repository.TournamentRepository;
import com.example.courtreserve.database.repository.UserRepository;
import com.example.courtreserve.dto.CreateTournamentRequest;
import com.example.courtreserve.dto.CreateTournamentResponse;
import com.example.courtreserve.dto.GetSingleTournamentResponse;
import com.example.courtreserve.dto.GetTournamentResponse;
import com.example.courtreserve.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class TournamentServiceImpl implements TournamentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourtRepository courtRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Override
    public CreateTournamentResponse createTournament(Long organizerId, CreateTournamentRequest request) {
        // Find the organizer
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));

        // Find the court
        Court court = courtRepository.findById(request.getCourtId())
                .orElseThrow(() -> new RuntimeException("Court not found"));

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
                .build();

        Tournament savedTournament = tournamentRepository.save(tournament);

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
                savedTournament.getCreated()
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
                    tournament.getRegisteredTeams()
            );
        });
    }

    @Override
    public GetSingleTournamentResponse getSingleTournament(Long Id) {

        Tournament tournament = tournamentRepository.findById(Id).orElseThrow(() -> new RuntimeException("Tournament Not Found"));

        List<Team> teams = new ArrayList<>();

        for (TournamentTeam tournamentTeam : tournament.getRegisteredTeams()) {
            teams.add(tournamentTeam.getTeam());
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
                teams
        );
    }
}