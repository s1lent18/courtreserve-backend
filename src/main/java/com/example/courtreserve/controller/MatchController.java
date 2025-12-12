package com.example.courtreserve.controller;

import com.example.courtreserve.dto.ScheduleMatchRequest;
import com.example.courtreserve.dto.UpdateMatchResultRequest;
import com.example.courtreserve.service.MatchService;
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
            var bracket = matchService.getTournamentBracket(tournamentId);
            return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Tournament bracket retrieved", "bracket", bracket));
        }

        @PostMapping("/schedule")
        public ResponseEntity<?> scheduleMatch(
            @RequestBody ScheduleMatchRequest request
        ) {
            var match = matchService.scheduleMatch(request);
            return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Match scheduled successfully", "match", match));
        }

        @PatchMapping("/result")
        public ResponseEntity<?> updateMatchResult(
            @RequestBody UpdateMatchResultRequest request
        ) {
            var match = matchService.updateMatchResult(request);
            return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Match result updated successfully", "match", match));
        }

        @GetMapping("/{tournamentId}/unscheduled")
        public ResponseEntity<?> getUnscheduledMatches(
            @PathVariable Long tournamentId
        ) {
            var matches = matchService.getUnscheduledMatches(tournamentId);
            return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Unscheduled matches retrieved", "matches", matches));
        }

        @GetMapping("/{matchId}")
        public ResponseEntity<?> getMatchById(
            @PathVariable Long matchId
        ) {
            var match = matchService.getMatchById(matchId);
            return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Match retrieved successfully", "match", match));
        }
}
