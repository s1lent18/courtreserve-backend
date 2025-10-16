package com.example.courtreserve.controller;

import com.example.courtreserve.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/tournament")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @PostMapping("/create")
    public ResponseEntity<?> createTournament(
            @RequestParam Long organizerId,
            @RequestBody TournamentService.CreateTournamentRequest request
    ) {
        try {
            TournamentService.CreateTournamentResponse tournament = tournamentService.createTournament(organizerId, request);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Tournament created successfully");
            response.put("tournament", tournament);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{tournamentId}")
    public ResponseEntity<?> getTournament(@PathVariable Long tournamentId) {
        try {
            TournamentService.GetTournamentResponse tournament = tournamentService.getTournamentById(tournamentId);
            Map<String, Object> response = new HashMap<>();
            response.put("tournament", tournament);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
