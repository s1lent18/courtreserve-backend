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

import java.time.LocalDateTime;
import java.util.List;

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
        private String name;
        private Long courtId;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
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
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String status;
        private Integer prize;
        private LocalDateTime created;
    }

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

    public List<Tournament> getPendingTournaments(Long vendorId) {
        userRepository.findById(vendorId).orElseThrow(() -> new RuntimeException("Vendor Not Found"));

        List<Long> courtIds = courtRepository.findCourtIdsByVendorId(vendorId);

        return tournamentRepository.findPendingTournaments(courtIds);
    }

    public Tournament confirmTournament(Long tournamentId) {
        Tournament pendingTournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament Not Found"));

        LocalDateTime now = LocalDateTime.now();

        if (pendingTournament.getStartDate().isAfter(now)) {
            pendingTournament.setStatus("CONFIRMED");
        } else {
            pendingTournament.setStatus("EXPIRED");
        }

        tournamentRepository.save(pendingTournament);

        return pendingTournament;
    }

    public Tournament rejectTournament(Long TournamentId) {
        Tournament pendingTournament = tournamentRepository.findById(TournamentId).orElseThrow(() -> new RuntimeException("Tournament Not Found"));

        pendingTournament.setStatus("REJECTED");

        tournamentRepository.save(pendingTournament);

        return pendingTournament;
    }

    public Tournament cancelTournament(Long TournamentId) {
        Tournament pendingTournament = tournamentRepository.findById(TournamentId).orElseThrow(() -> new RuntimeException("Tournament Not Found"));

        pendingTournament.setStatus("CANCELED");

        tournamentRepository.save(pendingTournament);

        return pendingTournament;
    }
}
