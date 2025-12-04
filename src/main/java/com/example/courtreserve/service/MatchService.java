package com.example.courtreserve.service;

import com.example.courtreserve.database.models.Match;
import com.example.courtreserve.dto.*;

import java.util.List;

public interface MatchService {

    /**
     * Generate bracket for a tournament (called when tournament starts)
     */
    void generateBracket(Long tournamentId);

    /**
     * Update match result and progress winner/loser to next matches
     */
    MatchResponse updateMatchResult(UpdateMatchResultRequest request);

    /**
     * Get all matches for a tournament
     */
    GetTournamentBracketResponse getTournamentBracket(Long tournamentId);

    /**
     * Schedule match time (for manual mode)
     */
    MatchResponse scheduleMatch(ScheduleMatchRequest request);

    /**
     * Get unscheduled matches for a tournament (for manual mode)
     */
    List<GetMatchResponse> getUnscheduledMatches(Long tournamentId);

    /**
     * Get match by ID
     */
    GetMatchResponse getMatchById(Long matchId);
}

