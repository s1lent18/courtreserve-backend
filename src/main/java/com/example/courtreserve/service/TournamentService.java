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
    private UserRepository userRepository;

    @Autowired
    private CourtRepository courtRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Getter @Setter
    @AllArgsConstructor
    public static class CreateTournamentRequest {
        private Long organizerId;
        private Long courtId;
        private String name;
        private String startDate;
        private String endDate;
        private Integer prize;
    }

    @Getter @Setter
    @AllArgsConstructor
    public static class CreateTournamentResponse {
        private Long id;
        private String name;
        private String sport;
        private Long organizerId;
        private String organizerName;
        private Long courtId;
        private String courtName;
        private String startDate;
        private String endDate;
        private String status;
        private Integer prize;
        private String created;
    }

    public CreateTournamentResponse createTournament(CreateTournamentRequest request) {
        // Validate organizer exists
        User organizer = userRepository.findById(request.getOrganizerId())
                .orElseThrow(() -> new RuntimeException("Organizer not found"));

        // Validate court exists
        Court court = courtRepository.findById(request.getCourtId())
                .orElseThrow(() -> new RuntimeException("Court not found"));

        // Validate dates
        LocalDate startDate = LocalDate.parse(request.getStartDate());
        LocalDate endDate = LocalDate.parse(request.getEndDate());
        
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }

        // Create tournament
        Tournament tournament = Tournament.builder()
                .name(request.getName())
                .sport(court.getType()) // Sport is identified by court type
                .organizer(organizer)
                .court(court)
                .startDate(startDate)
                .endDate(endDate)
                .status("PENDING APPROVAL") // Status set to PENDING APPROVAL on creation
                .prize(request.getPrize())
                .created(LocalDateTime.now())
                .build();

        Tournament savedTournament = tournamentRepository.save(tournament);

        return new CreateTournamentResponse(
                savedTournament.getId(),
                savedTournament.getName(),
                savedTournament.getSport(),
                savedTournament.getOrganizer().getId(),
                savedTournament.getOrganizer().getName(),
                savedTournament.getCourt().getId(),
                savedTournament.getCourt().getName(),
                savedTournament.getStartDate().toString(),
                savedTournament.getEndDate().toString(),
                savedTournament.getStatus(),
                savedTournament.getPrize(),
                savedTournament.getCreated().toString()
        );
    }
}
