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

    @PostMapping("/createTournament")
    @PreAuthorize("hasAnyRole('VENDOR', 'USER')")
    public ResponseEntity<?> createTournament(
            @RequestBody CreateTournamentRequest request,
            @RequestParam Long organizerId
    ) {
        var tournament = tournamentService.createTournament(organizerId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Tournament created successfully", "tournament", tournament));
    }

    @PreAuthorize("hasAnyRole('VENDOR', 'USER')")
    @PostMapping("/{tournamentId}/cancelTournament")
    public ResponseEntity<?> cancelTournament(
            @PathVariable Long tournamentId
    ) {
        var canceledTournament = tournamentService.rejectTournament(tournamentId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Tournament Canceled", "canceledTournament", canceledTournament));
    }

    @GetMapping("/getAllTournaments")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getAllTournaments(
            @RequestParam String location,
            @Parameter(hidden = true) Pageable pageable
    ) {
        PaginatedResponse<GetTournamentResponse> tournaments =
                tournamentService.getAllTournaments(location, pageable);

        return ResponseEntity.ok(Map.of(
                "message", "All Tournaments Obtained",
                "page", tournaments.getPage(),
                "size", tournaments.getSize(),
                "totalPages", tournaments.getTotalPages(),
                "totalElements", tournaments.getTotalElements(),
                "content", tournaments.getContent()
        ));
    }

    @GetMapping("/getSingleTournament")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getSingleTournament(
            @RequestParam Long Id
    ) {
        var tournament = tournamentService.getSingleTournament(Id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Single Tournament Returned", "singleTournament", tournament));
    }

    @PostMapping("/{tournamentId}/startTournament")
    @PreAuthorize("hasAnyRole('VENDOR', 'USER')")
    public ResponseEntity<?> startTournament(
            @PathVariable Long tournamentId
    ) {
        var tournament = tournamentService.startTournament(tournamentId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Tournament started and bracket generated", "tournament", tournament));
    }

    @GetMapping("/getPendingTournaments")
    @PreAuthorize("hasAnyRole('VENDOR', 'USER')")
    public ResponseEntity<?> getPendingTournaments(
            @RequestParam Long id
    ) {
        var pendingTournaments = tournamentService.getPendingTournaments(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Pending Tournaments returned", "pendingTournaments", pendingTournaments));
    }

    @PostMapping("/{tournamentId}/confirmTournament")
    @PreAuthorize("hasAnyRole('VENDOR', 'USER')")
    public ResponseEntity<?> confirmTournament(
            @PathVariable Long tournamentId
    ) {
        var confirmedTournament = tournamentService.confirmTournament(tournamentId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Tournament Confirmed", "confirmedTournament", confirmedTournament));
    }

    @PostMapping("/{tournamentId}/rejectTournament")
    @PreAuthorize("hasAnyRole('VENDOR', 'USER')")
    public ResponseEntity<?> rejectTournament(
            @PathVariable Long tournamentId
    ) {
        var rejectedTournament = tournamentService.rejectTournament(tournamentId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Tournament Rejected", "rejectedTournament", rejectedTournament));
    }
}
