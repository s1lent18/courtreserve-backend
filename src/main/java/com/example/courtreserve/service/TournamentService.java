package com.example.courtreserve.service;

import com.example.courtreserve.database.models.Court;
import com.example.courtreserve.database.models.Tournament;
import com.example.courtreserve.database.models.User;
import com.example.courtreserve.database.repository.CourtRepository;
import com.example.courtreserve.database.repository.TournamentRepository;
import com.example.courtreserve.database.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourtRepository courtRepository;

    @Getter @Setter
    @AllArgsConstructor
    public static class CreateTournamentRequest {
        private String name;
        private Long courtId;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer prize;
    }

    @Getter @Setter
    @AllArgsConstructor
    public static class CreateTournamentResponse {
        private Long id;
        private String name;
        private String sport;
        private Long organizerId;
        private Long courtId;
        private LocalDate startDate;
        private LocalDate endDate;
        private String status;
        private Integer prize;
        private LocalDateTime created;
    }

    @Getter @Setter
    @AllArgsConstructor
    public static class GetTournamentResponse {
        private Long id;
        private String name;
        private String sport;
        private String organizerName;
        private String courtName;
        private String courtLocation;
        private LocalDate startDate;
        private LocalDate endDate;
        private String status;
        private Integer prize;
        private LocalDateTime created;
    }

    public CreateTournamentResponse createTournament(Long organizerId, CreateTournamentRequest request) {
        // Validate organizer exists
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("Organizer not found with id: " + organizerId));

        // Validate court exists
        Court court = courtRepository.findById(request.getCourtId())
                .orElseThrow(() -> new RuntimeException("Court not found with id: " + request.getCourtId()));

        // Validate dates
        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Start date cannot be in the past");
        }
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("End date cannot be before start date");
        }

        // Create tournament
        Tournament tournament = Tournament.builder()
                .name(request.getName())
                .sport(court.getType()) // Sport is derived from court type
                .organizer(organizer)
                .court(court)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status("PENDING APPROVAL")
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

    public GetTournamentResponse getTournamentById(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found with id: " + tournamentId));

        return new GetTournamentResponse(
                tournament.getId(),
                tournament.getName(),
                tournament.getSport(),
                tournament.getOrganizer().getName(),
                tournament.getCourt().getName(),
                tournament.getCourt().getLocation(),
                tournament.getStartDate(),
                tournament.getEndDate(),
                tournament.getStatus(),
                tournament.getPrize(),
                tournament.getCreated()
        );
    }
}
