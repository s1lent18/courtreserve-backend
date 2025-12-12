package com.example.courtreserve.controller;

import com.example.courtreserve.dto.CreateTournamentRequest;
import com.example.courtreserve.dto.GetTournamentResponse;
import com.example.courtreserve.dto.PaginatedResponse;
import com.example.courtreserve.service.TournamentService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/tournament")
public class TournamentController {

    @Autowired
    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
            this.tournamentService = tournamentService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createTournament(
        @RequestBody CreateTournamentRequest request,
        @RequestParam Long organizerId
    ) {
        var tournament = tournamentService.createTournament(organizerId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Tournament created successfully", "tournament", tournament));
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/{tournamentId}/cancel")
    public ResponseEntity<?> cancelTournament(
        @PathVariable Long tournamentId
    ) {
        var canceledTournament = tournamentService.rejectTournament(tournamentId);
        return ResponseEntity.status(HttpStatus.OK)
            .body(Map.of("message", "Tournament Canceled", "canceledTournament", canceledTournament));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getAllTournaments(
        @RequestParam String location,
        @Parameter(hidden = true) Pageable pageable
    ) {
        PaginatedResponse<GetTournamentResponse> tournaments = tournamentService.getAllTournaments(location, pageable);

        return ResponseEntity.ok(Map.of(
            "message", "All Tournaments Obtained",
            "page", tournaments.getPage(),
            "size", tournaments.getSize(),
            "totalPages", tournaments.getTotalPages(),
            "totalElements", tournaments.getTotalElements(),
            "content", tournaments.getContent()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getSingleTournament(
        @PathVariable Long id
    ) {
        var tournament = tournamentService.getSingleTournament(id);
        return ResponseEntity.status(HttpStatus.OK)
            .body(Map.of("message", "Single Tournament Returned", "singleTournament", tournament));
    }

    @PatchMapping("/{tournamentId}/start")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> startTournament(
        @PathVariable Long tournamentId
    ) {
        var tournament = tournamentService.startTournament(tournamentId);
        return ResponseEntity.status(HttpStatus.OK)
            .body(Map.of("message", "Tournament started and bracket generated", "tournament", tournament));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> getPendingTournaments(
        @RequestParam Long id
    ) {
        var pendingTournaments = tournamentService.getPendingTournaments(id);
        return ResponseEntity.status(HttpStatus.OK)
            .body(Map.of("message", "Pending Tournaments returned", "pendingTournaments", pendingTournaments));
    }

    @PatchMapping("/{tournamentId}/confirm")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> confirmTournament(
        @PathVariable Long tournamentId
    ) {
        var confirmedTournament = tournamentService.confirmTournament(tournamentId);
        return ResponseEntity.status(HttpStatus.OK)
            .body(Map.of("message", "Tournament Confirmed", "confirmedTournament", confirmedTournament));
    }

    @PatchMapping("/{tournamentId}/reject")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> rejectTournament(
        @PathVariable Long tournamentId
    ) {
        var rejectedTournament = tournamentService.rejectTournament(tournamentId);
        return ResponseEntity.status(HttpStatus.OK)
            .body(Map.of("message", "Tournament Rejected", "rejectedTournament", rejectedTournament));
    }
}
