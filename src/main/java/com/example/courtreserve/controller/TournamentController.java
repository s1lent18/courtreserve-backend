package com.example.courtreserve.controller;

import com.example.courtreserve.dto.CreateTournamentRequest;
import com.example.courtreserve.dto.GetTournamentResponse;
import com.example.courtreserve.dto.PaginatedResponse;
import com.example.courtreserve.service.TournamentService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.persistence.EntityNotFoundException;
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

    // Not Implemented Yet

    @PostMapping("/createTournament")
    @PreAuthorize("hasAnyRole('VENDOR', 'USER')")
    public ResponseEntity<?> createTournament(
            @RequestBody CreateTournamentRequest request,
            @RequestParam Long organizerId
    ) {
        try {
            var tournament = tournamentService.createTournament(organizerId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Tournament created successfully", "tournament", tournament));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    // Not Implemented Yet
    @PreAuthorize("hasAnyRole('VENDOR', 'USER')")
    @PostMapping("/{tournamentId}/cancelTournament")
    public ResponseEntity<?> cancelTournament(
            @PathVariable Long tournamentId
    ) {
        try {
            var canceledTournament = tournamentService.rejectTournament(tournamentId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Tournament Canceled", "canceledTournament", canceledTournament));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
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
        try {
            var tournament = tournamentService.getSingleTournament(Id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Single Tournament Returned", "singleTournament", tournament));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{tournamentId}/startTournament")
    @PreAuthorize("hasAnyRole('VENDOR', 'USER')")
    public ResponseEntity<?> startTournament(
            @PathVariable Long tournamentId
    ) {
        try {
            var tournament = tournamentService.startTournament(tournamentId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Tournament started and bracket generated", "tournament", tournament));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/getPendingTournaments")
    @PreAuthorize("hasAnyRole('VENDOR', 'USER')")
    public ResponseEntity<?> getPendingTournaments(
            @RequestParam Long id
    ) {
        try {
            var pendingTournaments = tournamentService.getPendingTournaments(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Pending Tournaments returned", "pendingTournaments", pendingTournaments));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/{tournamentId}/confirmTournament")
    @PreAuthorize("hasAnyRole('VENDOR', 'USER')")
    public ResponseEntity<?> confirmTournament(
            @PathVariable Long tournamentId
    ) {
        try {
            var confirmedTournament = tournamentService.confirmTournament(tournamentId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Tournament Confirmed", "confirmedTournament", confirmedTournament));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/{tournamentId}/rejectTournament")
    @PreAuthorize("hasAnyRole('VENDOR', 'USER')")
    public ResponseEntity<?> rejectTournament(
            @PathVariable Long tournamentId
    ) {
        try {
            var rejectedTournament = tournamentService.rejectTournament(tournamentId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Tournament Rejected", "rejectedTournament", rejectedTournament));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
