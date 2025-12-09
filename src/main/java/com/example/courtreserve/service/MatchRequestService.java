package com.example.courtreserve.service;

import com.example.courtreserve.dto.AddMatchRequestRequest;
import com.example.courtreserve.dto.MatchRequestApplicationRequest;
import com.example.courtreserve.dto.MatchRequestResponse;

import java.time.LocalDate;
import java.util.List;

public interface MatchRequestService {


    MatchRequestResponse createMatchRequest(AddMatchRequestRequest request, Long userId);

    List<MatchRequestResponse> getMatchRequests(String requestType, Long courtId, LocalDate date);

    void applyToMatchRequest(MatchRequestApplicationRequest request, Long userId);

    void respondToApplication(Long applicationId, String status, Long userId);

    List<MatchRequestResponse> getMatchRequestsByRequester(Long userId);
}
