package com.example.courtreserve.service;

import com.example.courtreserve.database.models.Match;
import com.example.courtreserve.dto.GetMatchResponse;
import com.example.courtreserve.dto.GetTournamentBracketResponse;
import com.example.courtreserve.dto.ScheduleMatchRequest;
import com.example.courtreserve.dto.UpdateMatchResultRequest;

import java.util.List;

public interface MatchService {

    /**
     * Generate bracket for a tournament (called when tournament starts)
     */
    void generateBracket(Long tournamentId);

    /**
     * Update match result and progress winner/loser to next matches
     */
    Match updateMatchResult(UpdateMatchResultRequest request);

    /**
     * Get all matches for a tournament
     */
    GetTournamentBracketResponse getTournamentBracket(Long tournamentId);

    /**
     * Schedule match time (for manual mode)
     */
    Match scheduleMatch(ScheduleMatchRequest request);

    /**
     * Get unscheduled matches for a tournament (for manual mode)
     */
    List<GetMatchResponse> getUnscheduledMatches(Long tournamentId);

    /**
     * Get match by ID
     */
    GetMatchResponse getMatchById(Long matchId);
}

