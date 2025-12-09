package com.example.courtreserve.service.impl;

import com.example.courtreserve.database.models.*;
import com.example.courtreserve.database.repository.*;
import com.example.courtreserve.dto.AddMatchRequestRequest;
import com.example.courtreserve.dto.MatchRequestApplicationRequest;
import com.example.courtreserve.dto.MatchRequestResponse;
import com.example.courtreserve.exception.ResourceNotFoundException;
import com.example.courtreserve.service.MatchRequestService;
import com.example.courtreserve.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchRequestServiceImpl implements MatchRequestService {

    @Autowired
    private MatchRequestRepository matchRequestRepository;

    @Autowired
    private MatchRequestApplicationRepository applicationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CourtRepository courtRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Override
    public MatchRequestResponse createMatchRequest(AddMatchRequestRequest request, Long userId) {
        User requester = userService.findById(userId);
        Court court = courtRepository.findById(request.getCourtId())
                .orElseThrow(() -> new ResourceNotFoundException("Court not found"));

        Team team = null;
        if (request.getTeamId() != null) {
            team = teamRepository.findById(request.getTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        }

        MatchRequest matchRequest = MatchRequest.builder()
                .requester(requester)
                .court(court)
                .team(team)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .requestType(request.getRequestType())
                .status("OPEN")
                .message(request.getMessage())
                .created(LocalDateTime.now())
                .build();

        matchRequest = matchRequestRepository.save(matchRequest);

        return mapToResponse(matchRequest);
    }

    @Override
    public List<MatchRequestResponse> getMatchRequests(String requestType, Long courtId, LocalDate date) {
        List<MatchRequest> requests;

        if (requestType != null && courtId != null && date != null) {
            Court court = courtRepository.findById(courtId)
                    .orElseThrow(() -> new ResourceNotFoundException("Court not found"));
            requests = matchRequestRepository.findByCourtAndDateAndStatus(court, date, "OPEN")
                    .stream()
                    .filter(r -> r.getRequestType().equals(requestType))
                    .collect(Collectors.toList());
        } else if (requestType != null) {
            requests = matchRequestRepository.findByRequestType(requestType);
        } else if (courtId != null && date != null) {
            Court court = courtRepository.findById(courtId)
                    .orElseThrow(() -> new ResourceNotFoundException("Court not found"));
            requests = matchRequestRepository.findByCourtAndDate(court, date);
        } else {
            requests = matchRequestRepository.findByStatus("OPEN");
        }

        return requests.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void applyToMatchRequest(MatchRequestApplicationRequest request, Long userId) {
        MatchRequest matchRequest = matchRequestRepository.findById(request.getMatchRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Match request not found"));

        User applicant = userService.findById(userId);
        Team team = null;

        if (request.getTeamId() != null) {
            team = teamRepository.findById(request.getTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        }

        MatchRequestApplication application = MatchRequestApplication.builder()
                .matchRequest(matchRequest)
                .applicant(applicant)
                .team(team)
                .status("PENDING")
                .message(request.getMessage())
                .created(LocalDateTime.now())
                .build();

        applicationRepository.save(application);
    }

    @Override
    public void respondToApplication(Long applicationId, String status, Long userId) {
        MatchRequestApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        // Verify that the user responding is the requester of the match request
        if (!application.getMatchRequest().getRequester().getId().equals(userId)) {
            throw new RuntimeException("Only the requester can respond to applications");
        }

        application.setStatus(status);
        applicationRepository.save(application);

        // If accepted, mark the match request as fulfilled
        if ("ACCEPTED".equals(status)) {
            MatchRequest matchRequest = application.getMatchRequest();
            matchRequest.setStatus("FULFILLED");
            matchRequestRepository.save(matchRequest);
        }
    }

    @Override
    public List<MatchRequestResponse> getMatchRequestsByRequester(Long userId) {
        User requester = userService.findById(userId);
        List<MatchRequest> requests = matchRequestRepository.findByRequester(requester);

        return requests.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private MatchRequestResponse mapToResponse(MatchRequest matchRequest) {
        return MatchRequestResponse.builder()
                .id(matchRequest.getId())
                .requesterId(matchRequest.getRequester().getId())
                .requesterName(matchRequest.getRequester().getName())
                .teamId(matchRequest.getTeam() != null ? matchRequest.getTeam().getId() : null)
                .teamName(matchRequest.getTeam() != null ? matchRequest.getTeam().getName() : null)
                .courtId(matchRequest.getCourt().getId())
                .courtName(matchRequest.getCourt().getName())
                .startTime(matchRequest.getStartTime())
                .endTime(matchRequest.getEndTime())
                .status(matchRequest.getStatus())
                .requestType(matchRequest.getRequestType())
                .message(matchRequest.getMessage())
                .build();
    }
}
