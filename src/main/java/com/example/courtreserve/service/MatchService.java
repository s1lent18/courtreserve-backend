package com.example.courtreserve.service;

import com.example.courtreserve.database.models.Match;
import com.example.courtreserve.dto.*;

import java.util.List;

public interface MatchService {

    void generateBracket(Long tournamentId);

    MatchResponse updateMatchResult(UpdateMatchResultRequest request);

    GetTournamentBracketResponse getTournamentBracket(Long tournamentId);

    MatchResponse scheduleMatch(ScheduleMatchRequest request);

    List<GetMatchResponse> getUnscheduledMatches(Long tournamentId);

    GetMatchResponse getMatchById(Long matchId);
}

