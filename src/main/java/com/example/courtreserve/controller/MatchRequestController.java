package com.example.courtreserve.controller;

import com.example.courtreserve.dto.AddMatchRequestRequest;
import com.example.courtreserve.dto.MatchRequestApplicationRequest;
import com.example.courtreserve.dto.MatchRequestResponse;
import com.example.courtreserve.service.MatchRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/match-requests")
public class MatchRequestController {

    @Autowired
    private MatchRequestService matchRequestService;

    @Autowired
    private com.example.courtreserve.service.UserService userService;

    @PostMapping
    public ResponseEntity<?> createMatchRequest(
            @RequestBody AddMatchRequestRequest request,
            Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        var user = userService.findByEmail(userDetails.getUsername());

        MatchRequestResponse response = matchRequestService.createMatchRequest(request, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Match request created successfully", "matchRequest", response));
    }

    @GetMapping
    public ResponseEntity<?> getMatchRequests(
            @RequestParam(required = false) String requestType,
            @RequestParam(required = false) Long courtId,
            @RequestParam(required = false) LocalDate date) {
        List<MatchRequestResponse> requests = matchRequestService.getMatchRequests(requestType, courtId, date);
        return ResponseEntity.ok(Map.of("matchRequests", requests));
    }

    @GetMapping("/my-requests")
    public ResponseEntity<?> getMyMatchRequests(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        var user = userService.findByEmail(userDetails.getUsername());

        List<MatchRequestResponse> requests = matchRequestService.getMatchRequestsByRequester(user.getId());
        return ResponseEntity.ok(Map.of("matchRequests", requests));
    }

    @PostMapping("/{id}/apply")
    public ResponseEntity<?> applyToMatchRequest(
            @PathVariable Long id,
            @RequestBody MatchRequestApplicationRequest request,
            Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        var user = userService.findByEmail(userDetails.getUsername());

        request.setMatchRequestId(id);
        matchRequestService.applyToMatchRequest(request, user.getId());
        return ResponseEntity.ok(Map.of("message", "Application submitted successfully"));
    }

    @PostMapping("/applications/{applicationId}/respond")
    public ResponseEntity<?> respondToApplication(
            @PathVariable Long applicationId,
            @RequestParam String status,
            Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        var user = userService.findByEmail(userDetails.getUsername());

        matchRequestService.respondToApplication(applicationId, status, user.getId());
        return ResponseEntity.ok(Map.of("message", "Response recorded successfully"));
    }
}
