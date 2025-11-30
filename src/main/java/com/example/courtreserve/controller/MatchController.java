package com.example.courtreserve.controller;

import com.example.courtreserve.dto.ScheduleMatchRequest;
import com.example.courtreserve.dto.UpdateMatchResultRequest;
import com.example.courtreserve.service.MatchService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/match")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @GetMapping("/{tournamentId}/bracket")
    public ResponseEntity<?> getTournamentBracket(
            @PathVariable Long tournamentId
    ) {
        try {
            var bracket = matchService.getTournamentBracket(tournamentId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Tournament bracket retrieved", "bracket", bracket));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/schedule")
    public ResponseEntity<?> scheduleMatch(
            @RequestBody ScheduleMatchRequest request
    ) {
        try {
            var match = matchService.scheduleMatch(request);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Match scheduled successfully", "match", match));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/updateResult")
    public ResponseEntity<?> updateMatchResult(
            @RequestBody UpdateMatchResultRequest request
    ) {
        try {
            var match = matchService.updateMatchResult(request);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Match result updated successfully", "match", match));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{tournamentId}/unscheduledMatches")
    public ResponseEntity<?> getUnscheduledMatches(
            @PathVariable Long tournamentId
    ) {
        try {
            var matches = matchService.getUnscheduledMatches(tournamentId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Unscheduled matches retrieved", "matches", matches));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{matchId}")
    public ResponseEntity<?> getMatchById(
            @PathVariable Long matchId
    ) {
        try {
            var match = matchService.getMatchById(matchId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Match retrieved successfully", "match", match));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}
