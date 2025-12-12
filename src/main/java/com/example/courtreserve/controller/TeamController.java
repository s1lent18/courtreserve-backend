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

    @PostMapping
    public ResponseEntity<?> createTeam(
        @RequestBody CreateTeamRequest request,
        @RequestParam Long captainId
    ) {
        var team = teamService.createTeam(captainId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                        .body(Map.of("message", "Team created successfully", "team", team));
    }

    @PostMapping("/tournament")
    public ResponseEntity<?> joinTournament(
        @RequestBody JoinTournamentRequest request
    ) {
        var participation = teamService.joinTournament(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of("message", "Team successfully registered for tournament", "participation", participation));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSingleTeam(
        @PathVariable Long id
    ) {
        var team = teamService.getSingleTeam(id);
        return ResponseEntity.status(HttpStatus.OK)
            .body(Map.of("message", "Single Team Returned", "singleTeam", team));
    }

    @PostMapping("/member")
    public ResponseEntity<?> addTeamMember(
        @RequestBody AddTeamMemberRequest request
    ) {
        var member = teamService.addTeamMember(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of("message", "Team member added successfully", "member", member));
    }

    @DeleteMapping("/member")
    public ResponseEntity<?> removeTeamMember(
        @RequestBody RemoveTeamMemberRequest request
    ) {
        teamService.removeTeamMember(request);
        return ResponseEntity.status(HttpStatus.OK)
            .body(Map.of("message", "Team member removed successfully"));
    }

    @PutMapping("/member")
    public ResponseEntity<?> updateTeamMember(
        @RequestBody UpdateTeamMemberRequest request
    ) {
        var member = teamService.updateTeamMember(request);
        return ResponseEntity.status(HttpStatus.OK)
            .body(Map.of("message", "Team member updated successfully", "member", member));
    }
}