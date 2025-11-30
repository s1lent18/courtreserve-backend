package com.example.courtreserve.controller;

import com.example.courtreserve.dto.*;
import com.example.courtreserve.service.TeamService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/team")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @PostMapping("/createTeam")
    public ResponseEntity<?> createTeam(
            @RequestBody CreateTeamRequest request,
            @RequestParam Long captainId
    ) {
        try {
            var team = teamService.createTeam(captainId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Team created successfully", "team", team));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/joinTournament")
    public ResponseEntity<?> joinTournament(
            @RequestBody JoinTournamentRequest request
    ) {
        try {
            var participation = teamService.joinTournament(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Team successfully registered for tournament", "participation", participation));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/getSingleTeam")
    public ResponseEntity<?> getSingleTeam(
            @RequestParam Long Id
    ) {
        try {
            var team = teamService.getSingleTeam(Id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Single Team Returned", "singleTeam", team));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/addMember")
    public ResponseEntity<?> addTeamMember(
            @RequestBody AddTeamMemberRequest request
    ) {
        try {
            var member = teamService.addTeamMember(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Team member added successfully", "member", member));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/removeMember")
    public ResponseEntity<?> removeTeamMember(
            @RequestBody RemoveTeamMemberRequest request
    ) {
        try {
            teamService.removeTeamMember(request);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Team member removed successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/updateMember")
    public ResponseEntity<?> updateTeamMember(
            @RequestBody UpdateTeamMemberRequest request
    ) {
        try {
            var member = teamService.updateTeamMember(request);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Team member updated successfully", "member", member));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}
