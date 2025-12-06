package com.example.courtreserve.controller;

import com.example.courtreserve.dto.*;
import com.example.courtreserve.service.TeamService;
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
        var team = teamService.createTeam(captainId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Team created successfully", "team", team));
    }

    @PostMapping("/joinTournament")
    public ResponseEntity<?> joinTournament(
            @RequestBody JoinTournamentRequest request
    ) {
        var participation = teamService.joinTournament(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Team successfully registered for tournament", "participation", participation));
    }

    @GetMapping("/getSingleTeam")
    public ResponseEntity<?> getSingleTeam(
            @RequestParam Long Id
    ) {
        var team = teamService.getSingleTeam(Id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Single Team Returned", "singleTeam", team));
    }

    @PostMapping("/addMember")
    public ResponseEntity<?> addTeamMember(
            @RequestBody AddTeamMemberRequest request
    ) {
        var member = teamService.addTeamMember(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Team member added successfully", "member", member));
    }

    @PostMapping("/removeMember")
    public ResponseEntity<?> removeTeamMember(
            @RequestBody RemoveTeamMemberRequest request
    ) {
        teamService.removeTeamMember(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Team member removed successfully"));
    }

    @PostMapping("/updateMember")
    public ResponseEntity<?> updateTeamMember(
            @RequestBody UpdateTeamMemberRequest request
    ) {
        var member = teamService.updateTeamMember(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Team member updated successfully", "member", member));
    }
}
